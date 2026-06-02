package belldial.co.uk.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.List;
import java.util.Random;
import belldial.co.uk.R;
import belldial.co.uk.ui.model.ContactAddress;

public class SearchContactListAdapter
        extends RecyclerView.Adapter<SearchContactListAdapter.ViewHolder> {

    private Context context;
    ItemListener itemListener;
    private List<ContactAddress> items;
    private final String[] colorsArr = {
        "#2196F3", "#C62828", "#AD1457", "#009688", "#4CAF50", "#7CB342", "#C0CA33", "#FB8C00",
        "#757575", "#9E9E9E", "#6D4C41", "#212121", "#546E7A"
    };

    public SearchContactListAdapter(
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
                        .inflate(R.layout.layout_search_contact_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ContactAddress contactAddress = items.get(position);

        if (contactAddress.contact.getFullName() != null
                && !contactAddress.contact.getFullName().isEmpty()) {
            holder.tvUsername.setText(contactAddress.contact.getFullName());
            holder.flContactChars.setVisibility(View.VISIBLE);
            holder.ivUnknownContact.setVisibility(View.GONE);

            GradientDrawable gd = (GradientDrawable) holder.flContactChars.getBackground();

            Random rand = new Random();
            gd.setColor(Color.parseColor(colorsArr[rand.nextInt((colorsArr.length - 1) + 1)]));

            if (contactAddress.contact.getFullName().length() > 1) {
                holder.tvContactChars.setText(
                        contactAddress.contact.getFullName().substring(0, 2).toUpperCase());
            } else {
                holder.tvContactChars.setText(
                        contactAddress.contact.getFullName().substring(0, 1).toUpperCase());
            }

        } else {
            holder.tvUsername.setText(contactAddress.address);
            holder.flContactChars.setVisibility(View.GONE);
            holder.ivUnknownContact.setVisibility(View.VISIBLE);
        }
        holder.tvNumber.setText(contactAddress.address);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvUserName)
        AppCompatTextView tvUsername;

        @BindView(R.id.tvNumber)
        AppCompatTextView tvNumber;

        @BindView(R.id.ivUnknownContact)
        ImageView ivUnknownContact;

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
                            itemListener.onItemClick(items.get(getAdapterPosition()));
                        }
                    });
        }
    }

    public interface ItemListener {
        void onItemClick(ContactAddress contactAddress);
    }
}
