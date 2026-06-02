package belldial.co.uk.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
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

import belldial.co.uk.LinphoneContext;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.SettingsPresenter;
import belldial.co.uk.ui.views.SettingsView;
import belldial.co.uk.compatibility.Compatibility;
import belldial.co.uk.settings.LinphonePreferences;
import butterknife.BindView;
import butterknife.OnClick;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

/** A simple {@link Fragment} subclass. */
public class AdvancedFragment extends BaseFragment<SettingsPresenter, SettingsView>
        implements SettingsView {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.switchDebug)
    SwitchCompat switchDebug;

    @BindView(R.id.switchFriendlistsubscribe)
    SwitchCompat switchFriendlistsubscribe;

    @BindView(R.id.switchBgMode)
    SwitchCompat switchBgMode;

    @BindView(R.id.switchStartAtBootTime)
    SwitchCompat switchStartAtBootTime;

    @BindView(R.id.etDeviceName)
    AppCompatEditText etDeviceName;

    @BindView(R.id.etRemoteprovisioning)
    AppCompatEditText etRemoteprovisioning;

    @BindView(R.id.layoutAndroidAppSettings)
    LinearLayout layoutAndroidAppSettings;

    @BindView(R.id.etSettingsDisplayname)
    AppCompatEditText etSettingsDisplayname;

    @BindView(R.id.etSettingsUsername)
    AppCompatEditText etSettingsUsername;

    @BindView(R.id.textBgDesc)
    AppCompatTextView textBgDesc;

    private LinphonePreferences mPrefs;

    @Override
    protected int createLayout() {
        return R.layout.fragment_advanced;
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
        toolBarTitle.setText("Advanced");
        ((HomeActivity) getActivity()).hideTabBar(true);
        mPrefs = LinphonePreferences.instance();
        imageViewBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });

        switchDebug.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setDebugEnabled(b);
                    }
                });

        switchBgMode.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setServiceNotificationVisibility(b);
                        if (b) {
                            LinphoneContext.instance().getNotificationManager().startForeground();
                        } else {
                            LinphoneContext.instance().getNotificationManager().stopForeground();
                        }
                    }
                });

        switchStartAtBootTime.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setAutoStart(b);
                    }
                });

        etRemoteprovisioning.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        mPrefs.setRemoteProvisioningUrl(editable.toString());
                    }
                });
        etSettingsDisplayname.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        mPrefs.setDefaultDisplayName(editable.toString());
                    }
                });

        etSettingsUsername.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        mPrefs.setDefaultUsername(editable.toString());
                    }
                });

        etDeviceName.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        mPrefs.setDeviceName(editable.toString());
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        switchDebug.setChecked(mPrefs.isDebugEnabled());

        switchBgMode.setChecked(mPrefs.getServiceNotificationVisibility());
        if (Compatibility.isAppUserRestricted(getActivity())) {
            switchBgMode.setChecked(false);
            switchBgMode.setEnabled(false);
            textBgDesc.setText(getString(R.string.pref_background_mode_warning_desc));
        }

        switchStartAtBootTime.setChecked(mPrefs.isAutoStartEnabled());

        etRemoteprovisioning.setText(mPrefs.getRemoteProvisioningUrl());

        etSettingsDisplayname.setText(mPrefs.getDefaultDisplayName());

        etSettingsUsername.setText(mPrefs.getDefaultUsername());

        etDeviceName.setText(mPrefs.getDeviceName(getActivity()));
    }

    @OnClick(R.id.layoutAndroidAppSettings)
    public void onViewClicked() {
        Context context = getActivity();
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }
}
