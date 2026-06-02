package belldial.co.uk.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import belldial.co.uk.R;
import belldial.co.uk.ui.activity.PrivacyPolicyActivity;

public class PrivacyDialog extends DialogFragment {
    private TextView agreeButton;
    private TextView privacyText;
    private SharedPreferences sharedPreferences;
    private Runnable acceptHandler;

    public void addAcceptHandler(Runnable acceptHandler) {
        this.acceptHandler = acceptHandler;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.privacy_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        agreeButton = view.findViewById(R.id.continue_btn);
        privacyText = view.findViewById(R.id.dialog_text);
        sharedPreferences =
                requireActivity().getSharedPreferences("privacy_mode", Context.MODE_PRIVATE);
        agreeButton.setOnClickListener(
                v -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("privacy_Accepted", true);
                    editor.apply();
                    dismiss();
                    acceptHandler.run();
                });
        String description =
                "By tapping Agree and continue you agree to our Terms of Service and acknowledge that you have read our Privacy Policy to learn how we collect, use and share your data. ";
        SpannableString desc = new SpannableString(description);
        addBoldness(desc, description, "Terms of Service");
        addBoldness(desc, description, "Privacy Policy");
        privacyText.setText(desc);
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addBoldness(SpannableString spannableString, String description, String word) {
        spannableString.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (word.equals("Privacy Policy")) {
                            startActivity(
                                    new Intent(requireContext(), PrivacyPolicyActivity.class)
                                            .putExtra("title", word)
                                            .putExtra("link", "https://belldial.co.uk/privacy/"));
                        } else if (word.equals("Terms of Service")) {
                            startActivity(
                                    new Intent(requireContext(), PrivacyPolicyActivity.class)
                                            .putExtra("title", word)
                                            .putExtra(
                                                    "link",
                                                    "https://belldial.co.uk/terms-and-condition/"));
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
                    }
                },
                description.indexOf(word),
                description.indexOf(word) + word.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onStart() {
        super.onStart();
        requireDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static boolean shouldShow(Context context) {
        return !context.getSharedPreferences("privacy_mode", Context.MODE_PRIVATE)
                .getBoolean("privacy_Accepted", false);
    }

    public static PrivacyDialog newInstance() {
        return new PrivacyDialog();
    }
}
