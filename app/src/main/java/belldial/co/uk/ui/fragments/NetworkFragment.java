package belldial.co.uk.ui.fragments;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.SettingsPresenter;
import belldial.co.uk.ui.views.SettingsView;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.DeviceUtils;
import belldial.co.uk.utils.PushNotificationUtils;
import butterknife.BindView;
import butterknife.OnClick;
import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.tools.Log;

/** A simple {@link Fragment} subclass. */
public class NetworkFragment extends BaseFragment<SettingsPresenter, SettingsView>
        implements SettingsView {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.switchUseWifiOnly)
    SwitchCompat switchUseWifiOnly;

    @BindView(R.id.switchAllowIPV6)
    SwitchCompat switchAllowIPV6;

    @BindView(R.id.switchEnablePush)
    SwitchCompat switchEnablePush;

    @BindView(R.id.switchRandomPort)
    SwitchCompat switchRandomPort;

    @BindView(R.id.et_sipPortUse)
    AppCompatEditText etSipPortUse;

    @BindView(R.id.layoutSipPortTouse)
    LinearLayout layoutSipPortTouse;

    @BindView(R.id.switchEnableICE)
    SwitchCompat switchEnableICE;

    @BindView(R.id.switchEnableTURN)
    SwitchCompat switchEnableTURN;

    @BindView(R.id.etStunServer)
    AppCompatEditText etStunServer;

    @BindView(R.id.layoutBattery)
    LinearLayout layoutBattery;

    private LinphonePreferences mPrefs;

    @Override
    protected int createLayout() {
        return R.layout.fragment_network;
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
        imageViewSwitchOff.setVisibility(View.GONE);
        toolBarTitle.setText("Network");
        mPrefs = LinphonePreferences.instance();
        ((HomeActivity) getActivity()).hideTabBar(true);

        switchUseWifiOnly.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setWifiOnlyEnabled(b);
                    }
                });

        switchAllowIPV6.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.useIpv6(b);
                    }
                });

        switchEnablePush.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setPushNotificationEnabled(b);
                    }
                });

        switchRandomPort.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.useRandomPort(b);
                        layoutSipPortTouse.setVisibility(
                                mPrefs.isUsingRandomPort() ? View.GONE : View.VISIBLE);
                    }
                });
        switchEnableICE.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setIceEnabled(b);
                    }
                });

        switchEnableTURN.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setTurnEnabled(b);
                    }
                });
        etSipPortUse.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            mPrefs.setSipPort(Integer.valueOf(editable.toString()));
                        } catch (NumberFormatException nfe) {
                            Log.e(nfe);
                        }
                    }
                });

        etStunServer.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        mPrefs.setStunServer(editable.toString());
                        switchEnableICE.setEnabled(
                                mPrefs.getStunServer() != null
                                        && !mPrefs.getStunServer().isEmpty());
                        switchEnableTURN.setEnabled(
                                mPrefs.getStunServer() != null
                                        && !mPrefs.getStunServer().isEmpty());
                        if (editable.toString() == null || editable.toString().isEmpty()) {
                            switchEnableICE.setChecked(false);
                            switchEnableTURN.setChecked(false);
                        }
                    }
                });

        /* mAndroidBatterySaverSettings.setListener(
        new SettingListenerBase() {
            @Override
            public void onClicked() {
                mPrefs.powerSaverDialogPrompted(true);
                Intent intent = DeviceUtils.getDevicePowerManagerIntent(getActivity());
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        switchUseWifiOnly.setChecked(mPrefs.isWifiOnlyEnabled());

        switchAllowIPV6.setChecked(mPrefs.isUsingIpv6());

        switchEnablePush.setChecked(mPrefs.isPushNotificationEnabled());
        switchEnablePush.setVisibility(
                PushNotificationUtils.isAvailable(getActivity()) ? View.VISIBLE : View.GONE);

        switchRandomPort.setChecked(mPrefs.isUsingRandomPort());

        switchEnableICE.setChecked(mPrefs.isIceEnabled());
        switchEnableICE.setEnabled(
                mPrefs.getStunServer() != null && !mPrefs.getStunServer().isEmpty());

        switchEnableTURN.setChecked(mPrefs.isTurnEnabled());
        switchEnableTURN.setEnabled(
                mPrefs.getStunServer() != null && !mPrefs.getStunServer().isEmpty());

        etSipPortUse.setText(String.valueOf(mPrefs.getSipPort()));
        layoutSipPortTouse.setVisibility(mPrefs.isUsingRandomPort() ? View.GONE : View.VISIBLE);

        etStunServer.setText(String.valueOf(mPrefs.getStunServer()));

        layoutBattery.setVisibility(
                DeviceUtils.hasDevicePowerManager(getActivity()) ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.imageViewBack)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }
}
