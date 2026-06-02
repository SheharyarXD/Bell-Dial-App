package belldial.co.uk.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.Event;
import org.linphone.core.Account;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;

public class StatusBarFragment extends Fragment {
    private TextView mStatusText, mVoicemailCount;
    private ImageView mStatusLed;
    private ImageView mVoicemail;
    private SimpleCoreListener mListener;
    private MenuClikedListener mMenuListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_bar, container, false);

        mStatusText = view.findViewById(R.id.status_text);
        mStatusLed = view.findViewById(R.id.status_led);
        ImageView menu = view.findViewById(R.id.side_menu_button);
        mVoicemail = view.findViewById(R.id.voicemail);
        mVoicemailCount = view.findViewById(R.id.voicemail_count);

        mMenuListener = null;
        menu.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMenuListener != null) {
                            mMenuListener.onMenuCliked();
                        }
                    }
                });

        populateSliderContent();

        mListener = new SimpleCoreListener() {
            @Override
            public void onAccountRegistrationStateChanged(
                    Core core, Account account, RegistrationState state, String message) {

                // ✅ Skip SIP registration icon update during active call
                if (core != null && core.getCallsNb() > 0) {
                    return;
                }

                if (core.getAccountList() == null) {
                    mStatusLed.setImageResource(R.drawable.led_disconnected);
                    mStatusText.setText(getString(R.string.no_account));
                } else {
                    mStatusLed.setVisibility(View.VISIBLE);
                }

                if (core.getDefaultAccount() != null
                        && core.getDefaultAccount().equals(account)
                        || core.getDefaultAccount() == null) {
                    mStatusLed.setImageResource(getStatusIconResource(state));
                    mStatusText.setText(getStatusIconText(state));
                }

                try {
                    mStatusText.setOnClickListener(
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Core core = LinphoneManager.getCore();
                                    if (core != null) {
                                        core.refreshRegisters();
                                    }
                                }
                            });
                } catch (IllegalStateException ise) {
                    Log.e(ise);
                }
            }

            @Override
            public void onNotifyReceived(
                    Core core, Event ev, String eventName, Content content) {
                if (!content.getType().equals("application"))
                    return;
                if (!content.getSubtype().equals("simple-message-summary"))
                    return;
                if (content.getSize() == 0)
                    return;

                int unreadCount = 0;
                String data = content.getStringBuffer().toLowerCase();
                String[] voiceMail = data.split("voice-message: ");
                if (voiceMail.length >= 2) {
                    final String[] intToParse = voiceMail[1].split("/", 0);
                    try {
                        unreadCount = Integer.parseInt(intToParse[0]);
                    } catch (NumberFormatException nfe) {
                        Log.e("[Status Fragment] " + nfe);
                    }
                    if (unreadCount > 0) {
                        mVoicemailCount.setText(String.valueOf(unreadCount));
                        mVoicemail.setVisibility(View.VISIBLE);
                        mVoicemailCount.setVisibility(View.VISIBLE);
                    } else {
                        mVoicemail.setVisibility(View.GONE);
                        mVoicemailCount.setVisibility(View.GONE);
                    }
                }
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
            // ✅ Only trigger registration icon if no active call
            if (core.getCallsNb() == 0) {
                Account lpc = core.getDefaultAccount();
                if (lpc != null) {
                    mListener.onAccountRegistrationStateChanged(core, lpc, lpc.getState(), null);
                }
            }
        } else {
            mStatusText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
    }

    public void setMenuListener(MenuClikedListener listener) {
        mMenuListener = listener;
    }

    private void populateSliderContent() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            mVoicemailCount.setVisibility(View.VISIBLE);
            if (core.getAccountList().length == 0) {
                mStatusLed.setImageResource(R.drawable.led_disconnected);
                mStatusText.setText(getString(R.string.no_account));
            }
        }
    }

    private int getStatusIconResource(RegistrationState state) {
        try {
            if (state == RegistrationState.Ok) {
                return R.drawable.led_connected;
            } else if (state == RegistrationState.Progress) {
                return R.drawable.led_inprogress;
            } else if (state == RegistrationState.Failed) {
                return R.drawable.led_error;
            } else {
                return R.drawable.led_disconnected;
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return R.drawable.led_disconnected;
    }

    private String getStatusIconText(RegistrationState state) {
        Context context = getActivity();
        try {
            if (state == RegistrationState.Ok) {
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

    public interface MenuClikedListener {
        void onMenuCliked();
    }
}