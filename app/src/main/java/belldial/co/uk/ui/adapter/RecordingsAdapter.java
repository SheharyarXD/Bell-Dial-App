package belldial.co.uk.ui.adapter;

/** Created by MyInnos on 01-02-2017. */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import belldial.co.uk.R;
import belldial.co.uk.recording.Recording;
import belldial.co.uk.recording.RecordingListener;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private Context context;
    private ItemListener itemListener;
    private List<Recording> recordingList;
    private int playingPosition;

    public RecordingsAdapter(
            Context context, List<Recording> recordingList, ItemListener itemListener) {
        this.context = context;
        this.recordingList = recordingList;
        this.itemListener = itemListener;
    }

    public RecordingsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        if (recordingList == null) return 0;
        return recordingList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_recording_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Recording record = recordingList.get(position);

        if (record.isPlaying()) {
            holder.imageViewIconPlay.setImageResource(R.drawable.pause);
        } else {
            holder.imageViewIconPlay.setImageResource(R.drawable.play_button);
        }
        if (record.isIsshown()) {
            holder.layoutSubRecording.setVisibility(View.VISIBLE);
        } else {
            holder.layoutSubRecording.setVisibility(View.GONE);
        }
        holder.imageViewIconPlay.setOnClickListener(
                v -> {
                    if (record.isPaused()) {
                        record.play();
                        holder.imageViewIconPlay.setImageResource(R.drawable.pause);
                    } else {
                        record.pause();
                        holder.imageViewIconPlay.setImageResource(R.drawable.play_button);
                    }
                });
        holder.textViewDateTime.setText(
                new SimpleDateFormat("YYYYddMM HH:mm").format(record.getRecordDate()));
        holder.textViewConatctName.setText(record.getName());
        int duration = record.getDuration();
        holder.textViewDuration.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration)
                                - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(duration))));

        holder.seekbarRecording.setMax(record.getDuration());
        holder.seekbarRecording.setProgress(0);
        holder.seekbarRecording.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            int progressToSet =
                                    progress > 0 && progress < seekBar.getMax() ? progress : 0;

                            if (progress == seekBar.getMax()) {
                                if (record.isPlaying()) record.pause();
                            }

                            record.seek(progressToSet);
                            // holder.seekbarRecording.setProgress(progressToSet);

                            /*  int currentPosition = record.getCurrentPosition();
                            viewHolder.currentPosition.setText(
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                                            TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                                                    - TimeUnit.MINUTES.toSeconds(
                                                    TimeUnit.MILLISECONDS.toMinutes(
                                                            currentPosition))));*/
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        record.setRecordingListener(
                new RecordingListener() {
                    @Override
                    public void currentPositionChanged(int currentPosition) {
                        /*viewHolder.currentPosition.setText(
                        String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(currentPosition)
                                        - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(
                                                currentPosition))));*/
                        holder.seekbarRecording.setProgress(currentPosition);
                    }

                    @Override
                    public void endOfRecordReached() {
                        record.pause();
                        record.seek(0);
                        holder.seekbarRecording.setProgress(0);
                        // viewHolder.currentPosition.setText("00:00");
                        holder.imageViewIconPlay.setImageResource(R.drawable.play_button);
                    }
                });
        holder.imageViewDelete.setOnClickListener(
                v -> itemListener.onItemClick(recordingList.get(position), position));
        holder.itemView.setOnClickListener(
                v -> {
                    if (recordingList.get(position).isIsshown()) {
                        if (recordingList.get(position).isPlaying()) {
                            recordingList.get(position).pause();
                            recordingList.get(position).close();
                        }
                        recordingList.get(position).setIsshown(false);
                        notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < recordingList.size(); i++) {
                            if (recordingList.get(i).isIsshown()) {
                                if (recordingList.get(i).isPlaying()) {
                                    recordingList.get(i).pause();
                                    recordingList.get(i).close();
                                }
                                recordingList.get(i).setIsshown(false);
                            }
                        }
                        recordingList.get(position).setIsshown(true);
                        notifyDataSetChanged();
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textViewConatctName)
        AppCompatTextView textViewConatctName;

        @BindView(R.id.textViewDuration)
        AppCompatTextView textViewDuration;

        @BindView(R.id.imageViewDelete)
        AppCompatImageView imageViewDelete;

        @BindView(R.id.imageViewIconPause)
        AppCompatImageView imageViewIconPause;

        @BindView(R.id.imageViewIconPlay)
        AppCompatImageView imageViewIconPlay;

        @BindView(R.id.seekbarRecording)
        SeekBar seekbarRecording;

        @BindView(R.id.layoutSubRecording)
        LinearLayout layoutSubRecording;

        @BindView(R.id.textViewDateTime)
        AppCompatTextView textViewDateTime;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemListener {
        void onItemClick(Recording recording, int position);
    }
}
