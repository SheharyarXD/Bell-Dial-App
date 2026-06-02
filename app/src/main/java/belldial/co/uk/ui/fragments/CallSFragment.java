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
import butterknife.BindView;
import butterknife.OnClick;
import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.tools.Log;

/** A simple {@link Fragment} subclass. */
public class CallSFragment extends BaseFragment<SettingsPresenter, SettingsView>
        implements SettingsView {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.switchInBoundDTMF)
    SwitchCompat switchInBoundDTMF;

    @BindView(R.id.switchSIPInfoDTMF)
    SwitchCompat switchSIPInfoDTMF;

    @BindView(R.id.switchRepeatCall)
    SwitchCompat switchRepeatCall;

    @BindView(R.id.etVoiceMailUrl)
    AppCompatEditText etVoiceMailUrl;

    @BindView(R.id.switchUseDeviceRingtone)
    SwitchCompat switchUseDeviceRingtone;

    @BindView(R.id.switchVibrateIncomingCall)
    SwitchCompat switchVibrateIncomingCall;

    @BindView(R.id.textViewMediaEncryption)
    AppCompatTextView textViewMediaEncryption;

    @BindView(R.id.layoutMediaEncryption)
    LinearLayout layoutMediaEncryption;

    @BindView(R.id.textViewIncomingCallTimr)
    AppCompatEditText textViewIncomingCallTimr;

    @BindView(R.id.layoutEchoCanceler)
    LinearLayout layoutEchoCanceler;

    @BindView(R.id.textViewAutoAnswerCallTime)
    AppCompatEditText textViewAutoAnswerCallTime;

    @BindView(R.id.layoutAutoAnswerTime)
    LinearLayout layoutAutoAnswerTime;

    private LinphonePreferences mPrefs;

    @Override
    protected int createLayout() {
        return R.layout.fragment_call;
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
        toolBarTitle.setText("Call");
        ((HomeActivity) getActivity()).hideTabBar(true);
        mPrefs = LinphonePreferences.instance();
        switchUseDeviceRingtone.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.enableDeviceRingtone(true);
                    }
                });
        switchVibrateIncomingCall.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.enableIncomingCallVibration(b);
                    }
                });
        switchRepeatCall.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.enableAutoAnswer(b);
                        layoutAutoAnswerTime.setVisibility(
                                mPrefs.isAutoAnswerEnabled() ? View.VISIBLE : View.GONE);
                    }
                });
        switchSIPInfoDTMF.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.sendDTMFsAsSipInfo(b);
                    }
                });
        switchInBoundDTMF.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.sendDtmfsAsRfc2833(b);
                    }
                });
        textViewAutoAnswerCallTime.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            mPrefs.setAutoAnswerTime(Integer.parseInt(editable.toString()));
                        } catch (NumberFormatException nfe) {
                            Log.e(nfe);
                        }
                    }
                });
        textViewIncomingCallTimr.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            mPrefs.setIncTimeout(Integer.parseInt(editable.toString()));
                        } catch (NumberFormatException nfe) {
                            Log.e(nfe);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        switchSIPInfoDTMF.setChecked(mPrefs.useSipInfoDtmfs());
        switchInBoundDTMF.setChecked(mPrefs.useRfc2833Dtmfs());
        switchRepeatCall.setChecked(mPrefs.isAutoAnswerEnabled());
        etVoiceMailUrl.setText(mPrefs.getVoiceMailUri());
        switchUseDeviceRingtone.setChecked(mPrefs.isDeviceRingtoneEnabled());

        switchVibrateIncomingCall.setChecked(mPrefs.isIncomingCallVibrationEnabled());

        textViewMediaEncryption.setText(String.valueOf(mPrefs.getMediaEncryption()));

        textViewAutoAnswerCallTime.setText(String.valueOf(mPrefs.getAutoAnswerTime()));
        layoutAutoAnswerTime.setVisibility(mPrefs.isAutoAnswerEnabled() ? View.VISIBLE : View.GONE);

        textViewIncomingCallTimr.setText(String.valueOf(mPrefs.getIncTimeout()));
    }

    @OnClick(R.id.imageViewBack)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }
}
