package belldial.co.uk.ui.presenter;

import belldial.co.uk.ui.base.BaseActivity;
import belldial.co.uk.ui.base.BasePresenter;
import belldial.co.uk.ui.views.SettingsView;

public class SettingsPresenter extends BasePresenter<SettingsView> {

    public void openAccount() {
        navigator.openAccountFragment(BaseActivity.PerformFragment.REPLACE);
    }

    public void openAudio() {
        navigator.openAudioFragment(BaseActivity.PerformFragment.REPLACE);
    }

    public void openVideo() {
        navigator.openVideoFragment(BaseActivity.PerformFragment.REPLACE);
    }

    public void openCall() {
        navigator.openCallFragment(BaseActivity.PerformFragment.REPLACE);
    }

    public void openNetwork() {
        navigator.openNetworkFragment(BaseActivity.PerformFragment.REPLACE);
    }

    public void openAdvanced() {
        navigator.openAdvancedFragment(BaseActivity.PerformFragment.REPLACE);
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {}
}
