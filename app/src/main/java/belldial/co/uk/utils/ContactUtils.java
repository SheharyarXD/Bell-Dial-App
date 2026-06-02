package belldial.co.uk.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContactUtils {

    public static List<String> checkReadPermission(Context context) {
        List<String> permissions = new ArrayList<>();
        if (PackageManager.PERMISSION_DENIED
                == ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS))
            permissions.add(Manifest.permission.READ_CONTACTS);
        if (PackageManager.PERMISSION_DENIED
                == ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS))
            permissions.add(Manifest.permission.WRITE_CONTACTS);
        return permissions;
    }

    public static synchronized String getContactDisplayNameByNumber(
            Context context, String number) {
        List<String> perm = checkReadPermission(context);
        if (!perm.isEmpty()) {
            return null;
        }
        Uri uri =
                Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = null;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup =
                contentResolver.query(
                        uri,
                        new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME},
                        null,
                        null,
                        null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name =
                        contactLookup.getString(
                                contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                // String contactId =
                // contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        // android.util.Log.e("DisplayNameByName", " : " + name);

        return name;
    }

    public static String getContactDisplayImage(Context context, String number) {

        // android.util.Log.e("DisplayNameByNumber", " : " + number);

        Uri uri =
                Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String image = null;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup =
                contentResolver.query(
                        uri,
                        new String[] {
                            BaseColumns._ID, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI
                        },
                        null,
                        null,
                        null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                image =
                        contactLookup.getString(
                                contactLookup.getColumnIndex(
                                        ContactsContract.Data.PHOTO_THUMBNAIL_URI));
                // String contactId =
                // contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        // android.util.Log.e("DisplayNameByName", " : " + name);

        return image;
    }

    public static String getTimeAgo(Long duration) {
        Date date = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(date.getTime() - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(date.getTime() - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(date.getTime() - duration);
        long days = TimeUnit.MILLISECONDS.toDays(date.getTime() - duration);

        if (seconds < 60) {
            return "just now";
        } else if (minutes == 1) {
            return "a minute ago";
        } else if (minutes > 1 && minutes < 60) {
            return minutes + " minutes ago";
        } else if (hours == 1) {
            return "an hour ago";
        } else if (hours > 1 && hours < 24) {
            return hours + " hours ago";
        } else if (days > 0 && days < 8) {
            return "1 day ago";
        } else if (days > 7 && days < 31) {

            if (days > 7 && days < 14) {
                return "1 week ago";

            } else if (days > 14 && days < 21) {
                return "2 weeks ago";

            } else if (days > 21 && days < 28) {
                return "3 weeks ago";

            } else if (days > 28 && days < 31) {
                return "4 weeks ago";
            }

        } else if (days > 30 && days < 365) {
            //            long months = days % 30;
            days %= 365;
            long months = days / 30;

            if (months == 1) {
                return "1 month ago";

            } else if (months > 1 && months < 13) {
                return months + " months ago";
            }

        } else {
            long yr = days / 365;

            if (yr == 1) {
                return "1 year ago";

            } else if (yr > 1 && yr < 13) {
                return yr + " years ago";
            }
        }

        return days + " days ago";
    }
}
