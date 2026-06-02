package belldial.co.uk.ui.fragments;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.DialerPresenter;
import belldial.co.uk.ui.views.DialerView;
import belldial.co.uk.utils.Constant;
import butterknife.BindView;
import butterknife.OnClick;

import java.util.Objects;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.CallOutgoingActivity;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.Call;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import org.linphone.core.Account;
import org.linphone.core.RegistrationState;
import belldial.co.uk.utils.SimpleCoreListener;

/** A simple {@link Fragment} subclass. */
public class DialerFragment extends BaseFragment<DialerPresenter, DialerView>
        implements DialerView {

    @BindView(R.id.textViewNumber)
    EditText textViewNumber;

    @BindView(R.id.imgthreeDots)
    ImageView imgthreeDots;

    @BindView(R.id.imageViewRemove)
    ImageView imageViewRemove;

    @BindView(R.id.linearNumber)
    LinearLayout linearNumber;

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

    @BindView(R.id.contentDial)
    LinearLayout contentDial;

    @BindView(R.id.dial)
    ImageView dial;

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.cardView)
    CardView cardView;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    SharedPreferences sharedPreferences;
    private SimpleCoreListener mListener;
    private static final int CALL_PERMISSION_REQUEST_CODE = 101;

    public static boolean transfer = false;

    public static boolean isTransfer() {
        return transfer;
    }

    public static void setTransfer(boolean transfer) {
        DialerFragment.transfer = transfer;
    }

    @Override
    protected int createLayout() {
        return R.layout.fragment_dialer;
    }

    @Override
    protected void setPresenter() {
        presenter = new DialerPresenter();
    }

    @Override
    protected DialerView createView() {
        return this;
    }

    @Override
    protected void bindData() {
        sharedPreferences = getActivity().getSharedPreferences(
                Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);

        mListener = new SimpleCoreListener() {

            @Override
            public void onAccountRegistrationStateChanged(
                    final Core lc,
                    final Account account,
                    final RegistrationState state,
                    String smessage) {
                if (!LinphoneService.isReady()) {
                    return;
                }

                // ✅ Skip SIP registration icon update during active call
                if (lc != null && lc.getCallsNb() > 0) {
                    return;
                }

                if (lc.getAccountList() == null) {
                    imageViewStatus.setImageResource(R.drawable.signal_null);
                    return;
                }

                if (lc.getDefaultAccount() != null
                        && lc.getDefaultAccount().equals(account)) {
                    imageViewStatus.setImageResource(
                            Constant.getStatusIconResource(state, true));
                } else if (lc.getDefaultAccount() == null) {
                    imageViewStatus.setImageResource(
                            Constant.getStatusIconResource(state, true));
                }
            }

            @Override
            public void onCallStateChanged(
                    Core lc, Call call, Call.State cstate, String message) {
                if (cstate == Call.State.End
                        || cstate == Call.State.Released
                        || cstate == Call.State.Error) {
                    Log.e("CallListener", "Call ended: " + cstate);
                    ((HomeActivity) getActivity()).showActiveCall(false);

                    // ✅ Call ended → restore registration-based icon
                    Account account = lc.getDefaultAccount();
                    if (account != null) {
                        imageViewStatus.setImageResource(
                                Constant.getStatusIconResource(account.getState(), true));
                    }
                }
            }

            @Override
            public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
                if (call == null) {
                    ((HomeActivity) getActivity()).showActiveCall(false);

                    // ✅ No call → restore registration icon
                    Account account = lc.getDefaultAccount();
                    if (account != null) {
                        imageViewStatus.setImageResource(
                                Constant.getStatusIconResource(account.getState(), true));
                    }
                } else {
                    ((HomeActivity) getActivity()).showActiveCall(true);

                    // ✅ Active call → use real network signal
                    imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
                }
            }
        };

        try {
            if (LinphoneManager.getCore().getCurrentCall() != null) {
                ((HomeActivity) getActivity()).showActiveCall(true);
            } else {
                ((HomeActivity) getActivity()).showActiveCall(false);
            }
        } catch (NullPointerException e) {
            // ignored
        }

        toolBarTitle.setText("Ext: " + sharedPreferences.getString(Constant.USERNAME, ""));
        toolBarTitle.setTextColor(getResources().getColor(R.color.colorGreen));
        textViewNumber.setShowSoftInputOnFocus(false);

        imageViewRemove.setOnLongClickListener(v -> {
            textViewNumber.setText("");
            return false;
        });

        textViewNumber.setOnTouchListener((v, event) -> {
            v.onTouchEvent(event);
            InputMethodManager imm = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return true;
        });

        dial1.setOnLongClickListener(v -> {
            Core core = LinphoneManager.getCore();
            if (core.getCalls().length == 0) {
                LinphoneManager.getCallManager().newOutgoingCall(
                        "sip:*95" + "@" + sharedPreferences.getString(Constant.DOMAIN, ""),
                        "Voicemail");
                Intent intent = new Intent(getActivity(), CallOutgoingActivity.class);
                startActivity(intent);
            }
            return true;
        });
    }

    @OnClick({
            R.id.textViewNumber,
            R.id.imageViewRemove,
            R.id.dial1,
            R.id.dial2,
            R.id.dial3,
            R.id.dial4,
            R.id.dial5,
            R.id.dial6,
            R.id.dial7,
            R.id.dial8,
            R.id.dial9,
            R.id.dialstar,
            R.id.dial0,
            R.id.dialhash,
            R.id.contentDial,
            R.id.dial
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textViewNumber:
                hideKeyBoard();
                break;
            case R.id.imageViewRemove:
                int cursorPosition = textViewNumber.getSelectionStart();
                String tmp = textViewNumber.getText().toString().substring(0, cursorPosition);
                String tmp1 = textViewNumber.getText().toString().substring(
                        cursorPosition, textViewNumber.getText().toString().length());
                if (tmp.length() > 0) {
                    textViewNumber.setText(tmp.substring(0, tmp.length() - 1) + "" + tmp1);
                    if (cursorPosition >= 0) {
                        textViewNumber.setSelection(cursorPosition - 1);
                    }
                }
                break;
            case R.id.dial1: dailKeypad("1"); break;
            case R.id.dial2: dailKeypad("2"); break;
            case R.id.dial3: dailKeypad("3"); break;
            case R.id.dial4: dailKeypad("4"); break;
            case R.id.dial5: dailKeypad("5"); break;
            case R.id.dial6: dailKeypad("6"); break;
            case R.id.dial7: dailKeypad("7"); break;
            case R.id.dial8: dailKeypad("8"); break;
            case R.id.dial9: dailKeypad("9"); break;
            case R.id.dialstar: dailKeypad("*"); break;
            case R.id.dial0: dailKeypad("0"); break;
            case R.id.dialhash: dailKeypad("#"); break;
            case R.id.contentDial: break;
            case R.id.dial:
                if (checkAndRequestCallPermissions()) {
                    proceedWithCall();
                } else {
                    Toast.makeText(requireContext(),
                            "You have not provided required permissions", LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkAndRequestCallPermissions() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE};
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        if (!allGranted) {
            requestPermissions(permissions, CALL_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void proceedWithCall() {
        try {
            if (!textViewNumber.getText().toString().isEmpty()) {
                if (Constant.TRANSFER == 1) {
                    Core core = LinphoneManager.getCore();
                    if (core.getCurrentCall() == null) return;
                    core.getCurrentCall().transfer(textViewNumber.getText().toString());
                    showMessage("Call Transfer to " + textViewNumber.getText().toString());
                    Constant.TRANSFER = 0;
                } else {
                    if (Objects.requireNonNull(LinphoneManager.getCore()).getCallsNb() < 2) {
                        LinphoneManager.getCallManager().newOutgoingCall(
                                "sip:"
                                        + textViewNumber.getText().toString()
                                        + "@"
                                        + sharedPreferences.getString(Constant.DOMAIN, ""),
                                textViewNumber.getText().toString());
                        Intent intent = new Intent(getActivity(), CallOutgoingActivity.class);
                        startActivity(intent);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("asad", "onViewClicked: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                proceedWithCall();
            } else {
                showMessage("Permissions required to make a call.");
            }
        }
    }

    public void dailKeypad(String number) {
        try {
            int cursorPosition = textViewNumber.getSelectionStart();
            String tmp = textViewNumber.getText().toString().substring(0, cursorPosition);
            String tmp1 = textViewNumber.getText().toString().substring(
                    cursorPosition, textViewNumber.getText().toString().length());
            textViewNumber.setText(tmp + number + tmp1);
            if (cursorPosition >= 0) {
                textViewNumber.setSelection(cursorPosition + 1);
            }
        } catch (Exception e) {
            // ignored
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Core lc = LinphoneManager.getCore();
        Log.d("DialerFragment", "onResume: " + lc);
        if (lc != null) {
            lc.addListener(mListener);
            // ✅ If call is active on resume → show network icon immediately
            if (lc.getCallsNb() > 0) {
                imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
            } else {
                Account lpc = lc.getDefaultAccount();
                if (lpc != null) {
                    mListener.onAccountRegistrationStateChanged(lc, lpc, lpc.getState(), null);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.removeListener(mListener);
        }
    }
}