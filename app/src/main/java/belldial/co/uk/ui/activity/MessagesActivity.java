package belldial.co.uk.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.ui.adapter.SmsUserMessagesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.vanniktech.emoji.EmojiPopup;
import belldial.co.uk.R;


public class MessagesActivity extends AppCompatActivity {

    @BindView(R.id.rvMsgs)
    RecyclerView rvMessages;

    @BindView(R.id.ivEmoji)
    ImageView ivEmojis;

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.etTypeSomething)
    EditText etTypeSomething;

    @BindView(R.id.rootView)
    LinearLayout rootView;

    MessagesResponse messagesResponse;

    SmsUserMessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);

        getIntentData();
        setData();
        initListeners();
        initRecyclerView();
    }

    private void getIntentData() {
        messagesResponse = (MessagesResponse) getIntent().getSerializableExtra("messageResponse");
    }

    private void setData() {
        tvUserName.setText(messagesResponse.getReceiverNumber());
    }

    private void initRecyclerView() {
        adapter = new SmsUserMessagesAdapter(this, messagesResponse.getSmsList());
        rvMessages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        rvMessages.setHasFixedSize(true);
        rvMessages.setAdapter(adapter);
    }



    private void initListeners() {
        ivEmojis.setOnClickListener(
                v -> {
                    final EmojiPopup emojiPopup = new EmojiPopup(rootView, etTypeSomething);
                    if (emojiPopup.isShowing()) {
                        emojiPopup.dismiss(); // Dismisses the Popup.
                    } else {
                        emojiPopup.toggle();
                    }
                });
    }
}
