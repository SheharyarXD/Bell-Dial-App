package belldial.co.uk.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.SettingsPresenter;
import belldial.co.uk.ui.views.SettingsView;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.settings.SettingsActivity;
import butterknife.BindView;
import butterknife.OnClick;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.Core;
import org.linphone.core.CoreListener;
import org.linphone.core.EcCalibratorStatus;
import belldial.co.uk.utils.SimpleCoreListener;

/** A simple {@link Fragment} subclass. */
public class AudioFragment extends BaseFragment<SettingsPresenter, SettingsView>
        implements SettingsView {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.switchEchoCancellation)
    SwitchCompat switchEchoCancellation;

    @BindView(R.id.textViewEcho)
    AppCompatTextView textViewEcho;

    @BindView(R.id.layoutEchoCanceler)
    LinearLayout layoutEchoCanceler;

    @BindView(R.id.textViewTestEcho)
    AppCompatTextView textViewTestEcho;

    @BindView(R.id.layoutTestEcho)
    LinearLayout layoutTestEcho;

    @BindView(R.id.switchAdaptiveRate)
    SwitchCompat switchAdaptiveRate;

    @BindView(R.id.textViewCodecBitrate)
    AppCompatTextView textViewCodecBitrate;

    @BindView(R.id.layoutCodecBitrate)
    LinearLayout layoutCodecBitrate;

    @BindView(R.id.switchopus48KHz)
    SwitchCompat switchopus48KHz;

    @BindView(R.id.switchspeex16KHz)
    SwitchCompat switchspeex16KHz;

    @BindView(R.id.switchspeex8KHz)
    SwitchCompat switchspeex8KHz;

    @BindView(R.id.switchpcmu8KHz)
    SwitchCompat switchpcmu8KHz;

    @BindView(R.id.switchpcma8KHz)
    SwitchCompat switchpcma8KHz;

    @BindView(R.id.switchgsm8KHz)
    SwitchCompat switchgsm8KHz;

    @BindView(R.id.switchg7228KHz)
    SwitchCompat switchg7228KHz;

    @BindView(R.id.switchg7298KHz)
    SwitchCompat switchg7298KHz;

    @BindView(R.id.switchlibc8KHz)
    SwitchCompat switchlibc8KHz;

    @BindView(R.id.switchspeex32KHz)
    SwitchCompat switchspeex32KHz;

    @BindView(R.id.switchL1644KHz)
    SwitchCompat switchL1644KHz;

    @BindView(R.id.switchisac16KHz)
    SwitchCompat switchisac16KHz;

    private LinphonePreferences mPrefs;

    @Override
    protected int createLayout() {
        return R.layout.fragment_audio;
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
        toolBarTitle.setText("Audio");
        ((HomeActivity) getActivity()).hideTabBar(true);
        mPrefs = LinphonePreferences.instance();
        switchEchoCancellation.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.setEchoCancellation(b);
                    }
                });
        switchAdaptiveRate.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mPrefs.enableAdaptiveRateControl(b);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateValues();
    }

    private void updateValues() {
        switchEchoCancellation.setChecked(mPrefs.echoCancellationEnabled());

        switchAdaptiveRate.setChecked(mPrefs.adaptiveRateControlEnabled());

        textViewCodecBitrate.setText(String.valueOf(mPrefs.getCodecBitrateLimit()) + " kbits/s");

        if (mPrefs.echoCancellationEnabled()) {
            textViewEcho.setText(
                    String.format(
                            getString(R.string.ec_calibrated),
                            String.valueOf(mPrefs.getEchoCalibration())));
        }
    }

    @OnClick({
            R.id.imageViewBack,
            R.id.layoutEchoCanceler,
            R.id.layoutTestEcho,
            R.id.layoutCodecBitrate
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageViewBack:
                getActivity().onBackPressed();
                break;
            case R.id.layoutEchoCanceler:
                textViewEcho.setText(getString(R.string.ec_calibrating));

                int recordAudio = getActivity()
                        .getPackageManager()
                        .checkPermission(
                                Manifest.permission.RECORD_AUDIO,
                                getActivity().getPackageName());
                if (recordAudio == PackageManager.PERMISSION_GRANTED) {
                    startEchoCancellerCalibration();
                } else {
                    ((SettingsActivity) getActivity())
                            .requestPermissionIfNotGranted(Manifest.permission.RECORD_AUDIO);
                }
                break;
            case R.id.layoutTestEcho:
                if (LinphoneManager.getAudioManager().getEchoTesterStatus()) {
                    stopEchoTester();
                } else {
                    startEchoTester();
                }
                break;
            case R.id.layoutCodecBitrate:
                break;
        }
    }

    private void startEchoTester() {
        LinphoneManager.getAudioManager().startEchoTester();
        textViewTestEcho.setText("Is running");
    }

    private void stopEchoTester() {
        LinphoneManager.getAudioManager().stopEchoTester();
        textViewTestEcho.setText("Is stopped");
    }

    private void startEchoCancellerCalibration() {
        if (LinphoneManager.getAudioManager().getEchoTesterStatus())
            stopEchoTester();
        LinphoneManager.getCore()
                .addListener(
                        new SimpleCoreListener() {
                            @Override
                            public void onEcCalibrationResult(
                                    Core core, EcCalibratorStatus status, int delayMs) {
                                if (status == EcCalibratorStatus.InProgress)
                                    return;
                                core.removeListener(this);
                                LinphoneManager.getAudioManager().routeAudioToEarPiece();

                                if (status == EcCalibratorStatus.DoneNoEcho) {
                                    textViewEcho.setText(getString(R.string.no_echo));
                                } else if (status == EcCalibratorStatus.Done) {
                                    textViewEcho.setText(
                                            String.format(
                                                    getString(R.string.ec_calibrated),
                                                    String.valueOf(delayMs)));
                                } else if (status == EcCalibratorStatus.Failed) {
                                    textViewEcho.setText(getString(R.string.failed));
                                }
                                switchEchoCancellation.setChecked(
                                        status != EcCalibratorStatus.DoneNoEcho);
                                ((AudioManager) getActivity()
                                        .getSystemService(Context.AUDIO_SERVICE))
                                        .setMode(AudioManager.MODE_NORMAL);
                            }
                        });
        LinphoneManager.getAudioManager().startEcCalibration();
    }
}
