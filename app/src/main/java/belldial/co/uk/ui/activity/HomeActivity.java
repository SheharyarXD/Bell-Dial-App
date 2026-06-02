package belldial.co.uk.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.api.ApiService;
import belldial.co.uk.api.RetroClient;
import belldial.co.uk.api.model.AddTokenResponse;
import belldial.co.uk.api.model.GetDetailsResponse;
import belldial.co.uk.api.model.GetSmsDetailsResponse;
import belldial.co.uk.api.model.GetSmsHistoryResponse;
import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.api.model.Sms;
import belldial.co.uk.api.model.SmsDetails;
import belldial.co.uk.api.model.SmsMessage;
import belldial.co.uk.ui.fragments.AcoountFragment;
import belldial.co.uk.ui.fragments.AdvancedFragment;
import belldial.co.uk.ui.fragments.AudioFragment;
import belldial.co.uk.ui.fragments.CallRecordingFragment;
import belldial.co.uk.ui.fragments.CallSFragment;
import belldial.co.uk.ui.fragments.ContactDetailsFragment;
import belldial.co.uk.ui.fragments.NetworkFragment;
import belldial.co.uk.ui.fragments.SmsFragment;
import belldial.co.uk.ui.model.ContactAddress;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.utils.SimpleCoreListener;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import belldial.co.uk.contacts.LinphoneNumberOrAddress;
import belldial.co.uk.utils.LinphoneUtils;
import org.linphone.core.Account;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import belldial.co.uk.R;

import belldial.co.uk.ui.provider.AppNavigationProvider;

import org.linphone.core.Core;
import org.linphone.core.RegistrationState;

