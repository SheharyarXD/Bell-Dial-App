package belldial.co.uk.call;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.Account;
import org.linphone.core.MediaEncryption;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.LinphoneUtils;

public class CallStatusBarFragment extends Fragment {
    private TextView mStatusText;
    private ImageView mStatusLed, mCallQuality, mEncryption;
    private Runnable mCallQualityUpdater;
    private SimpleCoreListener mListener;
    private Dialog mZrtpDialog = null;
    private int mDisplayedQuality = -1;
    private StatsClikedListener mStatsListener;

    // -------------------------------------------------------------------------
    // Network monitoring
    // -------------------------------------------------------------------------
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    /**
     * Tracks whether at least one internet-capable validated network exists.
     * Starts as true so the LED doesn't flash red on fragment creation before
     * the first callback fires.
     */
    private boolean mIsNetworkAvailable = true;

    /**
     * FIX: How many validated networks are currently available.
     * Tracking a COUNT (not just a boolean) prevents a false "lost" state
     * during network switches (e.g. WiFi → mobile handoff mid-call).
     * The LED only goes red when this reaches zero.
     */
    private int mAvailableNetworkCount = 0;

    /**
     * FIX: Debounce delay in ms before turning the LED red after onLost.
     * During a network handoff, onLost fires before onAvailable for the
     * new network. Waiting 3 seconds prevents a red flash during handoff.
     * The LED turns GREEN immediately on onAvailable (no delay needed).
     */
    private static final long NETWORK_LOST_DEBOUNCE_MS = 3000;
    private Runnable mNetworkLostRunnable;

