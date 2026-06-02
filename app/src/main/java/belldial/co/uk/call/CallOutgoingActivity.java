package belldial.co.uk.call;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import belldial.co.uk.LinphoneContext;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import belldial.co.uk.activities.LinphoneGenericActivity;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import belldial.co.uk.utils.Constant;
import org.linphone.core.Account;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import org.linphone.core.RegistrationState;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.Reason;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.LinphoneUtils;
import belldial.co.uk.views.ContactAvatar;

public class CallOutgoingActivity extends LinphoneGenericActivity implements OnClickListener {
    private TextView mName, mNumber;
    private ImageView mMicro;
    private ImageView mSpeaker;
    private ImageView mImageViewStatus;
    private Core mCore;
    private SimpleCoreListener mListener;

    private Call mCall;
    private boolean mIsMicMuted, mIsSpeakerEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.call_outgoing);

        mName = findViewById(R.id.contact_name);
        mNumber = findViewById(R.id.contact_number);

        // Bind status icon if it exists in the layout
        mImageViewStatus = findViewById(R.id.imageViewStatus);

        mIsMicMuted = false;
        mIsSpeakerEnabled = false;

        mMicro = findViewById(R.id.micro);
        mMicro.setOnClickListener(this);
        mSpeaker = findViewById(R.id.speaker);
        mSpeaker.setOnClickListener(this);

        ImageView hangUp = findViewById(R.id.outgoing_hang_up);
        hangUp.setOnClickListener(this);

        mListener = new SimpleCoreListener() {

            @Override
            public void onAccountRegistrationStateChanged(
                    final Core lc,
                    final Account account,
                    final RegistrationState state,
                    String smessage) {
                // ✅ Skip SIP registration icon update during active call
                // so re-registration events don't flash red while audio is fine
                if (lc != null && lc.getCallsNb() > 0) {
                    return;
                }
                updateStatusIcon(lc, state);
            }

            // ✅ Fires every second during call — show real network signal
            @Override
            public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
                if (call != null) {
                    setStatusIcon(Constant.getNetworkSignalIcon());
                }
            }

            @Override
            public void onCallStateChanged(
                    Core core, Call call, Call.State state, String message) {
                if (state == State.Error) {
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                        Toast.makeText(
                                CallOutgoingActivity.this,
                                getString(R.string.error_call_declined),
                                Toast.LENGTH_SHORT).show();
                    } else if (call.getErrorInfo().getReason() == Reason.NotFound) {
                        Toast.makeText(
                                CallOutgoingActivity.this,
                                getString(R.string.error_user_not_found),
                                Toast.LENGTH_SHORT).show();
                    } else if (call.getErrorInfo().getReason() == Reason.NotAcceptable) {
                        Toast.makeText(
                                CallOutgoingActivity.this,
                                getString(R.string.error_incompatible_media),
                                Toast.LENGTH_SHORT).show();
                    } else if (call.getErrorInfo().getReason() == Reason.Busy) {
                        Toast.makeText(
                                CallOutgoingActivity.this,
                                getString(R.string.error_user_busy),
                                Toast.LENGTH_SHORT).show();
                    } else if (message != null) {
                        Toast.makeText(
                                CallOutgoingActivity.this,
                                getString(R.string.error_unknown) + " - " + message,
                                Toast.LENGTH_SHORT).show();
                    }
                } else if (state == State.End) {
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                        Toast.makeText(
                                CallOutgoingActivity.this,
                                getString(R.string.error_call_declined),
                                Toast.LENGTH_SHORT).show();
                    }
                    // Call ended → restore registration-based icon
                    Account account = core.getDefaultAccount();
                    if (account != null) {
                        updateStatusIcon(core, account.getState());
                    }
                } else if (state == State.Connected) {
                    // ✅ Call connected → switch to network-based icon
                    setStatusIcon(Constant.getNetworkSignalIcon());
                } else if (state == State.OutgoingProgress
                        || state == State.OutgoingRinging
                        || state == State.OutgoingInit) {
                    // ✅ Still dialing → show network icon, not SIP state
                    setStatusIcon(Constant.getNetworkSignalIcon());
                }

                if (LinphoneManager.getCore().getCallsNb() == 0) {
                    finish();
                }
            }
        };
    }

    // Helper to safely set icon (imageViewStatus may not exist in all layouts)
    private void setStatusIcon(int resId) {
        if (mImageViewStatus != null) {
            mImageViewStatus.setImageResource(resId);
        }
    }

    // Helper to set registration-based icon
    private void updateStatusIcon(Core lc, RegistrationState state) {
        if (lc == null) return;
        if (lc.getAccountList() == null || lc.getAccountList().length == 0) {
            setStatusIcon(R.drawable.signal_null);
            return;
        }
        setStatusIcon(Constant.getStatusIconResource(state, true));
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestCallPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);

            // ✅ On resume — if call active show network icon, else registration icon
            if (core.getCallsNb() > 0) {
                setStatusIcon(Constant.getNetworkSignalIcon());
            } else {
                Account account = core.getDefaultAccount();
                if (account != null) {
                    updateStatusIcon(core, account.getState());
                }
            }
        }

        mCall = null;

        if (LinphoneManager.getCore() != null) {
            for (Call call : LinphoneManager.getCore().getCalls()) {
                State cstate = call.getState();
                if (State.OutgoingInit == cstate
                        || State.OutgoingProgress == cstate
                        || State.OutgoingRinging == cstate
                        || State.OutgoingEarlyMedia == cstate) {
                    mCall = call;
                    break;
                }
            }
        }
        if (mCall == null) {
            Log.e("Couldn't find outgoing call");
            finish();
            return;
        }

        Address address = mCall.getRemoteAddress();
        LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(address);
        if (contact != null) {
            ContactAvatar.displayAvatar(contact, findViewById(R.id.avatar_layout), true);
            mName.setText(contact.getFullName());
        } else {
            String displayName = LinphoneUtils.getAddressDisplayName(address);
            ContactAvatar.displayAvatar(displayName, findViewById(R.id.avatar_layout), true);
            mName.setText(displayName);
        }
        mNumber.setText(LinphoneUtils.getDisplayableAddress(address));
    }

    @Override
    protected void onPause() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mName = null;
        mNumber = null;
        mMicro = null;
        mSpeaker = null;
        mImageViewStatus = null;
        mCall = null;
        mListener = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.micro) {
            mIsMicMuted = !mIsMicMuted;
            mMicro.setSelected(mIsMicMuted);
            LinphoneManager.getCore().setMicEnabled(!mIsMicMuted);
        }
        if (id == R.id.speaker) {
            mIsSpeakerEnabled = !mIsSpeakerEnabled;
            mSpeaker.setSelected(mIsSpeakerEnabled);
            if (mIsSpeakerEnabled) {
                LinphoneManager.getAudioManager().routeAudioToSpeaker();
            } else {
                LinphoneManager.getAudioManager().routeAudioToEarPiece();
            }
        }
        if (id == R.id.outgoing_hang_up) {
            decline();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (LinphoneContext.isReady()
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            mCall.terminate();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decline() {
        mCall.terminate();
        finish();
    }

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        int recordAudio = getPackageManager()
                .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i("[Permission] Record audio permission is "
                + (recordAudio == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        int camera = getPackageManager()
                .checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i("[Permission] Camera permission is "
                + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        int readPhoneState = getPackageManager()
                .checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName());
        Log.i("[Permission] Read phone state permission is "
                + (readPhoneState == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (LinphonePreferences.instance().shouldInitiateVideoCall()
                || LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
            if (camera != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.CAMERA);
            }
        }

        if (permissionsList.size() > 0) {
            String[] permissions = new String[permissionsList.size()];
            permissions = permissionsList.toArray(permissions);
            ActivityCompat.requestPermissions(this, permissions, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            Log.i("[Permission] " + permissions[i] + " is "
                    + (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
            if (permissions[i].equals(Manifest.permission.CAMERA)
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                LinphoneUtils.reloadVideoDevices();
            }
        }
    }
}