import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppNavigationProvider {
    ImageView tabItemImgSettings;
    boolean hide = false;
    SharedPreferences prefs;
    String tab;

    @BindView(R.id.placeHolder)
    FrameLayout placeHolder;

    @BindView(R.id.tabItemImgRecent)
    AppCompatImageView tabItemImgRecent;

    @BindView(R.id.tabItemImgDial)
    AppCompatImageView tabItemImgDial;

    @BindView(R.id.tabItemImgContact)
    AppCompatImageView tabItemImgContact;

    @BindView(R.id.tabItemImgSms)
    AppCompatImageView tabItemImgSms;

    // @BindView(R.id.tabItemImgSettings)
    // AppCompatImageView tabItemImgSettings;

    @BindView(R.id.tabItemImgMeeting)
    AppCompatImageView tabItemImgMeeting;

    @BindView(R.id.layoutBottomBar)
    LinearLayout layoutBottomBar;

    boolean moveToDail = false, transfer = false;

    @BindView(R.id.layoutActiveCall)
    LinearLayout layoutActiveCall;

    private SimpleCoreListener mListener;

    SmsDetails smsDetails;

    SmsMessage smsMessage;

    List<Sms> smsList = new ArrayList<>();

    SharedPreferences sharedPreferences;
    public static boolean isCaseContactDetails = false;

    private List<ContactAddress> linphoneContacts;

    @Override
    public int getPlaceHolder() {
        return R.id.placeHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        tabItemImgRecent.setImageResource(R.drawable.recent_sel);
        tabItemImgDial.setImageResource(R.drawable.keypad);
        tabItemImgContact.setImageResource(R.drawable.contact_new);
        tabItemImgSms.setImageResource(R.drawable.smsunsel);
        tabItemImgMeeting.setImageResource(R.drawable.meeting);
        tabItemImgSettings = findViewById(R.id.tabItemImgSettings);
        prefs = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        tabItemImgSettings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettingsFragment(PerformFragment.REPLACE);
                        tabItemImgSettings.setVisibility(View.GONE);
                        hideTabBar(true);
                        // back.setVisibility(View.VISIBLE);
                    }
                });
        //

        // tabItemImgSettings.setImageResource(R.drawable.setting);
        openAllCallsFragment(PerformFragment.REPLACE);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);

        String orderId = sharedPreferences.getString(Constant.ORDER_ID, "not_found");
        String productId = sharedPreferences.getString(Constant.PRODUCT_ID, "not_found");

        if (orderId.equals("not_found") || productId.equals("not_found")) {
            getDetails(sharedPreferences.getString(Constant.MOBILE, "not_found"));
        } else {
            getSmsDetails(orderId, productId);
        }

        if (LinphoneService.isReady()) {
            Core lc = LinphoneManager.getCore();
            if (lc != null) {
                lc.addListener(mListener);
            }
        }

        if (getIntent().getExtras() != null) {

            moveToDail = getIntent().getExtras().getBoolean(Constant.MOVETODIAL);
            transfer = getIntent().getExtras().getBoolean(Constant.MOVETODIALTRANSFER);
            if (moveToDail || transfer) {
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad_sel);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
                // tabItemImgSettings.setImageResource(R.drawable.setting);
                if (transfer) {
                    Constant.TRANSFER = 1;
                } else {
                    Constant.TRANSFER = 0;
                }
                openDialerFragment(PerformFragment.REPLACE);
            }
        }

        mListener = new SimpleCoreListener() {
            @Override
            public void onMessageReceived(Core lc, ChatRoom cr, ChatMessage message) {
                // displayMissedChats(LinphoneManager.getInstance().getUnreadMessageCount());
            }

            @Override
            public void onAccountRegistrationStateChanged(
                    Core lc, Account proxy, RegistrationState state, String smessage) {
                // Stub or uncomment and fix logic if needed
            }

            @Override
            public void onCallStateChanged(Core lc, Call call, Call.State state, String message) {
                android.util.Log.e("check", "onCallStateChanged: " + state);
                if (state == org.linphone.core.Call.State.IncomingReceived) {
                    Intent intent = new Intent(HomeActivity.this, CallIncomingActivity.class);
                    intent.putExtra("from", "home");
                    startActivity(intent);
                } else if (state == org.linphone.core.Call.State.OutgoingInit
                        || state == org.linphone.core.Call.State.OutgoingProgress) {
                    startActivity(
                            new Intent(HomeActivity.this, CallOutgoingActivity.class));
                    /*
                     * } else if (state == State.End || state == State.Error || state ==
                     * State.Released) {
                     * resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
                     * }
                     */

                    // int missedCalls = LinphoneManager.getLc().getMissedCallsCount();
                    // displayMissedCalls(missedCalls);
                }
            }
        };
        if (checkContactPermission()) {
            if (LinphoneService.isReady())
                GetContactsIntoArrayList();
        }

        // throw new RuntimeException("Test Crash");
    }

    public List<ContactAddress> getContactsListData() {
        List<ContactAddress> list = new ArrayList<ContactAddress>();
        if (ContactsManager.getInstance().hasReadContactsAccess()) {
            for (LinphoneContact con : ContactsManager.getInstance().getContacts()) {
                for (LinphoneNumberOrAddress noa : con.getNumbersOrAddresses()) {
                    String value = noa.getValue();
                    // Fix for sip:username compatibility issue
                    if (value.startsWith("sip:") && !value.contains("@")) {
                        value = value.substring(4);
                        value = LinphoneUtils.getFullAddressFromUsername(value);
                    }
                    list.add(new ContactAddress(con, value));
                }
            }
        }
        return list;
    }

    public void getDetails(String Number) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");
        headers.put("Phonenumber", Number);

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<GetDetailsResponse> call = api.getDetails(headers);
        call.enqueue(
                new Callback<GetDetailsResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<GetDetailsResponse> call,
                            Response<GetDetailsResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("Success")) {
                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
                                sharedPreferences
                                        .edit()
                                        .putString(
                                                Constant.ORDER_ID,
                                                response.body().getMessage().getOrderId())
                                        .apply();
                                sharedPreferences
                                        .edit()
                                        .putString(
                                                Constant.PRODUCT_ID,
                                                response.body()
                                                        .getMessage()
                                                        .getProducts()
                                                        .get(0)
                                                        .getId())
                                        .apply();

                                getSmsDetails(
                                        response.body().getMessage().getOrderId(),
                                        response.body().getMessage().getProducts().get(0).getId());
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<GetDetailsResponse> call, Throwable throwable) {
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    public boolean checkContactPermission() {
        int readContacts = getPackageManager()
                .checkPermission(Manifest.permission.READ_CONTACTS, getPackageName());
        int writeContacts = getPackageManager()
                .checkPermission(Manifest.permission.WRITE_CONTACTS, getPackageName());
        return readContacts == PackageManager.PERMISSION_GRANTED
                && writeContacts == PackageManager.PERMISSION_GRANTED;
    }

    public void showActiveCall(boolean isHide) {
        if (isHide) {
            layoutActiveCall.setVisibility(View.VISIBLE);
        } else {
            layoutActiveCall.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // checkAppStatus();
        if (LinphoneService.isReady()) {
            Core lc = LinphoneManager.getCore();
            if (lc != null) {
                lc.addListener(mListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LinphoneService.isReady()) {
            Core lc = LinphoneManager.getCore();
            if (lc != null) {
                lc.removeListener(mListener);
            }
        }
    }

    @OnClick({
            R.id.tabItemImgRecent,
            R.id.tabItemImgDial,
            R.id.tabItemImgContact,
            R.id.tabItemImgSms,
            // R.id.tabItemImgSettings,
            R.id.tabItemImgMeeting,
            R.id.layoutActiveCall
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tabItemImgRecent:
                tabItemImgRecent.setImageResource(R.drawable.recent_sel);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                // tabItemImgSettings.setImageResource(R.drawable.setting);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
                openAllCallsFragment(PerformFragment.REPLACE);
                tab = "1";

                break;
            case R.id.tabItemImgDial:
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad_sel);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                // tabItemImgSettings.setImageResource(R.drawable.setting);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
                openDialerFragment(PerformFragment.REPLACE);
                tab = "2";
                break;

            case R.id.tabItemImgContact:
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_sel);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
                // tabItemImgSettings.setImageResource(R.drawable.setting);
                openContactListFragment(PerformFragment.REPLACE);
                tab = "3";
                break;

            case R.id.tabItemImgSms:
                tab = "4"; {
                if (smsMessage != null) {
                    tabItemImgRecent.setImageResource(R.drawable.recent);
                    tabItemImgDial.setImageResource(R.drawable.keypad);
                    tabItemImgContact.setImageResource(R.drawable.contact_new);
                    tabItemImgSms.setImageResource(R.drawable.smssel);
                    // tabItemImgSettings.setImageResource(R.drawable.setting);
                    tabItemImgMeeting.setImageResource(R.drawable.meeting);

                    if (smsMessage.getContactList().isEmpty()) {
                        openSmsFragment(
                                PerformFragment.REPLACE, smsMessage, smsDetails, smsList, null);
                    } else {
                        openContactsSmsFragment(
                                PerformFragment.REPLACE,
                                smsDetails,
                                smsMessage.getContactList());
                    }
                }
            }
                break;

            case R.id.tabItemImgMeeting:
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting_sel);
                tabItemImgSettings.setVisibility(View.VISIBLE);
                // tabItemImgSettings.setImageResource(R.drawable.settingsel);
                openMeetingFragment(PerformFragment.REPLACE);
                tab = "5";
                break;
            case R.id.layoutActiveCall:
                org.linphone.core.Call currentCall = LinphoneManager.getCore().getCurrentCall();
                if (currentCall != null) {
                    if (currentCall.getState() == org.linphone.core.Call.State.IncomingReceived) {
                        Intent intent = new Intent(this, CallIncomingActivity.class);
                        intent.putExtra("from", "home");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, CallOutgoingActivity.class);
                        startActivity(intent);
                    }
                }
        }
    }

    public void GetContactsIntoArrayList() {

        try {

            if (LinphoneService.isReady()) {
                ContactsManager.getInstance().initializeSyncAccount();
                // initPushNotificationsService();

            }
            if (LinphoneService.isReady()) {
                ContactsManager.getInstance().initializeContactManager();
            }

            ContactsManager.getInstance().enableContactsAccess();
            if (!ContactsManager.getInstance().contactsFetchedOnce()) {
                ContactsManager.getInstance().enableContactsAccess();
                ContactsManager.getInstance().fetchContactsAsync();
            }
            // public List<ChatFragment.ContactAddress> getContactsList() {
            // List<ChatFragment.ContactAddress> list = new
            // ArrayList<ChatFragment.ContactAddress>();
            if (ContactsManager.getInstance().hasReadContactsAccess()) {
                for (LinphoneContact con : ContactsManager.getInstance().getContacts()) {
                    for (LinphoneNumberOrAddress noa : con.getNumbersOrAddresses()) {
                        String value = noa.getValue();
                        // Fix for sip:username compatibility issue
                        if (value.startsWith("sip:") && !value.contains("@")) {
                            value = value.substring(4);
                            value = LinphoneUtils.getFullAddressFromUsername(value);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("asad", "GetContactsIntoArrayList: " + e.getMessage());
        }

    }

    /*
     * public void checkAppStatus() {
     * 
     * ApiService api = RetroClient.getApiService();
     * Call<String> call = api.checkStatus();
     * call.enqueue(
     * new Callback<String>() {
     * 
     * @Override
     * public void onResponse(Call<String> call, Response<String> response) {
     * 
     * if (!response.body().equalsIgnoreCase("200")) {
     * final AlertDialog.Builder newbuilder1 =
     * new AlertDialog.Builder(HomeActivity.this);
     * newbuilder1.setMessage(
     * "Your license has expired. Please contact CelloIP Technologies at info@celloip.com to renew your license."
     * );
     * newbuilder1.setCancelable(false);
     * newbuilder1.setPositiveButton(
     * "Ok",
     * (dialog, which) -> {
     * dialog.dismiss();
     * finish();
     * System.exit(0);
     * });
     * AlertDialog al = newbuilder1.show();
     * al.setCanceledOnTouchOutside(false);
     * }
     * }
     * 
     * @Override
     * public void onFailure(Call<String> call, Throwable throwable) {
     * Log.e("onFailure", "Reason: " + throwable.getMessage());
     * }
     * });
     * }
     */

    public void addpushtoken(String Number, String token) {
        // view.showLoader();
        /*
         * HashMap<String, String> headers = new HashMap<String, String>();
         * headers.put("username", Number);
         * headers.put("pushtoken", token);
         * headers.put("platform", "android");
         */

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User", Number);
        headers.put("PnTok", token);
        headers.put("PnTtype", "android");

        ApiService api = RetroClient.getApiService();
        // retrofit2.Call<AddTokenResponse> call = api.addPushToken(headers);
        JsonObject data = new JsonObject();
        data.addProperty("User", Number);
        data.addProperty("PnTok", token);
        data.addProperty("PnTtype", "android");
        retrofit2.Call<AddTokenResponse> call = api.addBelldialToken(data);
        call.enqueue(
                new Callback<AddTokenResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<AddTokenResponse> call,
                            Response<AddTokenResponse> response) {
                        // view.hideLoader();
                        /*
                         * if (response.body().getStatus().equalsIgnoreCase("Success")) {
                         * view.setDetails(response.body().getMessage());
                         * } else {
                         * view.detailsNotFound();
                         * }
                         */
                        Log.e("onSuccess", "onSuccess: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<AddTokenResponse> call, Throwable throwable) {
                        // view.hideLoader();
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    public void hideTabBar(boolean isHide) {
        if (isHide) {
            hide = true;
            layoutBottomBar.setVisibility(View.GONE);
        } else {
            hide = false;
            layoutBottomBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (hide == true) {
            if (tab == null) {
                tab = "1";
            }
            if (tab.equals("1")) {
                openAllCallsFragment(PerformFragment.REPLACE);
                tabItemImgRecent.setImageResource(R.drawable.recent_sel);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
            }
            if (tab.equals("2")) {
                openDialerFragment(PerformFragment.REPLACE);
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad_sel);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
            }
            if (tab.equals("3")) {
                openContactListFragment(PerformFragment.REPLACE);
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_sel);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
            }
            if (tab.equals("5")) {
                openMeetingFragment(PerformFragment.REPLACE);
                tabItemImgRecent.setImageResource(R.drawable.recent);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting_sel);
            }
            if (tab.equals("4")) {
                tabItemImgRecent.setImageResource(R.drawable.recent_sel);
                tabItemImgDial.setImageResource(R.drawable.keypad);
                tabItemImgContact.setImageResource(R.drawable.contact_new);
                tabItemImgSms.setImageResource(R.drawable.smsunsel);
                tabItemImgMeeting.setImageResource(R.drawable.meeting);
                {
                    if (smsMessage != null) {
                        tabItemImgRecent.setImageResource(R.drawable.recent);
                        tabItemImgDial.setImageResource(R.drawable.keypad);
                        tabItemImgContact.setImageResource(R.drawable.contact_new);
                        tabItemImgSms.setImageResource(R.drawable.sms_selected);
                        // tabItemImgSettings.setImageResource(R.drawable.setting);
                        tabItemImgMeeting.setImageResource(R.drawable.meeting);

                        if (smsMessage.getContactList().isEmpty()) {
                            openSmsFragment(
                                    PerformFragment.REPLACE, smsMessage, smsDetails, smsList, null);
                        } else {
                            openContactsSmsFragment(
                                    PerformFragment.REPLACE,
                                    smsDetails,
                                    smsMessage.getContactList());
                        }
                    }
                }
            }

            tabItemImgSettings.setVisibility(View.VISIBLE);
            hideTabBar(false);

        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeHolder);
            if (fragment instanceof ContactDetailsFragment
                    || fragment instanceof AcoountFragment
                    || fragment instanceof AudioFragment
                    || fragment instanceof CallRecordingFragment
                    || fragment instanceof CallSFragment
                    || fragment instanceof NetworkFragment
                    || fragment instanceof AdvancedFragment) {
                if (layoutBottomBar.getVisibility() == View.GONE) {
                    layoutBottomBar.setVisibility(View.VISIBLE);
                }

                if (fragment instanceof ContactDetailsFragment) {
                    openContactListFragment(PerformFragment.REPLACE);
                } else {
                    super.onBackPressed();
                }
            } else if (fragment instanceof SmsFragment && !smsMessage.getContactList().isEmpty()) {
                if (isCaseContactDetails) {
                    isCaseContactDetails = false;
                    openContactDetailsFragment(PerformFragment.REPLACE);
                } else {
                    openContactsSmsFragment(
                            PerformFragment.REPLACE, smsDetails, smsMessage.getContactList());
                }
            } else {

                // super.onBackPressed();
                // finish();
                this.finishAffinity();
            }
        }
    }

    // Sms Fragment Work
    public void getSmsDetails(String orderId, String productId) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");
        headers.put("OrderId", orderId);
        headers.put("ProductId", productId);

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<GetSmsDetailsResponse> call = api.getSmsDetails(headers);
        call.enqueue(
                new Callback<GetSmsDetailsResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<GetSmsDetailsResponse> call,
                            Response<GetSmsDetailsResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getStatus().equalsIgnoreCase("Success")) {
                                smsDetails = response.body().getSmsDetails();
                                getSmsHistory();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<GetSmsDetailsResponse> call, Throwable throwable) {
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    public void getSmsHistory() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");
        headers.put("Userid", smsDetails.getUserId());

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<GetSmsHistoryResponse> call = api.getSmsHistory(headers);
        call.enqueue(
                new Callback<GetSmsHistoryResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<GetSmsHistoryResponse> call,
                            Response<GetSmsHistoryResponse> response) {
                        try {
                            if (response.body().getStatus().equalsIgnoreCase("Success")) {
                                smsMessage = response.body().getMessage();

                                linphoneContacts = getContactsListData();
                                formatContactList();
                            } else {
                                Toast.makeText(HomeActivity.this, "Failed...", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<GetSmsHistoryResponse> call, Throwable throwable) {
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                        Toast.makeText(HomeActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void formatContactList() {
        for (MessagesResponse msgResponse : smsMessage.getContactList()) {
            if (msgResponse.getReceiverNumber().charAt(0) == '7') {
                msgResponse.setReceiverNumber("0" + msgResponse.getReceiverNumber());
            }

            for (ContactAddress contactAddress : linphoneContacts) {
                if (contactAddress.address.trim().equals(msgResponse.getReceiverNumber().trim())) {
                    msgResponse.setFullName(contactAddress.contact.getFullName());
                }
            }
        }
    }

    public void openSmsFragmentFromAdapter(MessagesResponse messagesResponse, int position) {
        openSmsFragment(
                PerformFragment.REPLACE,
                smsMessage,
                smsDetails,
                smsMessage.getContactList().get(position).getSmsList(),
                messagesResponse);
    }

    public void openSmsFragmentEmptyFromAdapter(MessagesResponse messagesResponse) {
        openSmsFragment(
                PerformFragment.REPLACE,
                smsMessage,
                smsDetails,
                new ArrayList<>(),
                messagesResponse);
    }

    public void openSmsFragmentFromContactDetails(MessagesResponse m) {
        for (MessagesResponse messagesResponse : smsMessage.getContactList()) {
            if (m.getReceiverNumber().equals(messagesResponse.getReceiverNumber())) {
                openSmsFragment(
                        PerformFragment.REPLACE,
                        smsMessage,
                        smsDetails,
                        messagesResponse.getSmsList(),
                        messagesResponse);
                return;
            }
        }

        openSmsFragment(PerformFragment.REPLACE, smsMessage, smsDetails, new ArrayList<>(), m);
    }
}
