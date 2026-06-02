package belldial.co.uk.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.activities.MainActivity;
import belldial.co.uk.api.model.Message;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.dialog.BottomSheetDialog;
import belldial.co.uk.ui.model.CountryCode;
import belldial.co.uk.ui.presenter.LoginPresenter;
import belldial.co.uk.ui.views.LoginView;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.LinphoneUtils;
import butterknife.BindView;
import butterknife.OnClick;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
//import com.google.firebase.iid.FirebaseInstanceId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import belldial.co.uk.R;

import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.AccountCreator;
import org.linphone.core.Address;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.PayloadType;
import org.linphone.core.ProxyConfig;
import org.linphone.core.TransportType;

public class LoginFragment extends BaseFragment<LoginPresenter, LoginView>
        implements LoginView, BottomSheetDialog.CallBackSelectionGroup {

    SharedPreferences sharedPreferences;

    @BindView(R.id.editTextMobileNumber)
    AppCompatEditText editTextMobileNumber;

    @BindView(R.id.editTextPassword)
    AppCompatEditText editTextPassword;

    @BindView(R.id.imageShowHidePassword)
    AppCompatImageView imageShowHidePassword;

    @BindView(R.id.buttonSignup)
    AppCompatButton buttonSignup;

    boolean isshow = false;

    @BindView(R.id.textViewCountry)
    AppCompatTextView textViewCountry;

    @BindView(R.id.textViewCountryCode)
    AppCompatTextView textViewCountryCode;

    @BindView(R.id.buttonSubmit)
    AppCompatButton buttonSubmit;

    @BindView(R.id.LayoutMobileNumber)
    LinearLayout LayoutMobileNumber;

    @BindView(R.id.textViewVerificationMessage)
    AppCompatTextView textViewVerificationMessage;

    @BindView(R.id.editTextDigit1)
    AppCompatEditText editTextDigit1;

    @BindView(R.id.editTextDigit2)
    AppCompatEditText editTextDigit2;

    @BindView(R.id.editTextDigit3)
    AppCompatEditText editTextDigit3;

    @BindView(R.id.editTextDigit4)
    AppCompatEditText editTextDigit4;

    @BindView(R.id.editTextDigit5)
    AppCompatEditText editTextDigit5;

    @BindView(R.id.editTextDigit6)
    AppCompatEditText editTextDigit6;

    @BindView(R.id.textViewResendCode)
    AppCompatTextView textViewResendCode;

    @BindView(R.id.LayoutOTPVerification)
    LinearLayout LayoutOTPVerification;

    private Address address;
    private LinphonePreferences mPrefs;
    String username;
    String domain;
    BottomSheetDialog bottomSheetDialog;
    List<CountryCode> countryCodeList;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    String number;
    private static final String TAG = "LoginFragment";
    TelephonyManager tm;

    @Override
    protected int createLayout() {
        return R.layout.fragment_login;
    }

    @Override
    protected void setPresenter() {
        presenter = new LoginPresenter();
    }

    @Override
    protected LoginView createView() {
        return this;
    }

    @Override
    protected void bindData() {
        mPrefs = LinphonePreferences.instance();

        sharedPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        FirebaseApp.initializeApp(/*context=*/ getActivity());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        try {
            int code = Integer.parseInt(Constant.getcountryCode().get(tm.getSimCountryIso().toUpperCase()));
            Log.e(TAG, "bindData:  code" + code);
            textViewCountryCode.setText("+" + code);
            String countryName = Constant.getcountryNames().get(code);

            Log.e(TAG, "bindData:  name" + countryName);
            textViewCountry.setText(countryName);
        } catch (Exception e) {
            String locale = getContext().getResources().getConfiguration().locale.getCountry();
            String localeCountry =
                    getContext().getResources().getConfiguration().locale.getDisplayCountry();
            String code = Constant.getcountryCode().get(locale);
            Log.e(TAG, "bindData: " + locale);
            textViewCountryCode.setText("+" + code);
            Log.e(TAG, "bindData:  code" + code);
            Log.e(TAG, "bindData:  name" + localeCountry);
            textViewCountry.setText(localeCountry);
        }

        countryCodeList = new ArrayList<>();
        countryCodeList.addAll(Constant.getCountryCodeList());

        bottomSheetDialog = new BottomSheetDialog();
        bottomSheetDialog.setGroupList(countryCodeList);
        bottomSheetDialog.setSelectionListner(LoginFragment.this);

        mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {

                        Log.d(TAG, "onVerificationCompleted:" + credential);
                        mVerificationInProgress = false;

                        Log.d(TAG, "onVerificationCompleted:" + credential.getSmsCode());
                        // Update the UI and attempt sign in with the phone credential
                        updateUI(STATE_VERIFY_SUCCESS, credential);
                        signInWithPhoneAuthCredential(credential);
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e);
                        mVerificationInProgress = false;

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // [START_EXCLUDE]
                            hideLoader();
                            showMessage("Invalid phone number.");

                            // [END_EXCLUDE]
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // [START_EXCLUDE]
                            hideLoader();
                            showMessage("Quota exceeded.");

                            // [END_EXCLUDE]
                        }

                        // Show a message and update the UI
                        // [START_EXCLUDE]
                        updateUI(STATE_VERIFY_FAILED);
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCodeSent(
                            @NonNull String verificationId,
                            @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a
                        // credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verificationId);
                        showMessage("Verification code sent to your mobile number");

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;

                        // [START_EXCLUDE]
                        // Update UI
                        updateUI(STATE_CODE_SENT);
                        // [END_EXCLUDE]
                    }
                };

        editTextDigit1.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            editTextDigit2.requestFocus();
                            if (!editTextDigit4.getText().toString().isEmpty()
                                    && !editTextDigit2.getText().toString().isEmpty()
                                    && !editTextDigit3.getText().toString().isEmpty()
                                    && !editTextDigit5.getText().toString().isEmpty()
                                    && !editTextDigit6.getText().toString().isEmpty()) {
                                String code =
                                        editTextDigit1.getText().toString()
                                                + editTextDigit2.getText().toString()
                                                + editTextDigit3.getText().toString()
                                                + editTextDigit4.getText().toString()
                                                + editTextDigit5.getText().toString()
                                                + editTextDigit6.getText().toString();
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
        editTextDigit2.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            editTextDigit3.requestFocus();
                            if (!editTextDigit1.getText().toString().isEmpty()
                                    && !editTextDigit4.getText().toString().isEmpty()
                                    && !editTextDigit3.getText().toString().isEmpty()
                                    && !editTextDigit5.getText().toString().isEmpty()
                                    && !editTextDigit6.getText().toString().isEmpty()) {
                                String code =
                                        editTextDigit1.getText().toString()
                                                + editTextDigit2.getText().toString()
                                                + editTextDigit3.getText().toString()
                                                + editTextDigit4.getText().toString()
                                                + editTextDigit5.getText().toString()
                                                + editTextDigit6.getText().toString();
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
        editTextDigit3.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            editTextDigit4.requestFocus();
                            if (!editTextDigit1.getText().toString().isEmpty()
                                    && !editTextDigit2.getText().toString().isEmpty()
                                    && !editTextDigit4.getText().toString().isEmpty()
                                    && !editTextDigit5.getText().toString().isEmpty()
                                    && !editTextDigit6.getText().toString().isEmpty()) {
                                String code =
                                        editTextDigit1.getText().toString()
                                                + editTextDigit2.getText().toString()
                                                + editTextDigit3.getText().toString()
                                                + editTextDigit4.getText().toString()
                                                + editTextDigit5.getText().toString()
                                                + editTextDigit6.getText().toString();
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
        editTextDigit4.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            editTextDigit5.requestFocus();
                            if (!editTextDigit1.getText().toString().isEmpty()
                                    && !editTextDigit2.getText().toString().isEmpty()
                                    && !editTextDigit3.getText().toString().isEmpty()
                                    && !editTextDigit5.getText().toString().isEmpty()
                                    && !editTextDigit6.getText().toString().isEmpty()) {
                                String code =
                                        editTextDigit1.getText().toString()
                                                + editTextDigit2.getText().toString()
                                                + editTextDigit3.getText().toString()
                                                + editTextDigit4.getText().toString()
                                                + editTextDigit5.getText().toString()
                                                + editTextDigit6.getText().toString();
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
        editTextDigit5.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            editTextDigit6.requestFocus();
                            if (!editTextDigit1.getText().toString().isEmpty()
                                    && !editTextDigit2.getText().toString().isEmpty()
                                    && !editTextDigit3.getText().toString().isEmpty()
                                    && !editTextDigit4.getText().toString().isEmpty()
                                    && !editTextDigit6.getText().toString().isEmpty()) {
                                String code =
                                        editTextDigit1.getText().toString()
                                                + editTextDigit2.getText().toString()
                                                + editTextDigit3.getText().toString()
                                                + editTextDigit4.getText().toString()
                                                + editTextDigit5.getText().toString()
                                                + editTextDigit6.getText().toString();
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

        editTextDigit6.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            if (!editTextDigit1.getText().toString().isEmpty()
                                    && !editTextDigit2.getText().toString().isEmpty()
                                    && !editTextDigit3.getText().toString().isEmpty()
                                    && !editTextDigit4.getText().toString().isEmpty()
                                    && !editTextDigit5.getText().toString().isEmpty()) {
                                String code =
                                        editTextDigit1.getText().toString()
                                                + editTextDigit2.getText().toString()
                                                + editTextDigit3.getText().toString()
                                                + editTextDigit4.getText().toString()
                                                + editTextDigit5.getText().toString()
                                                + editTextDigit6.getText().toString();
                                verifyPhoneNumberWithCode(mVerificationId, code);
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    @OnClick({
            R.id.textViewCountry,
            R.id.textViewCountryCode,
            R.id.buttonSubmit,
            R.id.imageShowHidePassword,
            R.id.buttonSignup
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textViewCountry:
            case R.id.textViewCountryCode:
                if (countryCodeList.size() > 0) {
                    bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "check");
                }
                break;
            case R.id.buttonSubmit:

//                requestBluePermission();

                if (textViewCountry.getText().toString().equalsIgnoreCase("Select Country")) {
                    showMsg("Please Select Your Country");
                } else if (editTextMobileNumber.getText().toString().isEmpty()) {
                    showMsg("Please Enter Mobile Number");
                } else if (editTextPassword.getText().toString().length() > 5) {
                    showMsg("Please Enter Valid Mobile Number");
                } else {
                    hideKeyBoard();
                    number =
                            textViewCountryCode.getText().toString().replace("+", "")
                                    + editTextMobileNumber
                                    .getText()
                                    .toString()
                                    .replaceFirst("^0+(?!$)", "");

                    Log.e(TAG, "onViewClicked: Number " + number);
                    presenter.checkNumber(number);
                }
                break;
            case R.id.imageShowHidePassword:
                if (!isshow) {
                    editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isshow = true;
                    imageShowHidePassword.setImageResource(R.drawable.pswd_hide);
                } else {
                    editTextPassword.setInputType(
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isshow = false;
                    imageShowHidePassword.setImageResource(R.drawable.pswd_show);
                }
                break;
            case R.id.buttonSignup:
                if (editTextMobileNumber.getText().toString().isEmpty()) {
                    showMsg("Please Enter User Name");
                } else if (!editTextMobileNumber.getText().toString().contains("@")) {
                    showMsg("Please Enter Valid User Name");
                } else if (editTextPassword.getText().toString().isEmpty()) {
                    showMsg("Please Enter Password");
                } else {

                    showLoader();
                    /*
                                        if (editTextMobileNumber.getText().toString().contains("@")) {
                    */
                    String[] userdata = editTextMobileNumber.getText().toString().split("@");
                    username = userdata[0];
                    domain = userdata[1] + ".voip.belldial.co.uk";
                    /* } else {
                        username = editTextMobileNumber.getText().toString();
                        domain = "voip.belldial.co.uk";
                    }*/
                    genericLogIn(
                            username,
                            username,
                            editTextPassword.getText().toString(),
                            username,
                            null,
                            domain,
                            TransportType.Tcp);

                    hideLoader();
                    sharedPreferences.edit().putBoolean(Constant.IS_LOGIN, true).apply();
                    sharedPreferences.edit().putString(Constant.USERNAME, username).apply();
                    sharedPreferences
                            .edit()
                            .putString(Constant.PASSWORD, editTextPassword.getText().toString())
                            .apply();
                    sharedPreferences.edit().putString(Constant.DOMAIN, domain).apply();
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().finish();
                }
                break;
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        getActivity(), // Activity (for callback binding)
                        mCallbacks); // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        showLoader();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]

        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(
            String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        getActivity(), // Activity (for callback binding)
                        mCallbacks, // OnVerificationStateChangedCallbacks
                        token); // ForceResendingToken from callbacks
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    // [END resend_verification]
    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button

                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                textViewVerificationMessage.setText(
                        "A text message has been send to '\n "
                                + textViewCountryCode.getText().toString()
                                + editTextMobileNumber
                                .getText()
                                .toString()
                                .replaceFirst("^0+(?!$)", ""));
//                editTextDigit1.requestFocus();
                hideLoader();
                LayoutOTPVerification.setVisibility(View.VISIBLE);
                LayoutMobileNumber.setVisibility(View.GONE);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                hideLoader();
                break;
            case STATE_VERIFY_SUCCESS:
                // signInWithPhoneAuthCredential(cred);
                // Verification has succeeded, proceed to firebase sign in

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check

                break;
            case STATE_SIGNIN_SUCCESS:
                presenter.getDetails(requireActivity(), number);
                // Np-op, handled by sign-in check
                break;
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(
                        getActivity(),
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's
                                    // information
                                    Log.d(TAG, "signInWithCredential:success");

                                    FirebaseUser user = task.getResult().getUser();
                                    // [START_EXCLUDE]
                                    updateUI(STATE_SIGNIN_SUCCESS, user);
                                    // [END_EXCLUDE]
                                } else {
                                    // Sign in failed, display a message and update the UI
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    if (task.getException()
                                            instanceof FirebaseAuthInvalidCredentialsException) {
                                        // The verification code entered was invalid
                                        // [START_EXCLUDE silent]
                                        hideLoader();
                                        showMessage("Invalid code.");
                                        // [END_EXCLUDE]
                                    }
                                    // [START_EXCLUDE silent]
                                    // Update UI
                                    updateUI(STATE_SIGNIN_FAILED);
                                    // [END_EXCLUDE]
                                }
                            }
                        });
    }

    public void genericLogIn(
            String username,
            String userid,
            String password,
            String displayname,
            String prefix,
            String domain,
            TransportType transport) {
        saveCreatedAccount(
                username, userid, password, displayname, null, prefix, domain, transport);
    }

    public void saveCreatedAccount(
            String username,
            String userid,
            String password,
            String displayname,
            String ha1,
            String prefix,
            String domain,
            TransportType transport) {

        try {


            username = LinphoneUtils.getDisplayableUsernameFromAddress(username);
            domain = LinphoneUtils.getDisplayableUsernameFromAddress(domain);

            String identity = "sip:" + username + "@" + domain;
            address = Factory.instance().createAddress(identity);

            Log.d("asad", "aaaaaaaaaaaaaaaaaaaaasaveCreatedAccount: "+ LinphoneManager.getCore());

            AccountCreator mAccountCreator = LinphoneManager.getCore().createAccountCreator("");

            mAccountCreator.setUsername(username);
            mAccountCreator.setDomain(domain);
            mAccountCreator.setDisplayName(displayname);
            mAccountCreator.setPassword(password);

            ProxyConfig cfg = mAccountCreator.createProxyConfig();
            // String forcedProxy = "proxyserver.belldial.co.uk";
            String forcedProxy = "";
            if (!TextUtils.isEmpty(forcedProxy)) {
                // builder.setServerAddr(forcedProxy).setOutboundProxyEnabled(true).setAvpfRrInterval(5);
                cfg.setServerAddr(forcedProxy);
                cfg.done();
            }
            // Make sure the newly created one is the default
            LinphoneManager.getCore().setDefaultProxyConfig(cfg);
//            LinphoneManager.set;

            Core lc = LinphoneManager.getCore();
            for (final PayloadType pt : lc.getAudioPayloadTypes()) {
                if (pt.getMimeType().equalsIgnoreCase("pcmu")
                        || pt.getMimeType().equalsIgnoreCase("pcma")) {
                    pt.enable(true);
                } else {
                    pt.enable(false);
                }
                Log.e("Codecs", "saveCreatedAccount: " + pt.getMimeType() + " " + pt.enabled());
            }
            if (transport != null) {
                mAccountCreator.setTransport(transport);
            }
            //  mAccountCreator.setAvpfEnabled(true);
            //  builder.saveNewAccount();

            // mPrefs.setAvpfMode(0, true);
            // mPrefs.setIceEnabled(false);
            // mPrefs.setStunServer(getString(R.string.default_stun));
            // mPrefs.setStunServer(getString(R.string.default_stun));
            // mPrefs.setAccountOutboundProxyEnabled(0, false);
            mPrefs.setPushNotificationEnabled(true);
            cfg.setExpires(1800000);
            cfg.done();
            //  mPrefs.setExpires(0, "30");
            // mPrefs.setIncTimeout(30);
            // mPrefs.setWifiOnlyEnabled(false);
            // mPrefs.setTurnEnabled(true);
            mPrefs.useRandomPort(true);
            // mPrefs.enableAdaptiveRateControl(true);
            //  mPrefs.setInCallTimeout(0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("asad", "saveCreatedAccount: " + e.getMessage());
        }

    }

    @Override
    public void setCallbackSelectionGroup(String selection, int selectedId, String code) {
        textViewCountryCode.setText(code);
        textViewCountry.setText(selection);
    }

    @Override
    public void setVerification() {
        startPhoneNumberVerification(
                textViewCountryCode.getText().toString()
                        + editTextMobileNumber.getText().toString().replaceFirst("^0+(?!$)", ""));
    }

    @Override
    public void numberNotRegisterd() {
        final AlertDialog.Builder newbuilder1 = new AlertDialog.Builder(getContext());
        newbuilder1.setMessage(
                "Your number does not match our records. Please contact Belldial to setup your account.");
        newbuilder1.setCancelable(false);
        newbuilder1.setPositiveButton(
                "Ok",
                (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog al = newbuilder1.show();
    }

    @Override
    public void setDetails(Message message) {
        showLoader();

        boolean gotFBCrash = false;
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    org.linphone.core.tools.Log.i("[Push Notification] firebase token is: " + token);
                    LinphonePreferences.instance().setPushNotificationRegistrationID(token);

                    Log.d(TAG, message.getExtension() + "@" + message.getDomain());

                    if (presenter != null)
                        presenter.addpushtoken(message.getExtension() + "@" + message.getDomain(), token);
                    //GOT the token!
                }else {
                    org.linphone.core.tools.Log.e("[Push Notification] firebase getInstanceId failed: " + task.getException());
                    Log.d(TAG, "Fetching FCM registration token failed: "+task.getException());
                    return;
                }
            });
        } catch (Exception e) {
            // OMG, Firebase is used to log Firebase crash :)
            // I'm not sure if this will work...
            gotFBCrash = true;
        }
        if (gotFBCrash) {
            Toast.makeText(requireContext(), "No Firebase Token Found", Toast.LENGTH_LONG).show();
        }


        /*
         Commented By Muhammad Ans
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                org.linphone.core.tools.Log.e("[Push Notification] firebase getInstanceId failed: " + task.getException());
                Log.d(TAG, "Fetching FCM registration token failed: "+task.getException());
                return;
            }

            String token = task.getResult();
            org.linphone.core.tools.Log.i("[Push Notification] firebase token is: " + token);
            LinphonePreferences.instance().setPushNotificationRegistrationID(token);

            Log.d(TAG, message.getExtension() + "@" + message.getDomain());

            if (presenter != null)
                presenter.addpushtoken(message.getExtension() + "@" + message.getDomain(), token);
        });
*/

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });





