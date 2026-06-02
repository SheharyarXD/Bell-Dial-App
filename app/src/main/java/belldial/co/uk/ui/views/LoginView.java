package belldial.co.uk.ui.views;

import belldial.co.uk.api.model.Message;
import belldial.co.uk.ui.base.RootView;

public interface LoginView extends RootView {
    void setVerification();

    void numberNotRegisterd();

    void setDetails(Message message);

    void detailsNotFound();
}
