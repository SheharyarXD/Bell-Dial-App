package belldial.co.uk.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import belldial.co.uk.api.ApiService;
import belldial.co.uk.api.RetroClient;
import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.api.model.SendModel;
import belldial.co.uk.api.model.SendSmsRequest;
import belldial.co.uk.api.model.Sms;
import belldial.co.uk.api.model.SmsDetails;
import belldial.co.uk.api.model.SmsMessage;
import belldial.co.uk.ui.adapter.SearchContactListAdapter;
import belldial.co.uk.ui.adapter.SmsMessagesAdapter;
import belldial.co.uk.ui.model.ContactAddress;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import belldial.co.uk.contacts.LinphoneNumberOrAddress;
import belldial.co.uk.utils.LinphoneUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import belldial.co.uk.R;

import belldial.co.uk.ui.activity.HomeActivity;
import retrofit2.Callback;
import retrofit2.Response;

/** A simple {@link Fragment} subclass. */
public class SmsFragment extends Fragment implements SearchContactListAdapter.ItemListener {

    @BindView(R.id.textViewNumber)
    EditText textViewNumber;

    @BindView(R.id.imgthreeDots)
    ImageView imgthreeDots;

    @BindView(R.id.imageViewRemove)
    ImageView imageViewRemove;

    @BindView(R.id.linearNumber)
    LinearLayout linearNumber;

    /*@BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;*/

