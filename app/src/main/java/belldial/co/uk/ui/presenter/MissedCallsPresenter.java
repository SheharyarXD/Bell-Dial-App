package belldial.co.uk.ui.presenter;

import belldial.co.uk.ui.base.BasePresenter;
import belldial.co.uk.ui.views.RecentCallsView;

public class MissedCallsPresenter extends BasePresenter<RecentCallsView> {

    public void openCallDetails() {
        // navigator.openCallDetailsFragment(BaseActivity.PerformFragment.REPLACE);
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {}

    public void openChat() {
        // navigator.openChatFragment(BaseActivity1.PerformFragment.REPLACE);
    }
}
