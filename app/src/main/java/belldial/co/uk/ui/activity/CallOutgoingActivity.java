package belldial.co.uk.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.ui.provider.AppNavigationProvider;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.utils.ContactUtils;
import belldial.co.uk.utils.LinphoneUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Arrays;
import java.util.List;

import belldial.co.uk.R;
import org.linphone.core.Account;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import org.linphone.core.Reason;
import org.linphone.core.RegistrationState;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.tools.Log;

public class CallOutgoingActivity extends AppNavigationProvider {

    @BindView(R.id.toolbar_back_btn)
    Toolbar toolbar;

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.textViewCallType)
    AppCompatTextView textViewCallType;

    @BindView(R.id.textViewConnecting)
    AppCompatTextView textViewConnecting;

    @BindView(R.id.current_call_timer)
    Chronometer currentCallTimer;

    @BindView(R.id.imageViewMute)
    AppCompatImageView imageViewMute;

    @BindView(R.id.imageViewKeypad)
    AppCompatImageView imageViewKeypad;

    @BindView(R.id.imageViewSpeaker)
    AppCompatImageView imageViewSpeaker;

    @BindView(R.id.imageViewCallHangup)
    AppCompatImageView imageViewCallHangup;

    @BindView(R.id.imageViewAddCall)
    AppCompatImageView imageViewAddCall;

    @BindView(R.id.imageViewTransferCall)
    AppCompatImageView imageViewTransferCall;

    @BindView(R.id.imageViewRecordCall)
    AppCompatImageView imageViewRecordCall;

    @BindView(R.id.imageViewMergeCall)
    AppCompatImageView imageViewMergeCall;

    @BindView(R.id.imageViewSplitCall)
    AppCompatImageView imageViewSplitCall;

    @BindView(R.id.imageViewUser1)
    AppCompatImageView imageViewUser1;

    @BindView(R.id.textViewCallerNUmber1)
    AppCompatTextView textViewCallerNUmber1;

    @BindView(R.id.linearLayoutCaller1)
    LinearLayout linearLayoutCaller1;

    @BindView(R.id.imageViewUser2)
    AppCompatImageView imageViewUser2;

    @BindView(R.id.textViewCallerNUmber2)
    AppCompatTextView textViewCallerNUmber2;

    @BindView(R.id.linearLayoutCaller2)
    LinearLayout linearLayoutCaller2;

    @BindView(R.id.textViewCall1)
    AppCompatTextView textViewCall1;

    @BindView(R.id.imageViewSwapCall)
    AppCompatImageView imageViewSwapCall;

    @BindView(R.id.textViewCall2)
    AppCompatTextView textViewCall2;

    int splitCall = 0;

    @BindView(R.id.imageViewKeypadNew)
    AppCompatImageView imageViewKeypadNew;

    @BindView(R.id.contentDial)
    LinearLayout contentDial;

    @BindView(R.id.dial1)
    ImageView dial1;
    @BindView(R.id.dial2)
    ImageView dial2;
    @BindView(R.id.dial3)
    ImageView dial3;
    @BindView(R.id.dial4)
    ImageView dial4;
    @BindView(R.id.dial5)
    ImageView dial5;
    @BindView(R.id.dial6)
    ImageView dial6;
    @BindView(R.id.dial7)
    ImageView dial7;
    @BindView(R.id.dial8)
    ImageView dial8;
    @BindView(R.id.dial9)
    ImageView dial9;
    @BindView(R.id.dialstar)
    ImageView dialstar;
    @BindView(R.id.dial0)
    ImageView dial0;
    @BindView(R.id.dialhash)
    ImageView dialhash;

    private Core mCore;
    private Call mCall;
    private static Call pause_call;
    private SimpleCoreListener mListener;
    Address linphoneAddress;

    private boolean isSpeakerEnabled = false, isMicMuted = false,
            isBluetoothEnabled = false, isEarPhoneEnabled = false;
    boolean isMute = false, isSpeaker = false, isHold = false;
    private static final int WRITE_EXTERNAL_STORAGE_FOR_RECORDING = 2;

    @Override
    public int getPlaceHolder() {
        return R.id.placeHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.fragment_outgoing_call);
        ButterKnife.bind(this);
        toolBarTitle.setText("");

        mCore = LinphoneManager.getCore();
        Call currentCall = LinphoneManager.getCore().getCurrentCall();
        mCall = currentCall;
        if (currentCall == null) {
            hangUp();
            goBackToDialer();
        }
        if (mCall != null) {
            linphoneAddress = mCall.getRemoteAddress();
            String contactBookName = ContactUtils.getContactDisplayNameByNumber(
                    CallOutgoingActivity.this,
                    LinphoneUtils.getDisplayableUsernameFromAddress(
                            linphoneAddress.getUsername()));
            textViewCallerNUmber1.setText(
                    contactBookName == null
                            ? LinphoneUtils.getAddressDisplayName(linphoneAddress)
                            : contactBookName);
        }

        if (LinphoneService.isReady()) {
            LinphoneManager.getInstance().enableProximitySensing(true);
        }

        for (Call call : LinphoneManager.getCore().getCalls()) {
            registerCallDurationTimer(call);
        }

        mListener = new SimpleCoreListener() {

            @Override
            public void onAccountRegistrationStateChanged(
                    Core lc, Account account, RegistrationState state, String smessage) {
                // ✅ Skip SIP registration icon during active call
                // SIP re-registration must never flash red while on a call
                if (lc != null && lc.getCallsNb() > 0) {
                    return;
                }
                // No active call → restore registration icon
                updateStatusIcon(lc, state);
            }

            // ✅ Fires every second during call — show real network signal
            @Override
            public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
                if (call != null) {
                    imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
                }
            }

            @Override
            public void onCallStateChanged(
                    Core lc, Call call, Call.State state, String message) {

                android.util.Log.e("OutGoingCall",
                        "onCallStateChanged: " + state + " -- " + call.getState());

                if (call == mCall && Call.State.Connected == state) {
                    mCall = call;
                    registerCallDurationTimer(mCall);
                    // ✅ Connected → switch to network icon
                    imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
                    return;
                } else if (call == mCall && Call.State.StreamsRunning == state) {
                    registerCallDurationTimer(mCall);
                    // ✅ Streams running → network icon
                    imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
                } else if (state == Call.State.OutgoingProgress
                        || state == Call.State.OutgoingRinging
                        || state == Call.State.OutgoingInit) {
                    // ✅ Dialing → show network icon immediately, not SIP state
                    imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
                } else if (state == Call.State.Error) {
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                        Toast.makeText(CallOutgoingActivity.this,
                                getString(R.string.error_call_declined),
                                Toast.LENGTH_SHORT).show();
                        decline();
                    } else if (call.getErrorInfo().getReason() == Reason.NotFound) {
                        Toast.makeText(CallOutgoingActivity.this,
                                getString(R.string.error_user_not_found),
                                Toast.LENGTH_SHORT).show();
                        decline();
                    } else if (call.getErrorInfo().getReason() == Reason.NotAcceptable) {
                        Toast.makeText(CallOutgoingActivity.this,
                                getString(R.string.error_incompatible_media),
                                Toast.LENGTH_SHORT).show();
                        decline();
                    } else if (call.getErrorInfo().getReason() == Reason.Busy) {
                        Toast.makeText(CallOutgoingActivity.this,
                                getString(R.string.error_user_busy),
                                Toast.LENGTH_SHORT).show();
                        decline();
                    } else if (message != null) {
                        Toast.makeText(CallOutgoingActivity.this,
                                getString(R.string.error_unknown),
                                Toast.LENGTH_SHORT).show();
                        decline();
                    }
                } else if (state == Call.State.End) {
                    if (call.getErrorInfo().getReason() == Reason.Declined) {
                        decline();
                    }
                    // ✅ Call ended → restore registration icon
                    Account account = lc.getDefaultAccount();
                    if (account != null) {
                        updateStatusIcon(lc, account.getState());
                    }
                }

                if (LinphoneManager.getCore().getCallsNb() > 1) {
                    if (call.getConference() != null) {
                        imageViewAddCall.setVisibility(View.GONE);
                        imageViewMergeCall.setVisibility(View.GONE);
                        imageViewSplitCall.setVisibility(View.GONE);
                        setCallerData(mCore.getCalls(), 1);
                    } else {
                        imageViewAddCall.setVisibility(View.GONE);
                        imageViewMergeCall.setVisibility(View.VISIBLE);
                        imageViewSplitCall.setVisibility(View.GONE);
                        setCallerData(mCore.getCalls(), 2);
                    }
                } else {
                    imageViewAddCall.setVisibility(View.VISIBLE);
                    imageViewMergeCall.setVisibility(View.GONE);
                    imageViewSplitCall.setVisibility(View.GONE);
                    linearLayoutCaller2.setVisibility(View.GONE);
                    imageViewSwapCall.setVisibility(View.GONE);
                    if (mCall != null) {
                        linphoneAddress = mCall.getRemoteAddress();
                        String contactBookName = ContactUtils.getContactDisplayNameByNumber(
                                CallOutgoingActivity.this,
                                LinphoneUtils.getDisplayableUsernameFromAddress(
                                        linphoneAddress.getUsername()));
                        textViewCallerNUmber1.setText(
                                contactBookName == null
                                        ? LinphoneUtils.getAddressDisplayName(linphoneAddress)
                                        : contactBookName);
                    }
                }

                if (LinphoneManager.getCore().getCallsNb() == 0) {
                    finish();
                    return;
                }
            }
        };

        isSpeakerEnabled = LinphoneManager.getAudioManager().isAudioRoutedToSpeaker();
        isMicMuted = !LinphoneManager.getCore().isMicEnabled();
    }

    // Helper — set registration-based icon (only when NOT in a call)
    private void updateStatusIcon(Core lc, RegistrationState state) {
        if (lc == null) return;
        if (lc.getAccountList() == null || lc.getAccountList().length == 0) {
            imageViewStatus.setImageResource(R.drawable.signal_null);
            return;
        }
        imageViewStatus.setImageResource(Constant.getStatusIconResource(state, true));
    }

    private void setCallerData(Call[] calls, int type) {
        if (type == 1) {
            textViewCall1.setText("On Call");
            textViewCall2.setText("On Call");
        } else {
            textViewCall1.setText("On Hold");
            textViewCall2.setText("On Call");
        }
        String contactBookName = ContactUtils.getContactDisplayNameByNumber(
                CallOutgoingActivity.this,
                LinphoneUtils.getDisplayableUsernameFromAddress(
                        calls[0].getRemoteAddress().getUsername()));
        textViewCallerNUmber1.setText(
                contactBookName == null
                        ? LinphoneUtils.getAddressDisplayName(calls[0].getRemoteAddress())
                        : contactBookName);

        linearLayoutCaller2.setVisibility(View.VISIBLE);

        String contactBookName1 = ContactUtils.getContactDisplayNameByNumber(
                CallOutgoingActivity.this,
                LinphoneUtils.getDisplayableUsernameFromAddress(
                        calls[1].getRemoteAddress().getUsername()));
        textViewCallerNUmber2.setText(
                contactBookName1 == null
                        ? LinphoneUtils.getAddressDisplayName(calls[1].getRemoteAddress())
                        : contactBookName1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LinphoneService.isReady()) {
            LinphoneManager.getInstance().enableProximitySensing(false);
        }
    }

    private void goToDialerPad() {
        Intent intent = new Intent(CallOutgoingActivity.this, HomeActivity.class);
        intent.putExtra(Constant.MOVETODIAL, true);
        startActivity(intent);
    }

    private void goToDialerPadTrnsfter() {
        Intent intent = new Intent(CallOutgoingActivity.this, HomeActivity.class);
        intent.putExtra(Constant.MOVETODIALTRANSFER, true);
        startActivity(intent);
    }

    private boolean checkPermission(String permission) {
        int granted = getPackageManager().checkPermission(permission, getPackageName());
        Log.i("[Permission] " + permission + " permission is "
                + (granted == PackageManager.PERMISSION_GRANTED ? "granted" : "denied"));
        return granted == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkAndRequestPermission(String permission, int result) {
        if (!checkPermission(permission)) {
            Log.i("[Permission] Asking for " + permission);
            ActivityCompat.requestPermissions(this, new String[]{permission}, result);
            return false;
        }
        return true;
    }

    @OnClick({
            R.id.imageViewMute, R.id.imageViewKeypad, R.id.imageViewSpeaker,
            R.id.imageViewCallHangup, R.id.imageViewAddCall, R.id.imageViewKeypadNew,
            R.id.imageViewTransferCall, R.id.imageViewRecordCall, R.id.imageViewMergeCall,
            R.id.imageViewSwapCall, R.id.imageViewSplitCall,
            R.id.dial1, R.id.dial2, R.id.dial3, R.id.dial4, R.id.dial5, R.id.dial6,
            R.id.dial7, R.id.dial8, R.id.dial9, R.id.dialstar, R.id.dial0, R.id.dialhash
    })
    public void onViewClicked(View view) {
        try {
            switch (view.getId()) {
                case R.id.imageViewMute:
                    toggleMicro();
                    break;
                case R.id.imageViewSwapCall:
                    if (splitCall == 0) {
                        splitCall = 1;
                        splitcall(mCore.getCalls(), splitCall);
                    } else {
                        splitCall = 0;
                        splitcall(mCore.getCalls(), splitCall);
                    }
                    break;
                case R.id.imageViewKeypad:
                    togglePause(mCore.getCurrentCall());
                    break;
                case R.id.imageViewMergeCall:
                    mCore.addAllToConference();
                    imageViewSwapCall.setVisibility(View.GONE);
                    imageViewAddCall.setVisibility(View.GONE);
                    imageViewMergeCall.setVisibility(View.GONE);
                    imageViewSplitCall.setVisibility(View.GONE);
                    break;
                case R.id.imageViewSplitCall:
                    Call currentCall = mCore.getCurrentCall();
                    for (Call call : mCore.getCalls()) {
                        if (call == currentCall) {
                            call.pause();
                        } else {
                            if (call.getState() == Call.State.Paused) {
                                call.resume();
                            }
                        }
                    }
                    textViewCall1.setText("On Hold");
                    textViewCall2.setText("On Call");
                    imageViewSwapCall.setVisibility(View.VISIBLE);
                    imageViewSplitCall.setVisibility(View.GONE);
                    imageViewMergeCall.setVisibility(View.VISIBLE);
                    break;
                case R.id.imageViewSpeaker:
                    toggleSpeaker();
                    break;
                case R.id.imageViewCallHangup:
                    LinphoneManager.getCore().terminateAllCalls();
                    LinphoneManager.getInstance().enableProximitySensing(false);
                    goBackToDialer();
                    break;
                case R.id.imageViewAddCall:
                case R.id.imageViewKeypadNew:
                    if (contentDial.getVisibility() == View.GONE) {
                        contentDial.setVisibility(View.VISIBLE);
                    } else {
                        contentDial.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageViewTransferCall:
                    imageViewTransferCall.setImageResource(R.drawable.transfer_sel);
                    goToDialerPadTrnsfter();
                    break;
                case R.id.imageViewRecordCall:
                    if (checkAndRequestPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE_FOR_RECORDING)) {
                        toggleRecording();
                    }
                    break;
                case R.id.dial1:  sendDTMF('1'); break;
                case R.id.dial2:  sendDTMF('2'); break;
                case R.id.dial3:  sendDTMF('3'); break;
                case R.id.dial4:  sendDTMF('4'); break;
                case R.id.dial5:  sendDTMF('5'); break;
                case R.id.dial6:  sendDTMF('6'); break;
                case R.id.dial7:  sendDTMF('7'); break;
                case R.id.dial8:  sendDTMF('8'); break;
                case R.id.dial9:  sendDTMF('9'); break;
                case R.id.dialstar: sendDTMF('*'); break;
                case R.id.dial0:  sendDTMF('0'); break;
                case R.id.dialhash: sendDTMF('#'); break;
            }
        } catch (Exception e) {
            // ignored
        }
    }

    private void sendDTMF(char mKeyCode) {
        Core core = LinphoneManager.getCore();
        android.util.Log.e("SendDTMF", "sendDTMF: " + mKeyCode);
        core.stopDtmf();
        Call call = core.getCurrentCall();
        if (call != null) {
            call.sendDtmf(mKeyCode);
            core.playDtmf(mKeyCode, 1);
        }
    }

    private void splitcall(Call[] calls, int split) {
        if (split == 0) {
            calls[0].pause();
            if (calls[1].getState() == Call.State.Paused) calls[1].resume();
            textViewCall1.setText("On Hold");
            textViewCall2.setText("On Call");
        } else {
            calls[0].resume();
            calls[1].pause();
            textViewCall2.setText("On Hold");
            textViewCall1.setText("On Call");
        }
    }

    private void toggleRecording() {
        Call call = mCore.getCurrentCall();
        if (call == null) return;
        if (call.isRecording()) {
            call.stopRecording();
            imageViewRecordCall.setImageResource(R.drawable.record);
        } else {
            call.startRecording();
            imageViewRecordCall.setImageResource(R.drawable.record_sel);
        }
    }

    private void togglePause(Call call) {
        try {
            if (call == null) call = pause_call;
            if (call != null && call == mCore.getCurrentCall()) {
                pause_call = call;
                call.pause();
                imageViewKeypad.setImageResource(R.drawable.hold_sel);
            } else if (call != null && call.getState() == Call.State.Paused) {
                call.resume();
                imageViewKeypad.setImageResource(R.drawable.hold);
            }
        } catch (Exception e) {
            // ignored
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
        if (requestCode == WRITE_EXTERNAL_STORAGE_FOR_RECORDING) {
            toggleRecording();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.addListener(mListener);

            // ✅ If already in a call on resume → show network icon immediately
            // Don't trigger registration state which would flash red
            if (lc.getCallsNb() > 0) {
                imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
            } else {
                Account account = lc.getDefaultAccount();
                if (account != null) {
                    updateStatusIcon(lc, account.getState());
                }
            }
        }

        mCall = null;
        if (LinphoneManager.getCore() != null) {
            if (LinphoneManager.getCore().getCallsNb() > 1) {
                imageViewAddCall.setVisibility(View.GONE);
                imageViewMergeCall.setVisibility(View.VISIBLE);
                imageViewSplitCall.setVisibility(View.GONE);
                setCallerData(mCore.getCalls(), 2);
            } else {
                linearLayoutCaller2.setVisibility(View.GONE);
                imageViewAddCall.setVisibility(View.VISIBLE);
                imageViewMergeCall.setVisibility(View.GONE);
            }

            List<Call> calls = Arrays.asList(LinphoneManager.getCore().getCalls());
            for (Call call : calls) {
                Call.State cstate = call.getState();
                if (Call.State.OutgoingInit == cstate
                        || Call.State.OutgoingProgress == cstate
                        || Call.State.OutgoingRinging == cstate
                        || Call.State.OutgoingEarlyMedia == cstate) {
                    mCall = call;
                    break;
                }
                if (Call.State.StreamsRunning == cstate) {
                    return;
                }
            }
        }
        if (mCall == null) {
            Log.e("Couldn't find outgoing call");
            finish();
            return;
        }
    }

    @Override
    public void onPause() {
        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.removeListener(mListener);
        }
        super.onPause();
    }

    public void hangUp() {
        Core lc = LinphoneManager.getCore();
        Call currentCall = lc.getCurrentCall();
        if (currentCall != null) {
            currentCall.terminate();
        } else if (lc.isInConference()) {
            lc.terminateConference();
        } else {
            lc.terminateAllCalls();
        }
        LinphoneManager.getInstance().enableProximitySensing(false);
        goBackToDialer();
    }

    private void decline() {
        LinphoneManager.getInstance().enableProximitySensing(false);
        mCall.terminate();
        goBackToDialer();
    }

    public void toggleMicro() {
        isMicMuted = !isMicMuted;
        LinphoneManager.getCore().setMicEnabled(!isMicMuted);
        if (isMicMuted) {
            imageViewMute.setImageResource(R.drawable.mute_sel);
        } else {
            imageViewMute.setImageResource(R.drawable.mute);
        }
    }

    private void goBackToDialer() {
        try {
            if (LinphoneManager.getCore().getCallsNb() == 0) {
                finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void registerCallDurationTimer(Call call) {
        int callDuration = call.getDuration();
        if (callDuration == 0 && call.getState() != Call.State.StreamsRunning) {
            return;
        }
        if (currentCallTimer == null) {
            throw new IllegalArgumentException("no callee_duration view found");
        }
        currentCallTimer.setBase(SystemClock.elapsedRealtime() - 1000 * callDuration);
        currentCallTimer.start();
        textViewConnecting.setVisibility(View.GONE);
        currentCallTimer.setVisibility(View.VISIBLE);
    }

    public void toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled;
        if (isSpeakerEnabled) {
            LinphoneManager.getAudioManager().routeAudioToSpeaker();
            imageViewSpeaker.setImageResource(R.drawable.speacker_sel);
        } else {
            Log.d("Toggle speaker off, routing back to earpiece");
            LinphoneManager.getAudioManager().routeAudioToEarPiece();
            imageViewSpeaker.setImageResource(R.drawable.speacker);
        }
    }
}