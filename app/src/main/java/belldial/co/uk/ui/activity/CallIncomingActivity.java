package belldial.co.uk.ui.activity;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import org.linphone.core.CallStats;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.Account;
// import org.linphone.core.CoreListener;
import org.linphone.core.RegistrationState;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.tools.Log;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.R;
import belldial.co.uk.compatibility.Compatibility;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import belldial.co.uk.ui.provider.AppNavigationProvider;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.utils.LinphoneUtils;
import belldial.co.uk.utils.notifications.CustomRingtoneManager;
import belldial.co.uk.views.ContactAvatar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CallIncomingActivity extends AppNavigationProvider {

    @BindView(R.id.toolbar_back_btn)
    Toolbar toolbar;

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.textViewCallerName)
    AppCompatTextView textViewCallerName;

    @BindView(R.id.imageViewUser)
    AppCompatImageView imageViewUser;

    @BindView(R.id.imageViewCallHangup)
    AppCompatImageView imageViewCallHangup;

    @BindView(R.id.imageViewAnswer)
    AppCompatImageView imageViewAnswer;

    private Call mCall;
    private SimpleCoreListener mListener;
    private boolean mAlreadyAcceptedOrDeniedCall;

    @Override
    public int getPlaceHolder() {
        return R.id.placeHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
            wakeLock.acquire();
        } else {

            Compatibility.setShowWhenLocked(this, true);
            Compatibility.setTurnScreenOn(this, true);

            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }
        setContentView(R.layout.fragment_incoming_call);

        ButterKnife.bind(this);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        toolBarTitle.setText("");
        String from = getIntent().getStringExtra("from");
        lookupCurrentCall();

       mListener = new SimpleCoreListener() {

    @Override
    public void onAccountRegistrationStateChanged(
            final Core lc,
            final Account account,
            final RegistrationState state,
            String smessage) {
        if (!LinphoneService.isReady()) return;

        // ✅ Skip if call is active — don't let SIP state overwrite network icon
        if (lc != null && lc.getCallsNb() > 0) return;

        if (lc.getAccountList() == null) {
            imageViewStatus.setImageResource(R.drawable.signal_null);
            return;
        }

        if (lc.getDefaultAccount() != null && lc.getDefaultAccount().equals(account)) {
            imageViewStatus.setImageResource(Constant.getStatusIconResource(state, true));
        } else if (lc.getDefaultAccount() == null) {
            imageViewStatus.setImageResource(Constant.getStatusIconResource(state, true));
        }
    }

    // ✅ Add this — fires during active call
    @Override
    public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
        if (call != null) {
            imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
        }
    }

    @Override
    public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
        if (call == mCall) {
            if (state == Call.State.Connected) {
                // connected
            }
        }
        if (LinphoneManager.getCore().getCallsNb() == 0) {
            finish();
        }
    }
};
    }

    @Override
    protected void onResume() {
        Log.e("CallIncoming", "CallIncoming onResume");
        super.onResume();
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
            Account lpc = core.getDefaultAccount();
            if (lpc != null) {
                mListener.onAccountRegistrationStateChanged(core, lpc, lpc.getState(), null);
            }
        }

        mAlreadyAcceptedOrDeniedCall = false;
        mCall = null;

        // Only one call ringing at a time is allowed
        lookupCurrentCall();
        if (mCall == null) {
            // The incoming call no longer exists.
            Log.d("Couldn't find incoming call");
            finish();
            return;
        }

        Address address = mCall.getRemoteAddress();
        LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(address);
        if (contact != null) {
            ContactAvatar.displayAvatar(contact, findViewById(R.id.avatar_layout), true);
            textViewCallerName.setText(contact.getFullName());
        } else {
            String displayName = LinphoneUtils.getAddressDisplayName(address);
            ContactAvatar.displayAvatar(displayName, findViewById(R.id.avatar_layout), true);
            textViewCallerName.setText(displayName);
        }
    }

    @Override
    protected void onPause() {
        Log.e("CallIncoming", "CallIncoming onPause");
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }

    private void lookupCurrentCall() {
        if (LinphoneManager.getCore() != null) {
            for (Call call : LinphoneManager.getCore().getCalls()) {
                if (Call.State.IncomingReceived == call.getState()
                        || Call.State.IncomingEarlyMedia == call.getState()) {
                    mCall = call;
                    break;
                }
            }
        }
    }

    @OnClick({ R.id.imageViewCallHangup, R.id.imageViewAnswer })
    public void onViewClicked(View view) {
        CustomRingtoneManager audioManager = CustomRingtoneManager.getInstance(CallIncomingActivity.this);
        audioManager.stopRingtone();
        switch (view.getId()) {
            case R.id.imageViewCallHangup:
                decline();
                break;
            case R.id.imageViewAnswer:
                answer();
                break;
        }
    }

    private void decline() {
        if (mAlreadyAcceptedOrDeniedCall) {
            return;
        }
        mAlreadyAcceptedOrDeniedCall = true;

        mCall.terminate();
    }

    private void answer() {

        if (mAlreadyAcceptedOrDeniedCall) {
            return;
        }
        mAlreadyAcceptedOrDeniedCall = true;

        if (!LinphoneManager.getCallManager().acceptCall(mCall)) {
            // the above method takes care of Samsung Galaxy S
            Toast.makeText(this, R.string.couldnt_accept_call, Toast.LENGTH_LONG).show();
        }
    }
}
