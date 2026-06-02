package belldial.co.uk.ui.fragments;

import android.Manifest;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.ui.adapter.RecordingsAdapter;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.CallRecordingPresenter;
import belldial.co.uk.ui.views.CallRecordingView;
import belldial.co.uk.recording.Recording;
import belldial.co.uk.utils.FileUtils;
import butterknife.BindView;
import butterknife.OnClick;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

/** A simple {@link Fragment} subclass. */
public class CallRecordingFragment extends BaseFragment<CallRecordingPresenter, CallRecordingView>
        implements CallRecordingView, RecordingsAdapter.ItemListener {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.recyclerViewRecording)
    RecyclerView recyclerViewRecording;

    @BindView(R.id.no_recordings)
    TextView noRecordings;

    private List<Recording> mRecordings;
    private RecordingsAdapter mRecordingsAdapter;
    protected String[] mPermissionsToHave;

    @Override
    protected int createLayout() {
        return R.layout.fragment_recording;
    }

    @Override
    protected void setPresenter() {
        presenter = new CallRecordingPresenter();
    }

    @Override
    protected CallRecordingView createView() {
        return this;
    }

    @Override
    protected void bindData() {
        imageViewSwitchOff.setVisibility(View.GONE);
        toolBarTitle.setText("Recordings");
        ((HomeActivity) getActivity()).hideTabBar(true);
        mRecordings = new ArrayList<>();
        mPermissionsToHave = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    @OnClick(R.id.imageViewBack)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();

        LinphoneManager.getAudioManager().setAudioManagerModeNormal();
        LinphoneManager.getAudioManager().routeAudioToSpeaker();

        removeDeletedRecordings();
        searchForRecordings();
        hideRecordingListAndDisplayMessageIfEmpty();
        recyclerViewRecording.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecordingsAdapter = new RecordingsAdapter(getActivity(), mRecordings, this);
        recyclerViewRecording.setAdapter(mRecordingsAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        LinphoneManager.getAudioManager().routeAudioToEarPiece();

        // Close all opened mRecordings
        for (Recording r : mRecordings) {
            if (!r.isClosed()) {
                if (r.isPlaying()) r.pause();
                r.close();
            }
        }
    }

    @Override
    public void onItemClick(Recording recordin, int position) {
        Recording recording = mRecordings.get(position);
        if (recording.isPlaying()) recording.pause();
        recording.close();

        File recordingFile = new File(recording.getRecordPath());
        if (recordingFile.delete()) {
            mRecordings.remove(position);
        }

        mRecordingsAdapter.notifyDataSetChanged();
        hideRecordingListAndDisplayMessageIfEmpty();
    }

    private void removeDeletedRecordings() {
        String recordingsDirectory = FileUtils.getRecordingsDirectory(getContext());
        File directory = new File(recordingsDirectory);

        if (directory.exists() && directory.isDirectory()) {
            File[] existingRecordings = directory.listFiles();

            for (Recording r : mRecordings) {
                boolean exists = false;
                for (File f : existingRecordings) {
                    if (f.getPath().equals(r.getRecordPath())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) mRecordings.remove(r);
            }

            Collections.sort(mRecordings);
        }
        hideRecordingListAndDisplayMessageIfEmpty();
    }

    private void searchForRecordings() {
        String recordingsDirectory = FileUtils.getRecordingsDirectory(getContext());
        File directory = new File(recordingsDirectory);

        if (directory.exists() && directory.isDirectory()) {
            File[] existingRecordings = directory.listFiles();
            if (existingRecordings == null) return;

            for (File f : existingRecordings) {
                boolean exists = false;
                for (Recording r : mRecordings) {
                    if (r.getRecordPath().equals(f.getPath())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    if (Recording.RECORD_PATTERN.matcher(f.getPath()).matches()) {
                        mRecordings.add(new Recording(getActivity(), f.getPath()));
                    }
                }
            }

            Collections.sort(mRecordings);
        }
    }

    private void hideRecordingListAndDisplayMessageIfEmpty() {
        if (mRecordings == null || mRecordings.isEmpty()) {
            noRecordings.setVisibility(View.VISIBLE);
            recyclerViewRecording.setVisibility(View.GONE);
        } else {
            noRecordings.setVisibility(View.GONE);
            recyclerViewRecording.setVisibility(View.VISIBLE);
        }
    }
}
