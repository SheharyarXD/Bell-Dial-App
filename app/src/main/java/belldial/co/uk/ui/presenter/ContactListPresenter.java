package belldial.co.uk.ui.presenter;

import belldial.co.uk.ui.base.BaseActivity;
import belldial.co.uk.ui.base.BasePresenter;
import belldial.co.uk.ui.views.ContactListView;

public class ContactListPresenter extends BasePresenter<ContactListView> {

    public void openContactdetails() {
        navigator.openContactDetailsFragment(BaseActivity.PerformFragment.REPLACE);
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {}
}
