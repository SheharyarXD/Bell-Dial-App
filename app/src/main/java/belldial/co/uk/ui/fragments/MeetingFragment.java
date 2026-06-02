package belldial.co.uk.ui.fragments;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import belldial.co.uk.utils.SimpleCoreListener;

import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.base.RootView;

public class MeetingFragment extends BaseFragment {
    Button button;
    EditText editText, name;
    ImageView signal;
    private SimpleCoreListener mCoreListener;

    @Override
    protected int createLayout() {
        return 0;
    }

    @Override
    protected void setPresenter() {

    }

    @Override
    protected RootView createView() {
        return null;
    }

    @Override
    protected void bindData() {

    }

    // @Override
    // public View onCreateView(
    // LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // View view = inflater.inflate(R.layout.fragment_meeting, container, false);
    //
    // button = view.findViewById(R.id.button);
    // editText = view.findViewById(R.id.editText);
    // signal = view.findViewById(R.id.signal);
    // name = view.findViewById(R.id.userName);
    // bindData();
    //
    // button.setOnClickListener(
    // new View.OnClickListener() {
    // @Override
    // public void onClick(View view) {
    // String room = editText.getText().toString();
    // String username = name.getText().toString();
    // if (username.length() > 0) {
    //
    // if (room.length() > 0) {
    //
    // startActivity(
    // new Intent(getContext(), JitsiMeetingRoomActivity.class)
    // .putExtra("roomName", room)
    // .putExtra("name", username));
    // } else {
    // editText.setError("Meeting name is empty");
    // }
    //
    // } else {
    // name.setError("User name is empty");
    // }
    // }
    // });
    //
    // return view;
    // }
    //
    // protected void bindData() {
    // mCoreListener =
    // new SimpleCoreListener() {
    // @Override
    // public void onAccountRegistrationStateChanged(
    // Core lc, Account cfg, RegistrationState state, String smessage) {
    // if (lc.getProxyConfigList() == null) {
    // signal.setImageResource(R.drawable.signal_null);
    //
    // } else {
    // // statusLed.setVisibility(View.VISIBLE);
    // }
    //
    // if (lc.getDefaultProxyConfig() != null
    // && lc.getDefaultProxyConfig().equals(proxy)) {
    // signal.setImageResource(Constant.getStatusIconResource(state, true));
    // } else if (lc.getDefaultProxyConfig() == null) {
    // signal.setImageResource(Constant.getStatusIconResource(state, true));
    // }
    // }
    // };
    // }
    //
    // @Override
    // public void onResume() {
    // super.onResume();
    //
    // Core lc = LinphoneManager.getCore();
    // if (lc != null) {
    // lc.addListener(mListener);
    // ProxyConfig lpc = lc.getDefaultProxyConfig();
    // if (lpc != null) {
    // mListener.onRegistrationStateChanged(lc, lpc, lpc.getState(), null);
    // }
    // }
    // }
    //
    // @Override
    // public void onPause() {
    // super.onPause();
    // Core lc = LinphoneManager.getCore();
    // if (lc != null) {
    // lc.removeListener(mListener);
    // }
    // }
}
