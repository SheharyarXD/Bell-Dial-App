package belldial.co.uk.ui.navigator;

import java.util.List;
import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.api.model.Sms;
import belldial.co.uk.api.model.SmsDetails;
import belldial.co.uk.api.model.SmsMessage;
import belldial.co.uk.ui.base.BaseActivity;

public interface HomeNavigator {

    void openAllCallsFragment(BaseActivity.PerformFragment performFragment);

    void openSettingsFragment(BaseActivity.PerformFragment performFragment);

    void openContactListFragment(BaseActivity.PerformFragment performFragment);

    void openDialerFragment(BaseActivity.PerformFragment performFragment);

    void openContactDetailsFragment(BaseActivity.PerformFragment performFragment);

    void openSmsFragment(
            BaseActivity.PerformFragment performFragment,
            SmsMessage smsMessage,
            SmsDetails smsDetails,
            List<Sms> smsList,
            MessagesResponse messagesResponse);

    void openContactsSmsFragment(
            BaseActivity.PerformFragment performFragment,
            SmsDetails smsDetails,
            List<MessagesResponse> messagesResponseList);

    void openAccountFragment(BaseActivity.PerformFragment performFragment);

    void openAudioFragment(BaseActivity.PerformFragment performFragment);

    void openVideoFragment(BaseActivity.PerformFragment performFragment);

    void openCallFragment(BaseActivity.PerformFragment performFragment);

    void openNetworkFragment(BaseActivity.PerformFragment performFragment);

    void openAdvancedFragment(BaseActivity.PerformFragment performFragment);
}
