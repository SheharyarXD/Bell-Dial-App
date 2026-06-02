package belldial.co.uk.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.ContactDetailsPresenter;
import belldial.co.uk.ui.views.ContactDetailsView;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.contacts.LinphoneContact;
import belldial.co.uk.contacts.LinphoneNumberOrAddress;
import belldial.co.uk.utils.LinphoneUtils;
import butterknife.BindView;
import butterknife.OnClick;

import belldial.co.uk.R;

import belldial.co.uk.ui.activity.CallOutgoingActivity;
import belldial.co.uk.ui.activity.HomeActivity;
import belldial.co.uk.utils.SimpleCoreListener;

import org.linphone.core.Alert;
import org.linphone.core.Call;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import org.linphone.core.Account;
import org.linphone.core.Friend;
import org.linphone.core.RegistrationState;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailsFragment
        extends BaseFragment<ContactDetailsPresenter, ContactDetailsView>
        implements ContactDetailsView {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.imageViewMainDetails)
    AppCompatImageView imageViewMainDetails;

    @BindView(R.id.textViewName)
    AppCompatTextView textViewName;

    @BindView(R.id.textViewMobileNumber)
    AppCompatTextView textViewMobileNumber;

    @BindView(R.id.btnSms)
    AppCompatImageView btnSms;

    @BindView(R.id.controls)
    TableLayout controls;

    String displayednumberOrAddress;
    SharedPreferences sharedPreferences;

    private SimpleCoreListener mListener;

    private static LinphoneContact linphoneContact;

    private static final int CALL_PERMISSION_REQUEST_CODE = 102;
    private String pendingCallNumber;
    private String pendingCallDisplayName;

    public static LinphoneContact getLinphoneContact() {
        return linphoneContact;
    }

    public static void setLinphoneContact(LinphoneContact linphoneContact) {
        ContactDetailsFragment.linphoneContact = linphoneContact;
    }

    @Override
    protected int createLayout() {
        return R.layout.fragment_contact_details;
    }

    @Override
    protected void setPresenter() {
        presenter = new ContactDetailsPresenter();
    }

    @Override
    protected ContactDetailsView createView() {
        return this;
    }

    @SuppressLint("Range")
    @Override
    protected void bindData() {
        ((HomeActivity) getActivity()).hideTabBar(true);
        toolBarTitle.setText("Contacts");
        if (linphoneContact != null)
            textViewName.setText(linphoneContact.getFullName());
        sharedPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
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

                if (lc.getAccountList() == null) {
                    imageViewSwitchOff.setImageResource(R.drawable.signal_null);

                } else {
                    // statusLed.setVisibility(View.VISIBLE);
                }

                if (lc.getDefaultAccount() != null
                        && lc.getDefaultAccount().equals(account)) {
                    imageViewSwitchOff.setImageResource(
                            Constant.getStatusIconResource(state, true));
                } else if (lc.getDefaultAccount() == null) {
                    imageViewSwitchOff.setImageResource(
                            Constant.getStatusIconResource(state, true));
                }
            }

            @Override
            public void onCallStateChanged(
                    Core lc, Call call, Call.State cstate, String message) {
                // super.onCallStateChanged(lc, call, cstate, message);
                if (cstate == Call.State.End) {
                    Log.e("CallListener", "End");
                    ((HomeActivity) getActivity()).showActiveCall(false);

                } else if (cstate == Call.State.Released) {
                    ((HomeActivity) getActivity()).showActiveCall(false);

                } else if (cstate == Call.State.Error) {
                    ((HomeActivity) getActivity()).showActiveCall(false);
                }
            }

            @Override
            public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
                // super.onCallStatsUpdated(lc, call, stats);
                if (call == null) {
                    ((HomeActivity) getActivity()).showActiveCall(false);
                } else {
                    ((HomeActivity) getActivity()).showActiveCall(true);
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

        }

        controls.removeAllViews();
        if (linphoneContact != null)
            for (LinphoneNumberOrAddress noa : linphoneContact.getNumbersOrAddresses()) {
                View v = getActivity().getLayoutInflater().inflate(R.layout.row_contact, null);
                boolean skip = false;

                AppCompatTextView contactNumber = v.findViewById(R.id.textViewMobileNumber);
                final AppCompatTextView contactType = v.findViewById(R.id.textViewType);
                AppCompatImageView callButton = v.findViewById(R.id.imageViewCall);
                AppCompatImageView smsButton = v.findViewById(R.id.btnSms);

                String value = noa.getValue();

                displayednumberOrAddress = LinphoneUtils.getDisplayableUsernameFromAddress(value);

                Cursor cursor = getContext()
                        .getContentResolver()
                        .query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[] { linphoneContact.getAndroidId() },
                                null);

                while (cursor.moveToNext()) {

                    try {

                        if (cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                .equals(displayednumberOrAddress)) {
                            @SuppressLint("Range")
                            String type = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                                    getContext().getResources(),
                                    Integer.parseInt(cursor
                                            .getString(cursor
                                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))),
                                    "");
                            contactType.setText(type);
                            break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("asad", "bindData: " + e.getMessage());
                    }

                }
                cursor.close();
                contactNumber.setText(displayednumberOrAddress);
                callButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkAndRequestCallPermissions(
                                        displayednumberOrAddress.replaceAll("\\D+", ""),
                                        displayednumberOrAddress);
                            }
                        });
                smsButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MessagesResponse messagesResponse = new MessagesResponse();
                                messagesResponse.setFullName(textViewName.getText().toString());
                                messagesResponse.setReceiverNumber(
                                        contactNumber.getText().toString());
                                ((HomeActivity) requireActivity())
                                        .openSmsFragmentFromContactDetails(messagesResponse);
                                HomeActivity.isCaseContactDetails = true;
                            }
                        });

                if (!skip) {
                    controls.addView(v);
                }
            }
    }

    @OnClick(R.id.imageViewBack)
    public void onViewClicked() {
        requireActivity().onBackPressed();
    }

    @OnClick(R.id.btnSms)
    public void sendSms() {
        MessagesResponse messagesResponse = new MessagesResponse();
        messagesResponse.setFullName(textViewName.getText().toString());
        messagesResponse.setReceiverNumber(textViewMobileNumber.getText().toString());
        ((HomeActivity) requireActivity()).openSmsFragmentFromContactDetails(messagesResponse);
        HomeActivity.isCaseContactDetails = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.addListener(mListener);
            Account lpc = lc.getDefaultAccount();
            if (lpc != null) {
                mListener.onAccountRegistrationStateChanged(lc, lpc, lpc.getState(), null);
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

    private void checkAndRequestCallPermissions(String number, String displayName) {
        String[] permissions = { Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE };
        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            pendingCallNumber = number;
            pendingCallDisplayName = displayName;
            requestPermissions(permissions, CALL_PERMISSION_REQUEST_CODE);
        } else {
            proceedWithCall(number, displayName);
        }
    }

    private void proceedWithCall(String number, String displayName) {
        if (Constant.TRANSFER == 1) {
            Core core = LinphoneManager.getCore();
            if (core.getCurrentCall() == null) {
                return;
            }
            core.getCurrentCall().transfer(number);
            showMessage("Call Transfer to " + number);
            Constant.TRANSFER = 0;
        } else {
            if (LinphoneManager.getCore().getCallsNb() < 2) {
                LinphoneManager.getCallManager().newOutgoingCall(
                        "sip:" + number + "@" + sharedPreferences.getString(Constant.DOMAIN, ""),
                        displayName);
                Intent intent = new Intent(getActivity(), CallOutgoingActivity.class);
                startActivity(intent);
            }
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
            if (allGranted && pendingCallNumber != null) {
                proceedWithCall(pendingCallNumber, pendingCallDisplayName);
                pendingCallNumber = null;
                pendingCallDisplayName = null;
            } else if (!allGranted) {
                showMessage("Permissions required to make a call.");
            }
        }
    }
}
