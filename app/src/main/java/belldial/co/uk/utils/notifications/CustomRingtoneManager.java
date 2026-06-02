package belldial.co.uk.utils.notifications;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;

import belldial.co.uk.R;

public class CustomRingtoneManager implements AudioInterface {
    private static CustomRingtoneManager instance;
    private Ringtone ringtone;
    private Context context;


    // Private constructor to prevent instantiation from outside
    private CustomRingtoneManager(Context context) {
        this.context = context;
    }

    // Static method to get the singleton instance
    public static synchronized CustomRingtoneManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomRingtoneManager(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void startRingtone() {
        stopRingtone(); // Stop any previous ringtone
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        if (ringtoneUri == null) {
            // No default notification sound, fall back to app's default sound
            ringtoneUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);
        }
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
        if (ringtone != null) {
            ringtone.play();
        }
    }

    @Override
    public void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}



