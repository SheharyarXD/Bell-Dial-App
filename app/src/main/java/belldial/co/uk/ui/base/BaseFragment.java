package belldial.co.uk.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

import com.google.android.material.snackbar.Snackbar;

import belldial.co.uk.R;
import belldial.co.uk.ui.navigator.AppNavigator;

public abstract class BaseFragment<PresenterT extends BasePresenter<ViewT>, ViewT extends RootView>
        extends Fragment implements RootView {

    protected PresenterT presenter;

    private static ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(createLayout(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            setPresenter();

            if (presenter != null) {

                presenter.setNavigator((AppNavigator) getActivity());
                presenter.setView(createView());
            }
            bindData();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("asad", "onActivityCreated: " + e.getMessage());
        }

    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.destroy();
            presenter = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) presenter.pause();
    }

    public void showMsg(String msg) {
        hideKeyBoard();
        // hideLoader();
        if (getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);
            View view1 = snackbar.getView();
            // ((TextView) ((ViewGroup)
            // view1).getChildAt(0)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
            // "fonts/SourceSansPro-Regular.ttf"));
            view1.setBackgroundResource(R.color.colorGreen);
            TextView textView =
                    (TextView) view1.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.colorWhite));
            snackbar.show();
        }
    }

    /*public void showAlertMessage(String message, MyCustomDialog.OKListener okListener){
        presenter.navigator.openSingleOptionDialog()
                .setInitials(okListener,getString(R.string.app_name),message)
                .show(getFragmentManager(),"Alert");
    }*/

    @Override
    public void showMessage(String message) {
        showMsg(message);
    }

    @Override
    public void showLoader() {
        // hideKeyBoard();
        if (dialog == null && getActivity() != null) {
            try {
                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dialog != null) dialog.show();
    }

    @Override
    public void hideLoader() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @UiThread
    protected void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean isThereInternetConnection() {
        return false;
    }

    protected abstract int createLayout();

    protected abstract void setPresenter();

    protected abstract ViewT createView();

    protected abstract void bindData();

    @Override
    public void showListLoader() {
    }

    @Override
    public void hideListLoader() {
    }
}
