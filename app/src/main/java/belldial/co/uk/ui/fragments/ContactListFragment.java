package belldial.co.uk.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.ui.adapter.ContactListAdapter;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.model.ContactAddress;
import belldial.co.uk.ui.presenter.ContactListPresenter;
import belldial.co.uk.ui.views.ContactListView;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import belldial.co.uk.contacts.LinphoneNumberOrAddress;
import belldial.co.uk.utils.LinphoneUtils;
import butterknife.BindView;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.Account;
import org.linphone.core.Call;
import org.linphone.core.CallStats;
import org.linphone.core.Core;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.ProxyConfig;
import org.linphone.core.RegistrationState;
import org.linphone.core.GlobalState;

/** A simple {@link Fragment} subclass. */
public class ContactListFragment extends BaseFragment<ContactListPresenter, ContactListView>
        implements ContactListView, ContactListAdapter.ItemListener {

    @BindView(R.id.toolbar_toolbar_contact_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewAdd)
    ImageView imageViewAdd;

    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.editTextSearch)
    EditText editTextSearch;

    @BindView(R.id.recyclerViewAllContacts)
    RecyclerView recyclerViewAllContacts;

    ContactListAdapter contactListAdapter;
    private SimpleCoreListener mCoreListener;
    private List<ContactAddress> linphoneContacts;

    @Override
    protected int createLayout() {
        return R.layout.fragment_contacts;
    }

    @Override
    protected void setPresenter() {
        presenter = new ContactListPresenter();
    }

    @Override
    protected ContactListView createView() {
        return this;
    }

    @Override
    protected void bindData() {

        if (LinphoneService.isReady()) {
            linphoneContacts = getContactsListData();
            initialiseUI();
        }

        mCoreListener = new SimpleCoreListener() {
            @Override
            public void onGlobalStateChanged(
                    Core lc, GlobalState state, String message) {
            }

            @Override
            public void onAccountRegistrationStateChanged(
                    Core lc, Account cfg, RegistrationState state, String smessage) {
                if (!LinphoneService.isReady()) {
                    return;
                }

                if (lc.getAccountList() == null || lc.getAccountList().length == 0) {
                    imageViewStatus.setImageResource(R.drawable.signal_null);

                } else {
                    // statusLed.setVisibility(View.VISIBLE);
                }

                if (lc.getDefaultAccount() != null
                        && lc.getDefaultAccount().equals(cfg)) {
                    imageViewStatus.setImageResource(
                            Constant.getStatusIconResource(state, true));
                } else if (lc.getDefaultAccount() == null) {
                    imageViewStatus.setImageResource(
                            Constant.getStatusIconResource(state, true));
                }
            }

            @Override
            public void onCallStateChanged(
                    Core lc, Call call, Call.State cstate, String message) {
                // super.onCallStateChanged(lc, call, cstate, message);
                if (cstate == Call.State.End) {
                    Log.e("CallListener", "End");
                    ((HomeActivity) getActivity()).showActiveCall(false);

                } else if (cstate == Call.State.Released) {
                    ((HomeActivity) getActivity()).showActiveCall(false);

                } else if (cstate == Call.State.Error) {
                    ((HomeActivity) getActivity()).showActiveCall(false);
                }
            }

            @Override
            public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
                // super.onCallStatsUpdated(lc, call, stats);
                if (call == null) {
                    ((HomeActivity) getActivity()).showActiveCall(false);
                } else {
                    ((HomeActivity) getActivity()).showActiveCall(true);
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

        editTextSearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        searchRecent(editTextSearch.getText().toString().trim());
                    }
                });
    }

    protected void initialiseUI() {
        recyclerViewAllContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactListAdapter = new ContactListAdapter(getActivity(), linphoneContacts, ContactListFragment.this);
        recyclerViewAllContacts.setAdapter(contactListAdapter);
    }

    public void searchRecent(String search) {
        if (search == null || search.length() == 0) {
            linphoneContacts = getContactsListData();
            initialiseUI();
            return;
        }
        List<ContactAddress> mLogsNew = new ArrayList<ContactAddress>();
        if (search != null) {
            for (ContactAddress c : linphoneContacts) {
                String address = c.contact.getFullName();
                if (address.startsWith("sip:"))
                    address = address.substring(4);
                if (address != null
                        && address.toLowerCase(Locale.getDefault())
                                .contains(search.toLowerCase(Locale.getDefault()))
                        || c.address.contains(search)) {
                    mLogsNew.add(c);
                }
            }
        }
        recyclerViewAllContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactListAdapter = new ContactListAdapter(getActivity(), mLogsNew, ContactListFragment.this);
        recyclerViewAllContacts.setAdapter(contactListAdapter);
        contactListAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.imageViewAdd)
    public void onViewClicked() {
        Intent i = new Intent(Intent.ACTION_INSERT);
        i.setType(ContactsContract.Contacts.CONTENT_TYPE);
        i.putExtra(ContactsContract.Intents.Insert.NAME, "");
        i.putExtra(ContactsContract.Intents.Insert.PHONE, "");
        getActivity().startActivity(i);
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

    public List<ContactAddress> getContactsListData() {
        List<ContactAddress> list = new ArrayList<>();

        if (ContactsManager.getInstance() != null) {
            // Issue on this line because ContactsManager.getInstance() is null
            if (ContactsManager.getInstance().hasReadContactsAccess()) {
                for (LinphoneContact con : ContactsManager.getInstance().getContacts()) {
                    for (LinphoneNumberOrAddress noa : con.getNumbersOrAddresses()) {
                        String value = noa.getValue();
                        // Fix for sip:username compatibility issue
                        if (value.startsWith("sip:") && !value.contains("@")) {
                            value = value.substring(4);
                            value = LinphoneUtils.getFullAddressFromUsername(value);
                        }
                        list.add(new ContactAddress(con, value));
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Here is the issue", Toast.LENGTH_LONG).show();
        }

        return list;
    }

    @Override
    public void onItemClick(ContactAddress contactAddress) {
        ContactDetailsFragment.setLinphoneContact(contactAddress.contact);
        presenter.openContactdetails();
    }

}
