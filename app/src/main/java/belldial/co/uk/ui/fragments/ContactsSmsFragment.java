package belldial.co.uk.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.api.ApiService;
import belldial.co.uk.api.RetroClient;
import belldial.co.uk.api.model.GetSmsHistoryResponse;
import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.api.model.SmsDetails;
import belldial.co.uk.ui.adapter.SmsContactListAdapter;
import belldial.co.uk.utils.Constant;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import belldial.co.uk.R;

import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.Core;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.ProxyConfig;
import org.linphone.core.Account;
import org.linphone.core.RegistrationState;
import org.linphone.core.GlobalState;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsSmsFragment extends Fragment {

    @BindView(R.id.rvMessages)
    RecyclerView rvMessages;

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.imageViewSignal)
    ImageView signal;

    private SimpleCoreListener mCoreListener;

    View view;

    SmsContactListAdapter adapter;

    List<MessagesResponse> messagesResponseList = new ArrayList<>();

    SmsDetails smsDetails;

    SharedPreferences sharedPreferences;

    public ContactsSmsFragment(SmsDetails smsDetails, List<MessagesResponse> messagesResponseList) {
        this.messagesResponseList = messagesResponseList;
        this.smsDetails = smsDetails;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts_sms, null);
        return view.getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        sharedPreferences = requireContext()
                .getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
        initListeners();

        initRecyclerView();
        // getSmsHistory();
        bindData();
    }

    private void getSmsHistory() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");
        headers.put("Userid", smsDetails.getUserId());

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<GetSmsHistoryResponse> call = api.getSmsHistory(headers);
        call.enqueue(
                new Callback<GetSmsHistoryResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<GetSmsHistoryResponse> call,
                            Response<GetSmsHistoryResponse> response) {
                        if (response.body().getStatus().equalsIgnoreCase("Success")) {
                            messagesResponseList.clear();
                            messagesResponseList.addAll(
                                    response.body().getMessage().getContactList());

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "Failed...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<GetSmsHistoryResponse> call, Throwable throwable) {
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                        Toast.makeText(requireContext(), "Failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initRecyclerView() {
        adapter = new SmsContactListAdapter(requireContext(), messagesResponseList, null);
        rvMessages.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvMessages.setHasFixedSize(true);
        rvMessages.setAdapter(adapter);
    }

    private void initListeners() {
        imageViewStatus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HomeActivity homeActivity = (HomeActivity) requireActivity();
                        homeActivity.openSmsFragmentEmptyFromAdapter(null);
                    }
                });
    }

    protected void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /*
     * @OnClick({R.id.textViewNumber, R.id.imageViewRemove, R.id.btnSend,
     * R.id.llSenderPicker})
     * public void onViewClicked(View view) {
     * switch (view.getId()) {
     * case R.id.textViewNumber:
     * // hideKeyBoard();
     * break;
     * }
     * }
     */

    protected void bindData() {
        mCoreListener = new SimpleCoreListener() {
            @Override
            public void onGlobalStateChanged(
                    Core lc, GlobalState state, String message) {
            }

            @Override
            public void onAccountRegistrationStateChanged(
                    Core lc, Account cfg, RegistrationState state, String smessage) {
                if (lc.getAccountList() == null || lc.getAccountList().length == 0) {
                    signal.setImageResource(R.drawable.signal_null);

                } else {
                    // statusLed.setVisibility(View.VISIBLE);
                }

                if (lc.getDefaultAccount() != null
                        && lc.getDefaultAccount().equals(cfg)) {
                    signal.setImageResource(Constant.getStatusIconResource(state, true));
                } else if (lc.getDefaultAccount() == null) {
                    signal.setImageResource(Constant.getStatusIconResource(state, true));
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.addListener(mCoreListener);
            Account account = lc.getDefaultAccount();
            if (account != null) {
                mCoreListener.onAccountRegistrationStateChanged(lc, account, account.getState(), null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.removeListener(mCoreListener);
        }
    }
}
