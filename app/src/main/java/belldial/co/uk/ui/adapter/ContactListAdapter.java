package belldial.co.uk.ui.adapter;

/** Created by MyInnos on 01-02-2017. */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.List;
import belldial.co.uk.R;
import belldial.co.uk.ui.model.ContactAddress;
import belldial.co.uk.contacts.LinphoneContact;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private Context context;
    ItemListener itemListener;
    private List<ContactAddress> items;

    public ContactListAdapter(
            Context context, List<ContactAddress> items, ItemListener itemListener) {
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
                        .inflate(R.layout.row_contact_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ContactAddress contact = items.get(position);
        final String a = contact.address;
        final LinphoneContact subsidies = contact.contact;
        //  Log.e("contact", "onBindViewHolder: " + subsidies.toString());
        holder.textViewConatctName.setText(subsidies.getFullName());
        holder.textViewNumber.setText(a);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewConatctName)
        AppCompatTextView textViewConatctName;

        @BindView(R.id.textViewNumber)
        AppCompatTextView textViewNumber;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemListener.onItemClick(items.get(getAdapterPosition()));
                        }
                    });
        }
    }

    public interface ItemListener {
        void onItemClick(ContactAddress contactAddress);
    }
}
