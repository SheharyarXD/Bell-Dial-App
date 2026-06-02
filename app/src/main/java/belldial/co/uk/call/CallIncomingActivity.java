package belldial.co.uk.call;

/*
CallIncomingActivity.java
Copyright (C) 2017 Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Objects;

import belldial.co.uk.LinphoneContext;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import belldial.co.uk.activities.LinphoneGenericActivity;
import belldial.co.uk.compatibility.Compatibility;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Call.State;
import org.linphone.core.Core;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.LinphoneUtils;
import belldial.co.uk.views.CallIncomingAnswerButton;
import belldial.co.uk.views.CallIncomingButtonListener;
import belldial.co.uk.views.CallIncomingDeclineButton;
import belldial.co.uk.views.ContactAvatar;
import belldial.co.uk.utils.AndroidAudioManager;

public class CallIncomingActivity extends LinphoneGenericActivity {
    private TextView mName, mNumber;
    private Core mCore;
    private Call mCall;
    private SimpleCoreListener mListener;
    private AndroidAudioManager mAudioManager;
    private boolean mAlreadyAcceptedOrDeniedCall;
    private TextureView mVideoDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Compatibility.setShowWhenLocked(this, true);
        Compatibility.setTurnScreenOn(this, true);

        setContentView(R.layout.call_incoming);

        mName = findViewById(R.id.contact_name);
        mNumber = findViewById(R.id.contact_number);
        mVideoDisplay = findViewById(R.id.videoSurface);

        CallIncomingAnswerButton mAccept = findViewById(R.id.answer_button);
        CallIncomingDeclineButton mDecline = findViewById(R.id.decline_button);
        ImageView mAcceptIcon = findViewById(R.id.acceptIcon);
        lookupCurrentCall();

        if (LinphonePreferences.instance() != null
                && mCall != null
                && mCall.getRemoteParams() != null
                && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()
                && mCall.getRemoteParams().isVideoEnabled()) {
            mAcceptIcon.setImageResource(R.drawable.call_video_start);
        }

        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean doNotUseSliders = getResources()
                .getBoolean(
                        R.bool.do_not_use_sliders_to_answer_hangup_call_if_phone_unlocked);
        if (doNotUseSliders && !mKeyguardManager.inKeyguardRestrictedInputMode()) {
            mAccept.setSliderMode(false);
            mDecline.setSliderMode(false);
        } else {
            mAccept.setSliderMode(true);
            mDecline.setSliderMode(true);
            mAccept.setDeclineButton(mDecline);
            mDecline.setAnswerButton(mAccept);
        }
        mAccept.setListener(this::answer);
        mDecline.setListener(this::decline);

        mListener = new SimpleCoreListener() {
            public void onCallStateChanged(
                    Core core, Call call, Call.State state, String message) {
                if (call == mCall) {
                    if (state == State.Connected) {
                        // This is done by the Service listener now
                        // startActivity(new Intent(CallOutgoingActivity.this,
                        // CallActivity.class));
                    }
                }

                if (Objects.requireNonNull(LinphoneManager.getCore()).getCallsNb() == 0) {
                    finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestCallPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        android.util.Log.d("incall", "onResume: ");

        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
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
            mName.setText(contact.getFullName());
        } else {
            String displayName = LinphoneUtils.getAddressDisplayName(address);
            ContactAvatar.displayAvatar(displayName, findViewById(R.id.avatar_layout), true);
            mName.setText(displayName);
        }
        mNumber.setText(address.asStringUriOnly());

        if (LinphonePreferences.instance().acceptIncomingEarlyMedia()) {
            if (mCall.getCurrentParams().isVideoEnabled()) {
                findViewById(R.id.avatar_layout).setVisibility(View.GONE);
                mCall.getCore().setNativeVideoWindowId(mVideoDisplay);
            }
        }
    }

    @Override
    protected void onPause() {

        android.util.Log.d("incall", "onPause: ");

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
        mCall = null;
        mListener = null;
        mVideoDisplay = null;

        super.onDestroy();
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

    private void lookupCurrentCall() {
        if (LinphoneManager.getCore() != null) {
            for (Call call : LinphoneManager.getCore().getCalls()) {
                if (State.IncomingReceived == call.getState()
                        || State.IncomingEarlyMedia == call.getState()) {
                    mCall = call;
                    break;
                }
            }
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

    private void checkAndRequestCallPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();

        int recordAudio = getPackageManager()
                .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        Log.i(
                "[Permission] Record audio permission is "
                        + (recordAudio == PackageManager.PERMISSION_GRANTED
                                ? "granted"
                                : "denied"));
        int camera = getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName());
        Log.i(
                "[Permission] Camera permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        int readPhoneState = getPackageManager()
                .checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName());
        Log.i(
                "[Permission] Read phone state permission is "
                        + (camera == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));

        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for record audio");
            permissionsList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            Log.i("[Permission] Asking for read phone state");
            permissionsList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (LinphonePreferences.instance().shouldInitiateVideoCall()
                || LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
            if (camera != PackageManager.PERMISSION_GRANTED) {
                Log.i("[Permission] Asking for camera");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            Log.i(
                    "[Permission] "
                            + permissions[i]
                            + " is "
                            + (grantResults[i] == PackageManager.PERMISSION_GRANTED
                                    ? "granted"
                                    : "denied"));
            if (permissions[i].equals(Manifest.permission.CAMERA)
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                LinphoneUtils.reloadVideoDevices();
            }
        }
    }
}
