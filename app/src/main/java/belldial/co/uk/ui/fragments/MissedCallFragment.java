package belldial.co.uk.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.ui.adapter.MissedCallsAdapter;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.RecentCallsPresenter;
import belldial.co.uk.ui.views.RecentCallsView;
import belldial.co.uk.utils.Constant;
import butterknife.BindView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.CallOutgoingActivity;

import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.core.Core;

/** A simple {@link Fragment} subclass. */
public class MissedCallFragment extends BaseFragment<RecentCallsPresenter, RecentCallsView>
        implements RecentCallsView, MissedCallsAdapter.ItemListener {

    @BindView(R.id.recyclerViewRecentCalls)
    RecyclerView recyclerViewRecentCalls;

    @BindView(R.id.layoutEmptyScreen)
    View layoutEmptyScreen;

    @BindView(R.id.textEmptyScreenHeading)
    TextView textEmptyScreenHeading;

    @BindView(R.id.textEmptyScreenDescription)
    TextView textEmptyScreenDescription;


    MissedCallsAdapter missedCallsAdapter;
    private List<CallLog> mLogs = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @Override
    protected int createLayout() {
        return R.layout.fragment_missed_call;
    }

    @Override
    protected void setPresenter() {
        presenter = new RecentCallsPresenter();
    }

    @Override
    protected RecentCallsView createView() {
        return this;
    }

    @Override
    protected void bindData() {
        sharedPreferences =
                getActivity().getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
    }

    protected void initialiseUI() {
        if (mLogs.isEmpty()){
            recyclerViewRecentCalls.setVisibility(View.GONE);
            layoutEmptyScreen.setVisibility(View.VISIBLE);
            textEmptyScreenHeading.setText(R.string.text_no_missed_calls_yet);
            textEmptyScreenDescription.setText(R.string.text_no_missed_calls_yet_description);
        }else {
            recyclerViewRecentCalls.setVisibility(View.VISIBLE);
            layoutEmptyScreen.setVisibility(View.GONE);
            recyclerViewRecentCalls.setLayoutManager(new LinearLayoutManager(getActivity()));
            missedCallsAdapter = new MissedCallsAdapter(getActivity(), mLogs, MissedCallFragment.this);
            recyclerViewRecentCalls.setAdapter(missedCallsAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (LinphoneService.isReady()) {
            if (LinphoneManager.getCore() != null) {
                mLogs = Arrays.asList(LinphoneManager.getCore().getCallLogs());
                List<CallLog> missedCalls = new ArrayList<CallLog>();
                for (CallLog log : mLogs) {
                    if (log.getStatus() == Call.Status.Missed) {
                        missedCalls.add(log);
                    }
                }
                mLogs = missedCalls;
                initialiseUI();
            }
        }
    }

    @Override
    public void onItemClick(String displayableUsernameFromAddress) {
        if (Constant.TRANSFER == 1) {
            Core core = LinphoneManager.getCore();
            if (core.getCurrentCall() == null) {
                return;
            }
            core.getCurrentCall().transfer(displayableUsernameFromAddress.replaceAll("\\D+", ""));
            showMessage(
                    "Call Transfer to " + displayableUsernameFromAddress.replaceAll("\\D+", ""));
            Constant.TRANSFER = 0;

        } else {
            if (LinphoneManager.getCore().getCallsNb() < 2) {

                LinphoneManager.getCallManager()
                        .newOutgoingCall(
                                "sip:"
                                        + displayableUsernameFromAddress.replaceAll("\\D+", "")
                                        + "@"
                                        + sharedPreferences.getString(Constant.DOMAIN, ""),
                                displayableUsernameFromAddress);
                Intent intent = new Intent(getActivity(), CallOutgoingActivity.class);
                startActivity(intent);
            }
        }
    }
}
