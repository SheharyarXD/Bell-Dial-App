package belldial.co.uk.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import belldial.co.uk.R;
import belldial.co.uk.ui.model.CountryCode;

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder> {

    private List<CountryCode> mItems;
    private ItemListener mListener;

    public BottomSheetAdapter(List<CountryCode> items, ItemListener listener) {
        mItems = items;
        mListener = listener;
    }

    public void setListener(ItemListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bottom_sheet_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtLocationSearch, code;
        public CountryCode item;
        ImageView imgLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtLocationSearch = (TextView) itemView.findViewById(R.id.text);
            code = (TextView) itemView.findViewById(R.id.textcode);
            imgLogo = (ImageView) itemView.findViewById(R.id.imgLogo);
        }

        public void setData(CountryCode item) {
            this.item = item;
            txtLocationSearch.setText(item.getName());
            code.setText(item.getCode());
            imgLogo.setImageResource(item.getIcon());
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    public interface ItemListener {
        void onItemClick(CountryCode item);
    }
}