//        FirebaseInstanceId.getInstance()
//                .getInstanceId()
//                .addOnCompleteListener(
//                        task -> {
//                            if (!task.isSuccessful()) {
//                                org.linphone.core.tools.Log.e(
//                                        "[Push Notification] firebase getInstanceId failed: "
//                                                + task.getException());
//                                return;
//                            }
//                            String token = task.getResult().getToken();
//                            org.linphone.core.tools.Log.i(
//                                    "[Push Notification] firebase token is: " + token);
//                            LinphonePreferences.instance().setPushNotificationRegistrationID(token);
//                            if (presenter != null)
//                                presenter.addpushtoken(
//                                        message.getExtension() + "@" + message.getDomain(), token);
//                        });

        Log.e(TAG, "setDetails: token " + LinphonePreferences.instance().getPushNotificationRegistrationID());
        /*
                            if (editTextMobileNumber.getText().toString().contains("@")) {
        */
        // String[] userdata = editTextMobileNumber.getText().toString().split("@");
        username = message.getExtension();
        // domain = message.getDomain() + ".voip.belldial.co.uk";
        domain = message.getDomain();
        Log.e(TAG, "===================================================");
        Log.e(TAG, "setDetails: UserName:-" + username + "  Domain:-" + domain);
        Log.e(TAG, "===================================================");
        /* } else {
            username = editTextMobileNumber.getText().toString();
            domain = "voip.belldial.co.uk";
        }*/
        genericLogIn(
                username,
                username,
                message.getPassword(),
                username,
                null,
                domain,
                TransportType.Tcp);
        hideLoader();
        sharedPreferences.edit().putBoolean(Constant.IS_LOGIN, true).apply();
        sharedPreferences.edit().putString(Constant.USERNAME, username).apply();
        sharedPreferences.edit().putString(Constant.PASSWORD, message.getPassword()).apply();
        sharedPreferences.edit().putString(Constant.MOBILE, message.getPhoneNumber()).apply();
        sharedPreferences.edit().putString(Constant.DOMAIN, domain).apply();
        startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }

    @Override
    public void detailsNotFound() {
        final AlertDialog.Builder newbuilder1 = new AlertDialog.Builder(getContext());
        newbuilder1.setMessage("User Sip Details Not Found.");
        newbuilder1.setCancelable(false);
        newbuilder1.setPositiveButton(
                "Ok",
                (dialog, which) -> {
                    dialog.dismiss();
                });
         newbuilder1.show();
    }

    @OnClick(R.id.textViewResendCode)
    public void onViewClicked() {
        resendVerificationCode(
                textViewCountryCode.getText().toString()
                        + editTextMobileNumber.getText().toString().replaceFirst("^0+(?!$)", ""),
                mResendToken);
    }
}
