package belldial.co.uk.ui.adapter;

/** Created by MyInnos on 01-02-2017. */
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import belldial.co.uk.R;
import belldial.co.uk.utils.ContactUtils;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import belldial.co.uk.utils.LinphoneUtils;

public class MissedCallsAdapter extends RecyclerView.Adapter<MissedCallsAdapter.ViewHolder> {

    private Context context;
    private ItemListener itemListener;
    private List<CallLog> mLogs;

    public MissedCallsAdapter(Context context, List<CallLog> mLogs, ItemListener itemListener) {
        this.context = context;
        this.mLogs = mLogs;
        this.itemListener = itemListener;
    }

    @Override
    public int getItemCount() {

        if (mLogs == null) return 0;
        return mLogs.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_missed_call_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CallLog log = mLogs.get(position);
        long timestamp = log.getStartDate() * 1000;
        final Address address;

        Calendar logTime = Calendar.getInstance();
        logTime.setTimeInMillis(timestamp);
        holder.separatorText.setText(timestampToHumanDate(logTime));

        if (position > 0) {
            CallLog previousLog = mLogs.get(position - 1);
            long previousTimestamp = previousLog.getStartDate() * 1000;
            Calendar previousLogTime = Calendar.getInstance();
            previousLogTime.setTimeInMillis(previousTimestamp);

            if (isSameDay(previousLogTime, logTime)) {
                holder.separator.setVisibility(View.GONE);
            } else {
                holder.separator.setVisibility(View.VISIBLE);
            }
        } else {
            holder.separator.setVisibility(View.VISIBLE);
        }

        if (log.getDir() == Call.Dir.Incoming) {
            address = log.getFromAddress();
            if (log.getStatus() == Call.Status.Missed) {
                holder.imageViewConactImage.setImageResource(R.drawable.misscall);
                holder.textViewCallType.setText("Missed Call");
                holder.textViewCallType.setTextColor(
                        context.getResources().getColor(R.color.colorI));
            } else {
                holder.imageViewConactImage.setImageResource(R.drawable.icon_receive_call);
                holder.textViewCallType.setText("Inbound Call");
            }
        } else {
            address = log.getToAddress();
            holder.imageViewConactImage.setImageResource(R.drawable.receive_call);
        }
        holder.textViewTime.setText(
                LinphoneUtils.timestampToHumanDate(context, log.getStartDate(), "h:mm a"));
        holder.textViewDate.setText(
                LinphoneUtils.timestampToHumanDateNew(context, log.getStartDate(), "dd/MM/yyyy"));
        LinphoneContact c = ContactsManager.getInstance().findContactFromAddress(address);

        try {
            final String contactBookName =
                    ContactUtils.getContactDisplayNameByNumber(
                            context,
                            LinphoneUtils.getDisplayableUsernameFromAddress(address.getUsername()));
            holder.textViewConatctName.setText(
                    contactBookName == null
                            ? LinphoneUtils.getAddressDisplayName(address)
                            : contactBookName);

        } catch (NullPointerException e) {

        }
        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemListener.onItemClick(
                                LinphoneUtils.getDisplayableUsernameFromAddress(
                                        address.getUsername()));
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.separator_text)
        AppCompatTextView separatorText;

        @BindView(R.id.separator)
        LinearLayout separator;

        @BindView(R.id.imageViewConactImage)
        AppCompatImageView imageViewConactImage;

        @BindView(R.id.textViewConatctName)
        AppCompatTextView textViewConatctName;

        @BindView(R.id.textViewDate)
        AppCompatTextView textViewDate;

        @BindView(R.id.textViewCallType)
        AppCompatTextView textViewCallType;

        @BindView(R.id.textViewTime)
        AppCompatTextView textViewTime;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemListener {
        void onItemClick(String displayableUsernameFromAddress);
    }

    @SuppressLint("SimpleDateFormat")
    private String timestampToHumanDate(Calendar cal) {
        SimpleDateFormat dateFormat;
        if (isToday(cal)) {
            return context.getString(R.string.today);
        } else if (isYesterday(cal)) {
            return context.getString(R.string.yesterday);
        } else {
            dateFormat =
                    new SimpleDateFormat(
                            context.getResources().getString(R.string.history_date_format));
        }

        return dateFormat.format(cal.getTime());
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }

        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    private boolean isToday(Calendar cal) {
        return isSameDay(cal, Calendar.getInstance());
    }

    private boolean isYesterday(Calendar cal) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.roll(Calendar.DAY_OF_MONTH, -1);
        return isSameDay(cal, yesterday);
    }
}
