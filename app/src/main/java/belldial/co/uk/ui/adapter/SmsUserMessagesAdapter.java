package belldial.co.uk.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.List;
import belldial.co.uk.R;
import belldial.co.uk.api.model.Sms;

public class SmsUserMessagesAdapter
        extends RecyclerView.Adapter<SmsUserMessagesAdapter.ViewHolder> {

    private Context context;
    private List<Sms> smsList;
    private int successMessage = 1;
    private int failedMessage = 2;

    public SmsUserMessagesAdapter(Context context, List<Sms> smsList) {
        this.context = context;
        this.smsList = smsList;
    }

    public SmsUserMessagesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        if (smsList == null) return 0;
        return smsList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == failedMessage) {
            v =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_failed_row, parent, false);
        } else {
            v =
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message2_row, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Sms sms = smsList.get(position);

        holder.txtMessage.setText(sms.getMessage());

        /*if (sms.getCreatedDate().equals("Just now")) {
            holder.tvTime.setText("Just now");
        } else {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            try {
                date = dateFormatter.parse(sms.getCreatedDate());
                holder.tvTime.setText(ContactUtils.getTimeAgo(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public int getItemViewType(int position) {

        if (smsList.get(position).getStatus().equals("sent")) {
            return successMessage;
        }

        return failedMessage;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtMessage)
        TextView txtMessage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
