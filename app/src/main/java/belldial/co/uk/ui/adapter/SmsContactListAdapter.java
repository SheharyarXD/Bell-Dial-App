package belldial.co.uk.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.makeramen.roundedimageview.RoundedImageView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import belldial.co.uk.R;

import belldial.co.uk.api.model.MessagesResponse;
import belldial.co.uk.ui.activity.HomeActivity;
import belldial.co.uk.ui.model.ContactAddress;
import belldial.co.uk.utils.ContactUtils;

public class SmsContactListAdapter extends RecyclerView.Adapter<SmsContactListAdapter.ViewHolder> {

    private Context context;
    ItemListener itemListener;
    private List<MessagesResponse> items;
    private final String[] colorsArr = {
        "#2196F3", "#C62828", "#AD1457", "#009688", "#4CAF50", "#7CB342", "#C0CA33", "#FB8C00",
        "#757575", "#9E9E9E", "#6D4C41", "#212121", "#546E7A"
    };

    public SmsContactListAdapter(
            Context context, List<MessagesResponse> items, ItemListener itemListener) {
        this.context = context;
        this.items = items;
        this.itemListener = itemListener;
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_contact_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MessagesResponse messagesResponse = items.get(position);

        if (messagesResponse.getFullName() != null && !messagesResponse.getFullName().isEmpty()) {
            holder.tvUsername.setText(messagesResponse.getFullName());
            holder.flContactChars.setVisibility(View.VISIBLE);
            holder.ivUnknownContact.setVisibility(View.GONE);

            GradientDrawable gd = (GradientDrawable) holder.flContactChars.getBackground();

            Random rand = new Random();
            gd.setColor(Color.parseColor(colorsArr[rand.nextInt((colorsArr.length - 1) + 1)]));

            if (messagesResponse.getFullName().length() > 1) {
                holder.tvContactChars.setText(
                        messagesResponse.getFullName().substring(0, 2).toUpperCase());
            } else {
                holder.tvContactChars.setText(
                        messagesResponse.getFullName().substring(0, 1).toUpperCase());
            }

        } else {
            holder.tvUsername.setText(messagesResponse.getReceiverNumber());
            holder.flContactChars.setVisibility(View.GONE);
            holder.ivUnknownContact.setVisibility(View.VISIBLE);
        }
        holder.tvLastMsg.setText(messagesResponse.getSmsList().get(0).getMessage());

        String time = messagesResponse.getSmsList().get(0).getCreatedDate();

        if (time.equals("Just now")) {
            holder.tvTime.setText("Just now");
        } else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = dateFormatter.parse(time);
                holder.tvTime.setText(ContactUtils.getTimeAgo(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*Intent intent = new Intent(context, MessagesActivity.class);
                        intent.putExtra("messageResponse", messagesResponse);
                        context.startActivity(intent);*/
                        HomeActivity homeActivity = (HomeActivity) context;
                        homeActivity.openSmsFragmentFromAdapter(messagesResponse, position);
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUserName)
        AppCompatTextView tvUsername;

        @BindView(R.id.tvLastMsg)
        AppCompatTextView tvLastMsg;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.ivUnknownContact)
        RoundedImageView ivUnknownContact;

        @BindView(R.id.flContactChars)
        FrameLayout flContactChars;

        @BindView(R.id.tvContactChars)
        TextView tvContactChars;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //
                            // itemListener.onItemClick(items.get(getAdapterPosition()));
                        }
                    });
        }
    }

    public interface ItemListener {
        void onItemClick(ContactAddress contactAddress);
    }
}
