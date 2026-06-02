package belldial.co.uk.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import belldial.co.uk.utils.Constant;
import org.linphone.core.Core;
import org.linphone.core.Account;
// import org.linphone.core.CoreListener;
import belldial.co.uk.utils.SimpleCoreListener;
import org.linphone.core.RegistrationState;

public class SmsToEmailActivity extends AppCompatActivity {
    ImageView settings, back, signal;
    private SimpleCoreListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_to_email);
        settings = findViewById(R.id.settings);
        back = findViewById(R.id.imgBack);
        signal = findViewById(R.id.imageViewstat);
        bindData();

        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
    }

    protected void bindData() {
        mListener = new SimpleCoreListener() {
            @Override
            public void onAccountRegistrationStateChanged(
                    final Core lc,
                    final Account account,
                    final RegistrationState state,
                    String smessage) {
                if (lc.getAccountList() == null) {
                    signal.setImageResource(R.drawable.signal_null);

                } else {
                    // statusLed.setVisibility(View.VISIBLE);
                }

                if (lc.getDefaultAccount() != null
                        && lc.getDefaultAccount().equals(account)) {
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
            lc.addListener(mListener);
            Account lpc = lc.getDefaultAccount();
            if (lpc != null) {
                mListener.onAccountRegistrationStateChanged(lc, lpc, lpc.getState(), null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Core lc = LinphoneManager.getCore();
        if (lc != null) {
            lc.removeListener(mListener);
        }
    }
}
