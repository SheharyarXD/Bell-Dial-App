package belldial.co.uk.ui.navigator;

import belldial.co.uk.ui.base.BaseActivity;

public interface AppNavigator extends HomeNavigator, AuthNavigator {
    //    @Override
    //    public void openSettingsFragment(PerformFragment performFragment) {
    //        SettingsFragment settingsFragment = new SettingsFragment();
    //        openFragment(settingsFragment, SettingsFragment.class.getName(), performFragment,
    // false);
    //    }
    void openMeetingFragment(BaseActivity.PerformFragment performFragment);
}