    // =========================================================================
    // Fragment lifecycle
    // =========================================================================

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.call_status_bar, container, false);

        mStatusText  = view.findViewById(R.id.status_text);
        mStatusLed   = view.findViewById(R.id.status_led);
        mCallQuality = view.findViewById(R.id.call_quality);
        mEncryption  = view.findViewById(R.id.encryption);

        mStatsListener = null;

        mCallQuality.setOnClickListener(v -> {
            if (mStatsListener != null) {
                mStatsListener.onStatsClicked();
            }
        });

        mListener = new SimpleCoreListener() {

            @Override
            public void onAccountRegistrationStateChanged(
                    Core core, Account account, RegistrationState state, String message) {

                if (core.getAccountList() == null || core.getAccountList().length == 0) {
                    mStatusText.setText(getString(R.string.no_account));
                    mStatusLed.setVisibility(View.VISIBLE);
                } else {
                    mStatusLed.setVisibility(View.VISIBLE);
                }

                Account defaultAccount = core.getDefaultAccount();
                if (defaultAccount != null && defaultAccount.equals(account)) {
                    mStatusText.setText(getStatusText(state));
                } else if (defaultAccount == null) {
                    mStatusText.setText(getStatusText(state));
                }

                try {
                    mStatusText.setOnClickListener(v -> {
                        Core c = LinphoneManager.getCore();
                        if (c != null) {
                            c.refreshRegisters();
                        }
                    });
                } catch (IllegalStateException ise) {
                    Log.e(ise);
                }

                // LED is NOT driven by SIP registration state — network only.
            }

            @Override
            public void onCallStateChanged(
                    Core core, Call call, Call.State state, String message) {
                if (state == Call.State.Resuming || state == Call.State.StreamsRunning) {
                    refreshStatusItems(call);
                }
                // LED is NOT touched here.
            }

            @Override
            public void onCallEncryptionChanged(
                    Core core, Call call, boolean on, String authenticationToken) {
                if (call.getCurrentParams()
                        .getMediaEncryption()
                        .equals(MediaEncryption.ZRTP)
                        && !call.getAuthenticationTokenVerified()) {
                    showZRTPDialog(call);
                }
                refreshStatusItems(call);
                // LED is NOT touched here.
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Initialise network count from current state before registering callback.
        mAvailableNetworkCount = countAvailableNetworksNow();
        mIsNetworkAvailable    = (mAvailableNetworkCount > 0);

        // Apply LED immediately — no red flash on screen rotation / resume.
        updateLedForNetwork(mIsNetworkAvailable);

        startNetworkCallback();

        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);

            Account lpc = core.getDefaultAccount();
            if (lpc != null) {
                mListener.onAccountRegistrationStateChanged(core, lpc, lpc.getState(), null);
            }

            Call call = core.getCurrentCall();
            if (call != null) {
                startCallQuality();
                refreshStatusItems(call);
                if (!call.getAuthenticationTokenVerified()) {
                    showZRTPDialog(call);
                }
            }

            if (core.getDefaultAccount() == null) {
                mStatusText.setText(getString(R.string.no_account));
            }
        } else {
            mStatusText.setVisibility(View.VISIBLE);
            mEncryption.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }

        if (mCallQualityUpdater != null) {
            LinphoneUtils.removeFromUIThreadDispatcher(mCallQualityUpdater);
            mCallQualityUpdater = null;
        }

        // FIX: cancel any pending "go red" runnable to avoid stale UI updates
        cancelNetworkLostDebounce();

        stopNetworkCallback();
    }

    // =========================================================================
    // Network connectivity — LED logic
    // =========================================================================

    private void startNetworkCallback() {
        if (getActivity() == null) return;

        mConnectivityManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mConnectivityManager == null) return;

        NetworkRequest.Builder builder = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // NET_CAPABILITY_VALIDATED: confirmed working internet (no captive portal).
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }

        mNetworkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                // A good network appeared — cancel any pending "go red" timer
                // and turn green immediately.
                mAvailableNetworkCount++;
                mIsNetworkAvailable = true;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // FIX: cancel debounce — new network arrived before timer fired.
                        cancelNetworkLostDebounce();
                        updateLedForNetwork(true);
                    });
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                // A network was lost. Decrement, but don't go red immediately.
                // Another network (mobile data) may take over within ~1–2 seconds.
                // FIX: schedule a debounced check so handoffs don't flash red.
                mAvailableNetworkCount = Math.max(0, mAvailableNetworkCount - 1);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> scheduleNetworkLostCheck());
                }
            }

            @Override
            public void onUnavailable() {
                // Request timed out — no suitable network found at all.
                mAvailableNetworkCount = 0;
                mIsNetworkAvailable    = false;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        cancelNetworkLostDebounce();
                        updateLedForNetwork(false);
                    });
                }
            }
        };

        try {
            mConnectivityManager.registerNetworkCallback(builder.build(), mNetworkCallback);
        } catch (Exception e) {
            Log.e("[Status Fragment] Failed to register network callback: " + e.getMessage());
        }
    }

    private void stopNetworkCallback() {
        if (mConnectivityManager != null && mNetworkCallback != null) {
            try {
                mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            } catch (Exception e) {
                Log.e("[Status Fragment] Failed to unregister network callback: " + e.getMessage());
            }
            mNetworkCallback = null;
        }
    }

    /**
     * FIX: Schedules a delayed check before turning the LED red.
     *
     * During a WiFi → mobile handoff:
     *   t=0ms   onLost(wifi)    → we schedule this runnable
     *   t=800ms onAvailable(mobile) → cancelNetworkLostDebounce() fires, no red
     *
     * During genuine total loss:
     *   t=0ms   onLost(wifi)    → we schedule this runnable
     *   t=3000ms (no onAvailable) → runnable fires, LED goes red
     */
    private void scheduleNetworkLostCheck() {
        cancelNetworkLostDebounce(); // reset any prior pending runnable

        mNetworkLostRunnable = () -> {
            // Re-check synchronously — another network may now be active.
            int count = countAvailableNetworksNow();
            mAvailableNetworkCount = count;
            mIsNetworkAvailable    = (count > 0);
            updateLedForNetwork(mIsNetworkAvailable);
        };

        if (getActivity() != null) {
            getActivity().getWindow().getDecorView()
                    .postDelayed(mNetworkLostRunnable, NETWORK_LOST_DEBOUNCE_MS);
        }
    }

    /** Cancels a pending network-lost runnable if one is scheduled. */
    private void cancelNetworkLostDebounce() {
        if (mNetworkLostRunnable != null && getActivity() != null) {
            getActivity().getWindow().getDecorView()
                    .removeCallbacks(mNetworkLostRunnable);
            mNetworkLostRunnable = null;
        }
    }

    /**
     * FIX: Returns the count of currently active validated networks.
     * Counting (instead of a boolean) correctly handles the case where
     * WiFi + mobile are both active simultaneously.
     */
    private int countAvailableNetworksNow() {
        if (mConnectivityManager == null) {
            if (getActivity() == null) return 0;
            mConnectivityManager = (ConnectivityManager)
                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (mConnectivityManager == null) return 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network[] networks = mConnectivityManager.getAllNetworks();
            if (networks == null) return 0;
            int count = 0;
            for (Network net : networks) {
                NetworkCapabilities caps = mConnectivityManager.getNetworkCapabilities(net);
                if (caps != null
                        && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    count++;
                }
            }
            return count;
        } else {
            // API 21–22 fallback: can only detect one network at a time
            android.net.NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
            return (info != null && info.isConnected()) ? 1 : 0;
        }
    }

    /**
     * Updates the LED drawable based solely on internet availability.
     * 🟢 led_connected    — internet reachable
     * 🔴 led_disconnected — no internet at all
     *
     * This is the ONLY place the LED resource is set.
     */
    private void updateLedForNetwork(boolean networkAvailable) {
        if (mStatusLed == null) return;
        mStatusLed.setImageResource(
                networkAvailable ? R.drawable.led_connected : R.drawable.led_disconnected);
    }

    // =========================================================================
    // SIP registration text helpers (LED-free — unchanged)
    // =========================================================================

    private String getStatusText(RegistrationState state) {
        Context context = getActivity();
        if (context == null) return "";
        try {
            if (state == RegistrationState.Ok
                    && LinphoneManager.getCore() != null
                    && LinphoneManager.getCore().getDefaultAccount() != null
                    && LinphoneManager.getCore().getDefaultAccount().getState()
                            == RegistrationState.Ok) {
                return context.getString(R.string.status_connected);
            } else if (state == RegistrationState.Progress) {
                return context.getString(R.string.status_in_progress);
            } else if (state == RegistrationState.Failed) {
                return context.getString(R.string.status_error);
            } else {
                return context.getString(R.string.status_not_connected);
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return context.getString(R.string.status_not_connected);
    }

    // =========================================================================
    // Public surface
    // =========================================================================

    public void setStatsListener(StatsClikedListener listener) {
        mStatsListener = listener;
    }

    // =========================================================================
    // Call quality polling (unchanged)
    // =========================================================================

    private void startCallQuality() {
        LinphoneUtils.dispatchOnUIThreadAfter(
                mCallQualityUpdater = new Runnable() {
                    final Call mCurrentCall = LinphoneManager.getCore().getCurrentCall();

                    public void run() {
                        if (mCurrentCall == null) {
                            mCallQualityUpdater = null;
                            return;
                        }
                        float newQuality = mCurrentCall.getCurrentQuality();
                        updateQualityOfSignalIcon(newQuality);
                        LinphoneUtils.dispatchOnUIThreadAfter(this, 1000);
                    }
                },
                1000);
    }

    private void updateQualityOfSignalIcon(float quality) {
        int iQuality = (int) quality;
        if (iQuality == mDisplayedQuality) return;

        if      (quality >= 4) mCallQuality.setImageResource(R.drawable.call_quality_indicator_4);
        else if (quality >= 3) mCallQuality.setImageResource(R.drawable.call_quality_indicator_3);
        else if (quality >= 2) mCallQuality.setImageResource(R.drawable.call_quality_indicator_2);
        else if (quality >= 1) mCallQuality.setImageResource(R.drawable.call_quality_indicator_1);
        else                   mCallQuality.setImageResource(R.drawable.call_quality_indicator_0);

        mDisplayedQuality = iQuality;
    }

    // =========================================================================
    // Encryption icon (unchanged)
    // =========================================================================

    public void refreshStatusItems(final Call call) {
        if (call == null) return;

        MediaEncryption mediaEncryption = call.getCurrentParams().getMediaEncryption();
        mEncryption.setVisibility(View.VISIBLE);

        if (mediaEncryption == MediaEncryption.SRTP
                || (mediaEncryption == MediaEncryption.ZRTP
                        && call.getAuthenticationTokenVerified())
                || mediaEncryption == MediaEncryption.DTLS) {
            mEncryption.setImageResource(R.drawable.security_ok);
        } else if (mediaEncryption == MediaEncryption.ZRTP
                && !call.getAuthenticationTokenVerified()) {
            mEncryption.setImageResource(R.drawable.security_pending);
        } else {
            mEncryption.setImageResource(R.drawable.security_ko);
            if (LinphonePreferences.instance().getMediaEncryption() == MediaEncryption.None) {
                mEncryption.setVisibility(View.GONE);
            }
        }

        if (mediaEncryption == MediaEncryption.ZRTP) {
            mEncryption.setOnClickListener(v -> showZRTPDialog(call));
        } else {
            mEncryption.setOnClickListener(null);
        }
    }

    // =========================================================================
    // ZRTP dialog (unchanged)
    // =========================================================================

    public void showZRTPDialog(final Call call) {
        if (getActivity() == null) {
            Log.w("[Status Fragment] Can't display ZRTP popup, no Activity");
            return;
        }

        if (mZrtpDialog != null && mZrtpDialog.isShowing()) return;

        String token = call.getAuthenticationToken();
        if (token == null) {
            Log.w("[Status Fragment] Can't display ZRTP popup, no token !");
            return;
        }
        if (token.length() < 4) {
            Log.w("[Status Fragment] Can't display ZRTP popup, token is invalid (" + token + ")");
            return;
        }

        mZrtpDialog = new Dialog(getActivity());
        mZrtpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mZrtpDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        mZrtpDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        mZrtpDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Drawable d = new ColorDrawable(
                ContextCompat.getColor(getActivity(), R.color.dark_grey_color));
        d.setAlpha(200);
        mZrtpDialog.setContentView(R.layout.dialog);
        mZrtpDialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mZrtpDialog.getWindow().setBackgroundDrawable(d);

        String zrtpToRead, zrtpToListen;
        if (call.getDir().equals(Call.Dir.Incoming)) {
            zrtpToRead   = token.substring(0, 2);
            zrtpToListen = token.substring(2);
        } else {
            zrtpToListen = token.substring(0, 2);
            zrtpToRead   = token.substring(2);
        }

        TextView localSas  = mZrtpDialog.findViewById(R.id.zrtp_sas_local);
        localSas.setText(zrtpToRead.toUpperCase());
        TextView remoteSas = mZrtpDialog.findViewById(R.id.zrtp_sas_remote);
        remoteSas.setText(zrtpToListen.toUpperCase());

        TextView message = mZrtpDialog.findViewById(R.id.dialog_message);
        message.setVisibility(View.GONE);
        mZrtpDialog.findViewById(R.id.dialog_zrtp_layout).setVisibility(View.VISIBLE);

        TextView title = mZrtpDialog.findViewById(R.id.dialog_title);
        title.setText(getString(R.string.zrtp_dialog_title));
        title.setVisibility(View.VISIBLE);

        Button delete = mZrtpDialog.findViewById(R.id.dialog_delete_button);
        delete.setText(R.string.deny);
        Button cancel = mZrtpDialog.findViewById(R.id.dialog_cancel_button);
        cancel.setVisibility(View.GONE);
        Button accept = mZrtpDialog.findViewById(R.id.dialog_ok_button);
        accept.setVisibility(View.VISIBLE);
        accept.setText(R.string.accept);

        ImageView icon = mZrtpDialog.findViewById(R.id.dialog_icon);
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(R.drawable.security_2_indicator);

        delete.setOnClickListener(v -> {
            LinphoneManager.getInstance().lastCallSasRejected(true);
            call.setAuthenticationTokenVerified(false);
            if (mEncryption != null) {
                mEncryption.setImageResource(R.drawable.security_ko);
            }
            mZrtpDialog.dismiss();
        });

        accept.setOnClickListener(v -> {
            call.setAuthenticationTokenVerified(true);
            if (mEncryption != null) {
                mEncryption.setImageResource(R.drawable.security_ok);
            }
            mZrtpDialog.dismiss();
        });

        mZrtpDialog.show();
    }

    // =========================================================================
    // Interface
    // =========================================================================

    public interface StatsClikedListener {
        void onStatsClicked();
    }
}