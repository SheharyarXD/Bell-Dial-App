package belldial.co.uk.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.activities.SmsToEmailActivity;
import belldial.co.uk.api.ApiService;
import belldial.co.uk.api.RetroClient;
import belldial.co.uk.api.model.AddTokenResponse;
import belldial.co.uk.ui.activity.HomeActivity;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.SettingsPresenter;
import belldial.co.uk.ui.views.SettingsView;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.settings.LinphonePreferences;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.gson.JsonObject;

import belldial.co.uk.R;

import belldial.co.uk.ui.activity.AuthActivity;

import org.linphone.core.AccountCreator;
import org.linphone.core.Account;
import org.linphone.core.Call;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.mediastream.Version;

import retrofit2.Callback;
import retrofit2.Response;

/** A simple {@link Fragment} subclass. */
public class SettingsFragment extends BaseFragment<SettingsPresenter, SettingsView>
        implements SettingsView {

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    SharedPreferences sharedPreferences;

    @BindView(R.id.toolbar_back_btn)
    Toolbar toolbar;

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.layoutAccount)
    LinearLayout layoutAccount;

    @BindView(R.id.layoutAudio)
    LinearLayout layoutAudio;

    @BindView(R.id.layoutVideo)
    LinearLayout layoutVideo;

    @BindView(R.id.layoutCall)
    LinearLayout layoutCall;

    @BindView(R.id.layoutNetwork)
    LinearLayout layoutNetwork;

    @BindView(R.id.layoutAdvanced)
    LinearLayout layoutAdvanced;

    @BindView(R.id.layoutLogout)
    LinearLayout layoutLogout;

    @BindView(R.id.layoutSmsToEmail)
    LinearLayout layoutSmsToEmail;
    @BindView(R.id.layoutOverDraw)
    LinearLayout layoutOverDraw;

    private SimpleCoreListener mListener;
    LinphonePreferences linphonePreferences;

    ActivityResultLauncher<Intent> onSettingsIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (Settings.canDrawOverlays(requireActivity())) {
                    layoutOverDraw.setVisibility(View.GONE);
                } else {
                    layoutOverDraw.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected int createLayout() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void setPresenter() {
        presenter = new SettingsPresenter();
    }

    @Override
    protected SettingsView createView() {
        return this;
    }

    @Override
    protected void bindData() {

        toolBarTitle.setText("More Options");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Settings.canDrawOverlays(requireActivity())) {
                layoutOverDraw.setVisibility(View.GONE);
            } else {
                layoutOverDraw.setVisibility(View.VISIBLE);
                layoutOverDraw.setOnClickListener(view -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName"));
                    onSettingsIntentLauncher.launch(intent);
                });
            }

        } else {
            layoutOverDraw.setVisibility(View.GONE);
        }

        layoutSmsToEmail.setOnClickListener(
                view -> {
                    Intent intent = new Intent(getContext(), SmsToEmailActivity.class);
                    startActivity(intent);
                });

        linphonePreferences = LinphonePreferences.instance();
        sharedPreferences = getActivity().getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
        mListener = new SimpleCoreListener() {
            @Override
            public void onAccountRegistrationStateChanged(
                    Core lc, Account cfg, RegistrationState state, String smessage) {
                if (!LinphoneService.isReady()) {
                    return;
                }

                if (lc.getAccountList() == null || lc.getAccountList().length == 0) {
                    imageViewStatus.setImageResource(R.drawable.signal_null);

                } else {
                    // statusLed.setVisibility(View.VISIBLE);
                }

                if (lc.getDefaultAccount() != null
                        && lc.getDefaultAccount().equals(cfg)) {
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
    }

    @OnClick({
            R.id.layoutAccount,
            R.id.layoutAudio,
            R.id.layoutVideo,
            R.id.layoutCall,
            R.id.layoutNetwork,
            R.id.layoutAdvanced,
            R.id.layoutLogout
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layoutAccount:
                presenter.openAccount();
                break;
            case R.id.layoutAudio:
                presenter.openAudio();
                break;
            case R.id.layoutVideo:
                presenter.openVideo();
                break;
            case R.id.layoutCall:
                presenter.openCall();
                break;
            case R.id.layoutNetwork:
                presenter.openNetwork();
                break;
            case R.id.layoutAdvanced:
                presenter.openAdvanced();
                break;
            case R.id.layoutLogout:

                try {
                    final AlertDialog.Builder newbuilder1 = new AlertDialog.Builder(getActivity());
                    newbuilder1.setMessage("Are you sure you want to Logout?");
                    newbuilder1.setPositiveButton(
                            "Yes",
                            (dialog, which) -> {
                                deletepushtoken(
                                        sharedPreferences.getString(Constant.USERNAME, "")
                                                + "@"
                                                + sharedPreferences.getString(Constant.DOMAIN, ""));
                                Core core = LinphoneManager.getCore();
                                if (core != null) {
                                    core.setDefaultProxyConfig(null);
                                    core.clearAllAuthInfo();
                                    core.clearProxyConfig();
                                }
                                AccountCreator mAccountCreator = LinphoneManager.getCore().createAccountCreator(null);
                                mAccountCreator.recoverAccount();
                                linphonePreferences.setPushNotificationRegistrationID("");
                                linphonePreferences.setPushNotificationEnabled(false);
                                sharedPreferences.edit().putBoolean(Constant.IS_LOGIN, false).apply();
                                sharedPreferences.edit().clear().apply();
                                Intent intent1 = new Intent(getActivity(), AuthActivity.class);
                                getActivity().startActivity(intent1);
                                getActivity().finish();
                            });
                    newbuilder1.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    newbuilder1.show();
                } catch (Exception e) {

                }

                break;
        }
    }

    public void deletepushtoken(String Number) {

        ApiService api = RetroClient.getApiService();
        JsonObject data = new JsonObject();
        data.addProperty("User", Number);
        retrofit2.Call<AddTokenResponse> call = api.deleteBelldialToken(data);
        call.enqueue(
                new Callback<AddTokenResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<AddTokenResponse> call,
                            Response<AddTokenResponse> response) {
                        Log.e("onSuccess", "onSuccess: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<AddTokenResponse> call, Throwable throwable) {
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.addListener(mListener);
            Account account = lc.getDefaultAccount();
            if (account != null) {
                mListener.onAccountRegistrationStateChanged(lc, account, account.getState(), null);
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
