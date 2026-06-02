package belldial.co.uk.ui.provider;

import java.util.List;
import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.api.model.Sms;
import belldial.co.uk.api.model.SmsDetails;
import belldial.co.uk.api.model.SmsMessage;
import belldial.co.uk.ui.base.BaseActivity;
import belldial.co.uk.ui.fragments.AcoountFragment;
import belldial.co.uk.ui.fragments.AdvancedFragment;
import belldial.co.uk.ui.fragments.AllCallsFragment;
import belldial.co.uk.ui.fragments.AudioFragment;
import belldial.co.uk.ui.fragments.CallRecordingFragment;
import belldial.co.uk.ui.fragments.CallSFragment;
import belldial.co.uk.ui.fragments.ContactDetailsFragment;
import belldial.co.uk.ui.fragments.ContactListFragment;
import belldial.co.uk.ui.fragments.ContactsSmsFragment;
import belldial.co.uk.ui.fragments.DialerFragment;
import belldial.co.uk.ui.fragments.LoginFragment;
import belldial.co.uk.ui.fragments.MeetingFragment;
import belldial.co.uk.ui.fragments.NetworkFragment;
import belldial.co.uk.ui.fragments.SettingsFragment;
import belldial.co.uk.ui.fragments.SmsFragment;
import belldial.co.uk.ui.navigator.AppNavigator;

public abstract class AppNavigationProvider extends BaseActivity implements AppNavigator {

    @Override
    public void openLoginFragment(PerformFragment performFragment) {
        LoginFragment loginFragment = new LoginFragment();
        openFragment(loginFragment, LoginFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openAllCallsFragment(PerformFragment performFragment) {
        AllCallsFragment allCallsFragment = new AllCallsFragment();
        openFragment(allCallsFragment, AllCallsFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openSettingsFragment(PerformFragment performFragment) {
        SettingsFragment settingsFragment = new SettingsFragment();
        openFragment(settingsFragment, SettingsFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openMeetingFragment(PerformFragment performFragment) {
        MeetingFragment meetingFragment = new MeetingFragment();
        openFragment(meetingFragment, MeetingFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openContactListFragment(PerformFragment performFragment) {
        ContactListFragment contactListFragment = new ContactListFragment();
        openFragment(contactListFragment, ContactListFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openDialerFragment(PerformFragment performFragment) {
        DialerFragment dialerFragment = new DialerFragment();
        openFragment(dialerFragment, DialerFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openContactDetailsFragment(PerformFragment performFragment) {
        ContactDetailsFragment contactDetailsFragment = new ContactDetailsFragment();
        openFragment(
                contactDetailsFragment,
                ContactDetailsFragment.class.getName(),
                performFragment,
                true);
    }

    @Override
    public void openSmsFragment(
            PerformFragment performFragment,
            SmsMessage smsMessage,
            SmsDetails smsDetails,
            List<Sms> smsList,
            MessagesResponse messagesResponse) {
        SmsFragment smsFragment =
                new SmsFragment(smsMessage, smsDetails, smsList, messagesResponse);
        openFragment(smsFragment, SmsFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openContactsSmsFragment(
            PerformFragment performFragment,
            SmsDetails smsDetails,
            List<MessagesResponse> messagesResponseList) {
        ContactsSmsFragment contactsSmsFragment =
                new ContactsSmsFragment(smsDetails, messagesResponseList);
        openFragment(
                contactsSmsFragment, ContactsSmsFragment.class.getName(), performFragment, false);
    }

    @Override
    public void openAccountFragment(PerformFragment performFragment) {
        AcoountFragment acoountFragment = new AcoountFragment();
        openFragment(acoountFragment, AcoountFragment.class.getName(), performFragment, true);
    }

    @Override
    public void openAdvancedFragment(PerformFragment performFragment) {
        AdvancedFragment advancedFragment = new AdvancedFragment();
        openFragment(advancedFragment, AdvancedFragment.class.getName(), performFragment, true);
    }

    @Override
    public void openNetworkFragment(PerformFragment performFragment) {
        NetworkFragment networkFragment = new NetworkFragment();
        openFragment(networkFragment, NetworkFragment.class.getName(), performFragment, true);
    }

    @Override
    public void openCallFragment(PerformFragment performFragment) {
        CallSFragment callSFragment = new CallSFragment();
        openFragment(callSFragment, CallSFragment.class.getName(), performFragment, true);
    }

    @Override
    public void openVideoFragment(PerformFragment performFragment) {
        CallRecordingFragment callRecordingFragment = new CallRecordingFragment();
        openFragment(
                callRecordingFragment,
                CallRecordingFragment.class.getName(),
                performFragment,
                true);
    }

    @Override
    public void openAudioFragment(PerformFragment performFragment) {
        AudioFragment audioFragment = new AudioFragment();
        openFragment(audioFragment, AudioFragment.class.getName(), performFragment, true);
    }
}
