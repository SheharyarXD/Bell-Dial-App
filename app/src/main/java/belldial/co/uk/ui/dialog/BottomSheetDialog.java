package belldial.co.uk.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;
import belldial.co.uk.R;
import belldial.co.uk.ui.adapter.BottomSheetAdapter;
import belldial.co.uk.ui.model.CountryCode;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    String mString;

    BottomSheetDialog bottomsheetDialog;

    CallBackSelectionGroup callBackSelection;

    List<CountryCode> groupLists;

    public void setGroupList(List<CountryCode> groupLists) {

        this.groupLists = groupLists;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rvBottomSheet);

        bottomsheetDialog = this;

        if (groupLists != null) {}

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(
                new BottomSheetAdapter(
                        groupLists,
                        new BottomSheetAdapter.ItemListener() {
                            @Override
                            public void onItemClick(CountryCode item) {

                                Log.e("Item Click ", item.getName().toString());
                                if (bottomsheetDialog != null) {
                                    bottomsheetDialog.dismiss();
                                    callBackSelection.setCallbackSelectionGroup(
                                            item.getName(), item.getIcon(), item.getCode());
                                }
                            }
                        }));

        return v;
    }

    public void setSelectionListner(CallBackSelectionGroup callBackSelection) {
        this.callBackSelection = callBackSelection;
    }

    public interface CallBackSelectionGroup {

        public void setCallbackSelectionGroup(String selection, int selectedId, String code);
    }
}
