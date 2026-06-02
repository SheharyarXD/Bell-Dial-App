package belldial.co.uk.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseActivity extends AppCompatActivity {

    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction;
    public static boolean isActivityStatus;

    public abstract int getPlaceHolder();

    public enum PerformFragment {
        ADD,
        REPLACE
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityStatus = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isActivityStatus = true;
    }

    // add or replace fragment
    @UiThread
    public void openFragment(
            Fragment fragment,
            String fragmentTag,
            PerformFragment performFragment,
            boolean backStack) {
        hideKeyBoard();
        fragmentTransaction = manager.beginTransaction();
        if (performFragment == PerformFragment.ADD)
            fragmentTransaction.add(getPlaceHolder(), fragment, fragmentTag);
        else if (performFragment == PerformFragment.REPLACE)
            fragmentTransaction.replace(getPlaceHolder(), fragment, fragmentTag);
        if (backStack) fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commit();
    }

    @UiThread
    protected void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityStatus = false;
    }

    @Override
    public void onBackPressed() {
        hideKeyBoard();
        super.onBackPressed();
    }
}
