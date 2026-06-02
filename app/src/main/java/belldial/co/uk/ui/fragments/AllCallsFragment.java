package belldial.co.uk.ui.fragments;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.ui.adapter.SectionPagerCallsAdapter;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.AllCallsPresenter;
import belldial.co.uk.ui.views.AllCallsView;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.utils.CustomViewPager;
import butterknife.BindView;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.Account;
import org.linphone.core.Call;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;

/** A simple {@link Fragment} subclass. */
public class AllCallsFragment extends BaseFragment<AllCallsPresenter, AllCallsView>
        implements AllCallsView {

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.textViewAllCalls)
    AppCompatTextView textViewAllCalls;

    @BindView(R.id.layoutAllCalls)
    LinearLayout layoutAllCalls;

    @BindView(R.id.textViewMissedCall)
    AppCompatTextView textViewMissedCall;

    @BindView(R.id.layoutMissedCall)
    LinearLayout layoutMissedCall;

    @BindView(R.id.layoutAll)
    LinearLayout layoutAll;

    @BindView(R.id.viewPagerRecentCalls)
    CustomViewPager viewPagerRecentCalls;

    private SimpleCoreListener mCoreListener;

    private SectionPagerCallsAdapter sectionPagerAdapter;

    @Override
    protected int createLayout() {
        return R.layout.fragment_calls;
    }

    @Override
    protected void setPresenter() {
        presenter = new AllCallsPresenter();
    }

    @Override
    protected AllCallsView createView() {
        return this;
    }

    @Override
    protected void bindData() {
mCoreListener = new SimpleCoreListener() {

    @Override
    public void onAccountRegistrationStateChanged(
            final Core lc,
            final Account cfg,
            final RegistrationState state,
            String smessage) {
        if (!LinphoneService.isReady()) return;

        // ✅ Skip registration-based icon update if a call is active
        if (lc != null && lc.getCallsNb() > 0) return;

        if (lc.getAccountList() == null || lc.getAccountList().length == 0) {
            imageViewStatus.setImageResource(R.drawable.signal_null);
            return;
        }

        if (lc.getDefaultAccount() != null && lc.getDefaultAccount().equals(cfg)) {
            imageViewStatus.setImageResource(Constant.getStatusIconResource(state, true));
        } else if (lc.getDefaultAccount() == null) {
            imageViewStatus.setImageResource(Constant.getStatusIconResource(state, true));
        }
    }

    // ✅ This fires repeatedly during a call — use it to show real network signal
    @Override
    public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
        if (call == null) {
            ((HomeActivity) getActivity()).showActiveCall(false);
            // No call → restore registration-based icon
            Account account = lc.getDefaultAccount();
            if (account != null) {
                imageViewStatus.setImageResource(
                        Constant.getStatusIconResource(account.getState(), true));
            }
        } else {
            ((HomeActivity) getActivity()).showActiveCall(true);
            // ✅ During call → use network-based icon only
            Account account = lc.getDefaultAccount();
            imageViewStatus.setImageResource(Constant.getStatusIconResource(account.getState(), true));
            imageViewStatus.setImageResource(Constant.getNetworkSignalIcon());
        }
    }

    @Override
    public void onCallStateChanged(Core lc, Call call, Call.State state, String message) {
        if (state == Call.State.End || state == Call.State.Released || state == Call.State.Error) {
            ((HomeActivity) getActivity()).showActiveCall(false);
            // ✅ Call ended → restore registration icon
            Account account = lc.getDefaultAccount();
            if (account != null) {
                imageViewStatus.setImageResource(
                        Constant.getStatusIconResource(account.getState(), true));
            }
        }
    }
};
        try {
            if (LinphoneManager.getCore().getCurrentCall() != null) {
                ((HomeActivity) getActivity()).showActiveCall(true);
            } else {
                ((HomeActivity) getActivity()).showActiveCall(false);
            }
        } catch (NullPointerException e) {

        }

        sectionPagerAdapter = new SectionPagerCallsAdapter(getChildFragmentManager(), 2);
        viewPagerRecentCalls.setAdapter(sectionPagerAdapter);
        viewPagerRecentCalls.setCurrentItem(0);
        viewPagerRecentCalls.setPagingEnabled(false);
        viewPagerRecentCalls.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(
                            int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        /*
                         * if (viewPagerRecentCalls.getCurrentItem() == 0) {
                         * viewPagerRecentCalls.setCurrentItem(0);
                         * viewLineRecentCalls.setVisibility(View.VISIBLE);
                         * viewLineMissedCall.setVisibility(View.INVISIBLE);
                         * viewLineContacts.setVisibility(View.INVISIBLE);
                         * }else {
                         * viewPagerRecentCalls.setCurrentItem(1);
                         * viewLineRecentCalls.setVisibility(View.INVISIBLE);
                         * viewLineMissedCall.setVisibility(View.VISIBLE);
                         * viewLineContacts.setVisibility(View.INVISIBLE);
                         * }
                         */
                        /*
                         * else {
                         * viewPagerRecentCalls.setCurrentItem(2);
                         * viewLineRecentCalls.setVisibility(View.INVISIBLE);
                         * viewLineMissedCall.setVisibility(View.INVISIBLE);
                         * viewLineContacts.setVisibility(View.VISIBLE);
                         * }
                         */

                    }
                });
        layoutAllCalls.setOnClickListener(
                v -> {
                    layoutMissedCall.setBackground(
                            getResources().getDrawable(R.drawable.rounded_bg_white));
                    layoutAllCalls.setBackground(
                            getResources().getDrawable(R.drawable.rounded_bg_green_new));
                    textViewAllCalls.setTextColor(getResources().getColor(R.color.colorWhite));
                    textViewMissedCall.setTextColor(getResources().getColor(R.color.colorGreen));
                    viewPagerRecentCalls.setCurrentItem(0);
                });
        layoutMissedCall.setOnClickListener(
                v -> {
                    layoutAllCalls.setBackground(
                            getResources().getDrawable(R.drawable.rounded_bg_white_new));
                    layoutMissedCall.setBackground(
                            getResources().getDrawable(R.drawable.rounded_bg_green));
                    textViewAllCalls.setTextColor(getResources().getColor(R.color.colorGreen));
                    textViewMissedCall.setTextColor(getResources().getColor(R.color.colorWhite));
                    viewPagerRecentCalls.setCurrentItem(1);
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LinphoneService.isReady()) {
            Core lc = LinphoneManager.getCore();
            if (lc != null) {
                lc.addListener(mCoreListener);
                Account account = lc.getDefaultAccount();
                if (account != null) {
                    mCoreListener.onAccountRegistrationStateChanged(lc, account, account.getState(), null);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Core lc = LinphoneManager.getCore();
            if (lc != null) {
                lc.removeListener(mCoreListener);
            }
        } catch (Exception ignored) {
        }
    }
}
