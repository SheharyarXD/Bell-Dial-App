package belldial.co.uk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppUtilities {
    private static final String SHARED_STORAGE = "android-belldial";

    final Context context;

    public AppUtilities(Context context) {
        this.context = context;
    }

    //---------------------------------- SHARED PREFERENCE -----------------------------------------

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_STORAGE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

}
