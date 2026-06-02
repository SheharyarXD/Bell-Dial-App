package belldial.co.uk.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import belldial.co.uk.ui.fragments.MissedCallFragment;
import belldial.co.uk.ui.fragments.RecentCallFragment;

public class SectionPagerCallsAdapter extends FragmentStatePagerAdapter {

    // integer to count number of tabs
    int tabCount;

    // Constructor to the class
    public SectionPagerCallsAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        // Initializing tab count
        this.tabCount = tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    // Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        // Returning the current tabs
        switch (position) {
            case 0:
                RecentCallFragment tab1 = new RecentCallFragment();
                return tab1;
            case 1:
                MissedCallFragment tab2 = new MissedCallFragment();
                return tab2;
            default:
                return null;
        }
    }

    // Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}