    @BindView(R.id.cardView)
    CardView cardView;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.tvCharactersCount)
    TextView tvCharactersCount;

    @BindView(R.id.etMessage)
    TextView etMessage;

    @BindView(R.id.rvMessages)
    RecyclerView rvMessages;

    @BindView(R.id.rvSearchContacts)
    RecyclerView rvSearchContacts;

    @BindView(R.id.tvBalance)
    TextView tvBalance;

    @BindView(R.id.tvSender)
    TextView tvSender;

    @BindView(R.id.layoutMsgs)
    LinearLayout layoutMsgs;

    View view;

    SmsMessage smsMessage;
    SmsMessagesAdapter adapter;

    SearchContactListAdapter searchAdapter;

    List<Sms> smsList = new ArrayList<>();

    SharedPreferences sharedPreferences;

    SmsDetails smsDetails;

    private List<ContactAddress> linphoneContacts;
    private List<ContactAddress> searchList = new ArrayList<>();

    MessagesResponse selectedMessage;

    private int selectedNumberPosition = -1;

    private String sender = "";
    private String receiver = "";

    public SmsFragment() {}

    public SmsFragment(
            SmsMessage smsMessage,
            SmsDetails smsDetails,
            List<Sms> smsList,
            MessagesResponse selectedMessage) {
        this.smsMessage = smsMessage;
        this.smsDetails = smsDetails;
        this.smsList = smsList;
        this.selectedMessage = selectedMessage;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sms, null);
        return view.getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ButterKnife.bind(this, view);
            sharedPreferences =
                    requireContext()
                            .getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
            linphoneContacts = getContactsListData();
            toolBarTitle.setText("Send SMS");
            initRecyclerView();
            initSearchRecyclerView();
            initListeners();
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ContactAddress> getContactsListData() {
        if (ContactsManager.getInstance() == null) return new ArrayList<>();
        List<ContactAddress> list = null;
        try {
            list = new ArrayList<ContactAddress>();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setData() {
        sender = smsDetails.getSendList().get(0).getSenderName();
        if (!smsDetails.getSendList().isEmpty())
            tvSender.setText(smsDetails.getSendList().get(0).getSenderName());

        tvBalance.setText(smsMessage.getRemainingCredit());

        if (selectedMessage != null) {
            if (selectedMessage.getFullName() != null && !selectedMessage.getFullName().isEmpty()) {
                textViewNumber.setText(selectedMessage.getFullName());
            } else {
                textViewNumber.setText(selectedMessage.getReceiverNumber());
            }
            receiver = selectedMessage.getReceiverNumber();
        }
    }

    private void initRecyclerView() {
        adapter = new SmsMessagesAdapter(requireContext(), smsList);
        rvMessages.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        rvMessages.setHasFixedSize(true);
        rvMessages.setAdapter(adapter);
    }

    private void initSearchRecyclerView() {
        searchAdapter = new SearchContactListAdapter(requireContext(), searchList, this);
        rvSearchContacts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvSearchContacts.setHasFixedSize(true);
        rvSearchContacts.setAdapter(searchAdapter);
    }

    private void initListeners() {
        TextWatcher textWatcher =
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        tvCharactersCount.setText((160 - s.length()) + " characters remaining");
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                };

        etMessage.addTextChangedListener(textWatcher);

        TextWatcher textWatcher2 =
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Show popup menu with contacts
                        receiver = s.toString();
                        if (s.length() > 1) {

                            searchList.clear();
                            for (ContactAddress contactAddress : linphoneContacts) {
                                if (contactAddress.address.startsWith(s.toString())) {
                                    searchList.add(contactAddress);
                                }
                            }

                            if (!searchList.isEmpty()) {
                                rvSearchContacts.setVisibility(View.VISIBLE);
                                layoutMsgs.setVisibility(View.GONE);
                            } else {
                                rvSearchContacts.setVisibility(View.GONE);
                                layoutMsgs.setVisibility(View.VISIBLE);
                            }

                            searchAdapter.notifyDataSetChanged();

                            /*PopupMenu popupMenu = new PopupMenu(requireContext(), textViewNumber);

                            searchList.clear();
                            for (ContactAddress contactAddress : linphoneContacts) {
                                if (contactAddress.address.startsWith(s.toString())) {
                                    searchList.add(contactAddress);
                                    popupMenu.getMenu().add(contactAddress.contact.getFullName());
                                }
                            }

                            popupMenu.setOnMenuItemClickListener(
                                    new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem menuItem) {
                                            // Toast message on menu item clicked
                                            //
                                            // tvSender.setText(menuItem.getTitle());
                                            return true;
                                        }
                                    });
                            // Showing the popup menu
                            popupMenu.show();*/
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                };

        textViewNumber.addTextChangedListener(textWatcher2);

        textViewNumber.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_DEL) {
                            if (TextUtils.isDigitsOnly(textViewNumber.getText().toString())) {
                                int cursorPosition = textViewNumber.getSelectionStart();
                                String tmp =
                                        textViewNumber
                                                .getText()
                                                .toString()
                                                .substring(0, cursorPosition);
                                String tmp1 =
                                        textViewNumber
                                                .getText()
                                                .toString()
                                                .substring(cursorPosition);

                                if (tmp.length() > 0) {
                                    textViewNumber.setText(
                                            tmp.substring(0, tmp.length() - 1) + "" + tmp1);
                                    if (cursorPosition >= 0) {
                                        textViewNumber.setSelection(cursorPosition - 1);
                                    }
                                }
                            } else {
                                textViewNumber.setText("");
                            }
                        }
                        return false;
                    }
                });

        textViewNumber.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            layoutMsgs.setVisibility(View.VISIBLE);
                            rvSearchContacts.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
    }

    protected void hideKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick({
        R.id.textViewNumber,
        R.id.imageViewRemove,
        R.id.btnSend,
        R.id.llSenderPicker,
        R.id.imageViewBack
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.textViewNumber:
                //                hideKeyBoard();
                break;
            case R.id.imageViewRemove:
                if (TextUtils.isDigitsOnly(textViewNumber.getText().toString())) {
                    int cursorPosition = textViewNumber.getSelectionStart();
                    String tmp = textViewNumber.getText().toString().substring(0, cursorPosition);
                    String tmp1 = textViewNumber.getText().toString().substring(cursorPosition);

                    if (tmp.length() > 0) {
                        textViewNumber.setText(tmp.substring(0, tmp.length() - 1) + "" + tmp1);
                        if (cursorPosition >= 0) {
                            textViewNumber.setSelection(cursorPosition - 1);
                        }
                    }
                } else {
                    textViewNumber.setText("");
                }
                break;

            case R.id.btnSend:
                {
                    if (etMessage.getText().toString().isEmpty()
                            && textViewNumber.getText().toString().isEmpty()) {
                        Toast.makeText(
                                        requireContext(),
                                        "Please enter a message and a number",
                                        Toast.LENGTH_SHORT)
                                .show();
                        return;
                    } else if (textViewNumber.getText().toString().isEmpty()) {
                        Toast.makeText(
                                        requireContext(),
                                        "Please enter a number",
                                        Toast.LENGTH_SHORT)
                                .show();
                        return;
                    } else if (etMessage.getText().toString().isEmpty()) {
                        Toast.makeText(
                                        requireContext(),
                                        "Please enter a message",
                                        Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    if (tvSender.getText().toString().isEmpty()) {
                        Toast.makeText(
                                        requireContext(),
                                        "Please select send from",
                                        Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    hideKeyBoard();

                    if (Integer.parseInt(smsMessage.getRemainingCredit()) > 0) {
                        sendSmsMessage();
                    } else {
                        Sms sms = new Sms();
                        sms.setMessage(etMessage.getText().toString());
                        sms.setStatus("failed");
                        sms.setCreatedDate("Just now");
                        smsList.add(0, sms);
                        adapter.notifyDataSetChanged();
                        etMessage.setText("");
                        textViewNumber.setText("");
                    }
                }
                break;

            case R.id.llSenderPicker:
                {
                    PopupMenu popupMenu = new PopupMenu(requireContext(), view);

                    for (SendModel sender : smsDetails.getSendList()) {
                        if (!sender.getSenderName().isEmpty()) {
                            popupMenu.getMenu().add(sender.getSenderName());
                        }
                    }

                    popupMenu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    // Toast message on menu item clicked
                                    tvSender.setText(menuItem.getTitle());
                                    sender = menuItem.getTitle().toString();
                                    return true;
                                }
                            });
                    // Showing the popup menu
                    popupMenu.show();
                }
                break;

            case R.id.imageViewBack:
                {
                    requireActivity().onBackPressed();
                }
                break;
        }
    }

    private void sendSmsMessage() {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");

        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setMessage(etMessage.getText().toString());
        sendSmsRequest.setReceiverNumber(receiver);

        sendSmsRequest.setSenderNumber(sender);
        sendSmsRequest.setUserId(smsDetails.getUserId());
        sendSmsRequest.setCreditId(smsDetails.getCreditId());

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<Void> call = api.sendSmsMessage(headers, sendSmsRequest);
        call.enqueue(
                new Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            if (getContext() == null) return;
                            Toast.makeText(requireContext(), "Message Sent", Toast.LENGTH_SHORT)
                                    .show();

                            Sms sms = new Sms();
                            sms.setMessage(etMessage.getText().toString());
                            sms.setCreatedDate("Just now");
                            smsList.add(0, sms);
                            adapter.notifyDataSetChanged();

                            smsMessage.setRemainingCredit(
                                    String.valueOf(
                                            Integer.parseInt(smsMessage.getRemainingCredit()) - 1));
                            tvBalance.setText(smsMessage.getRemainingCredit());

                            etMessage.setText("");
                            //                            textViewNumber.setText("");

                            ((HomeActivity) requireActivity()).getSmsHistory();

                        } else {
                            Toast.makeText(requireContext(), "Failed...", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable throwable) {
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                        Toast.makeText(requireContext(), "Failed...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(ContactAddress contactAddress) {
        receiver = contactAddress.address;
        textViewNumber.setText(contactAddress.contact.getFullName());

        layoutMsgs.setVisibility(View.VISIBLE);
        rvSearchContacts.setVisibility(View.GONE);

        hideKeyBoard();
    }
}
