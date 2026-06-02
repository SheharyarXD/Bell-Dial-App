package belldial.co.uk;

import android.app.Application;
import android.content.Context;
import belldial.co.uk.utils.Constant;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Constant.init(this);
        FirebaseCrashlytics.getInstance();
        EmojiManager.install(new IosEmojiProvider());
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
