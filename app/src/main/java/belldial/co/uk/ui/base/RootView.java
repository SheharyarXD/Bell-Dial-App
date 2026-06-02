package belldial.co.uk.ui.base;

public interface RootView {

    void showMessage(String s);

    void showLoader();

    void hideLoader();

    boolean isThereInternetConnection();

    void showListLoader();

    void hideListLoader();
}
