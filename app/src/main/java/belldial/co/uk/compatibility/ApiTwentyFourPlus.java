package belldial.co.uk.compatibility;

/*
ApiTwentyFourPlus.java
Copyright (C) 2017 Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import static belldial.co.uk.compatibility.Compatibility.CHAT_NOTIFICATIONS_GROUP;
import static belldial.co.uk.compatibility.Compatibility.INTENT_ANSWER_CALL_NOTIF_ACTION;
import static belldial.co.uk.compatibility.Compatibility.INTENT_HANGUP_CALL_NOTIF_ACTION;
import static belldial.co.uk.compatibility.Compatibility.INTENT_LOCAL_IDENTITY;
import static belldial.co.uk.compatibility.Compatibility.INTENT_MARK_AS_READ_ACTION;
import static belldial.co.uk.compatibility.Compatibility.INTENT_NOTIF_ID;
import static belldial.co.uk.compatibility.Compatibility.INTENT_REPLY_NOTIF_ACTION;
import static belldial.co.uk.compatibility.Compatibility.KEY_TEXT_REPLY;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.text.Html;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import belldial.co.uk.R;
import belldial.co.uk.notifications.Notifiable;
import belldial.co.uk.notifications.NotifiableMessage;
import belldial.co.uk.notifications.NotificationBroadcastReceiver;

@TargetApi(24)
class ApiTwentyFourPlus {

    public static Notification createRepliedNotification(Context context, String reply) {
        return new Notification.Builder(context)
                .setSmallIcon(R.drawable.topbar_chat_notification)
                .setContentText(
                        context.getString(R.string.notification_replied_label).replace("%s", reply))
                .build();
    }

    public static Notification createMessageNotification(
            Context context, Notifiable notif, Bitmap contactIcon, PendingIntent intent) {

        Notification.MessagingStyle style = new Notification.MessagingStyle(notif.getMyself());
        for (NotifiableMessage message : notif.getMessages()) {
            Notification.MessagingStyle.Message msg =
                    new Notification.MessagingStyle.Message(
                            message.getMessage(), message.getTime(), message.getSender());
            if (message.getFilePath() != null)
                msg.setData(message.getFileMime(), message.getFilePath());
            style.addMessage(msg);
        }
        if (notif.isGroup()) {
            style.setConversationTitle(notif.getGroupTitle());
        }

        return new Notification.Builder(context)
                .setSmallIcon(R.drawable.topbar_chat_notification)
                .setAutoCancel(true)
                .setContentIntent(intent)
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE
                                | Notification.DEFAULT_LIGHTS)
                .setLargeIcon(contactIcon)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setGroup(CHAT_NOTIFICATIONS_GROUP)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setNumber(notif.getMessages().size())
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setColor(context.getColor(R.color.notification_led_color))
                .setStyle(style)
                .addAction(Compatibility.getReplyMessageAction(context, notif))
                .addAction(Compatibility.getMarkMessageAsReadAction(context, notif))
                .build();
    }

    public static Notification createMissedCallNotification(
            Context context, String msg, String contactName, PendingIntent intent) {

        return new Notification.Builder(context)
                .setContentTitle(msg)
                .setContentText(contactName)
                .setSmallIcon(R.drawable.call_status_missed)
                .setAutoCancel(false)
                .setContentIntent(intent)
                .setCategory(Notification.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(context.getColor(R.color.notification_led_color))
                .build();
    }

    public static Notification createInCallNotification(
            Context context,
            int callId,
            String msg,
            int iconID,
            Bitmap contactIcon,
            String contactName,
            PendingIntent intent) {

        return new Notification.Builder(context)
                .setContentTitle(contactName)
                .setContentText(msg)
                .setSmallIcon(iconID)
                .setAutoCancel(false)
                .setContentIntent(intent)
                .setLargeIcon(contactIcon)
                .setCategory(Notification.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(context.getColor(R.color.notification_led_color))
                .addAction(Compatibility.getCallDeclineAction(context, callId))
                .build();
    }

    public static Notification createIncomingCallNotification(
            Context context,
            int callId,
            Bitmap contactIcon,
            String contactName,
            String sipUri,
            PendingIntent intent) {
        RemoteViews notificationLayoutHeadsUp =
                new RemoteViews(
                        context.getPackageName(), R.layout.call_incoming_notification_heads_up);
        notificationLayoutHeadsUp.setTextViewText(R.id.caller, contactName);
        // notificationLayoutHeadsUp.setTextViewText(R.id.sip_uri, sipUri);
        notificationLayoutHeadsUp.setTextViewText(
                R.id.incoming_call_info, context.getString(R.string.incall_notif_incoming));
        /*   if (contactIcon != null) {
            notificationLayoutHeadsUp.setImageViewBitmap(R.id.caller_picture, contactIcon);
        }*/

        return new Notification.Builder(context)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .setSmallIcon(R.drawable.topbar_call_notification)
                .setLargeIcon(contactIcon)
                .setContentTitle(contactName)
                .setContentText(
                        Html.fromHtml(
                                "<font color=\""
                                        + ContextCompat.getColor(context, R.color.colorGN)
                                        + "\">"
                                        + context.getString(R.string.incall_notif_incoming)
                                        + "</font>",
                                HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setContentIntent(intent)
                .setCategory(Notification.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(context.getColor(R.color.notification_led_color))
                .addAction(Compatibility.getCallDeclineAction(context, callId))
                .addAction(Compatibility.getCallAnswerAction(context, callId))
                .setCustomHeadsUpContentView(notificationLayoutHeadsUp)
                .setFullScreenIntent(intent, true)
                .build();
    }

    public static Notification.Action getReplyMessageAction(Context context, Notifiable notif) {
        String replyLabel = context.getResources().getString(R.string.notification_reply_label);
        RemoteInput remoteInput =
                new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build();

        Intent replyIntent = new Intent(context, NotificationBroadcastReceiver.class);
        replyIntent.setAction(INTENT_REPLY_NOTIF_ACTION);
        replyIntent.putExtra(INTENT_NOTIF_ID, notif.getNotificationId());
        replyIntent.putExtra(INTENT_LOCAL_IDENTITY, notif.getLocalIdentity());

        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        notif.getNotificationId(),
                        replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Action.Builder(
                R.drawable.chat_send_over,
                context.getString(R.string.notification_reply_label),
                replyPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();
    }

    public static Notification.Action getMarkMessageAsReadAction(
            Context context, Notifiable notif) {
        Intent markAsReadIntent = new Intent(context, NotificationBroadcastReceiver.class);
        markAsReadIntent.setAction(INTENT_MARK_AS_READ_ACTION);
        markAsReadIntent.putExtra(INTENT_NOTIF_ID, notif.getNotificationId());
        markAsReadIntent.putExtra(INTENT_LOCAL_IDENTITY, notif.getLocalIdentity());

        PendingIntent markAsReadPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        notif.getNotificationId(),
                        markAsReadIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Action.Builder(
                R.drawable.chat_send_over,
                context.getString(R.string.notification_mark_as_read_label),
                markAsReadPendingIntent)
                .build();
    }

    public static Notification.Action getCallAnswerAction(Context context, int callId) {
        Intent answerIntent = new Intent(context, NotificationBroadcastReceiver.class);
        answerIntent.setAction(INTENT_ANSWER_CALL_NOTIF_ACTION);
        answerIntent.putExtra(INTENT_NOTIF_ID, callId);

        PendingIntent answerPendingIntent = PendingIntent.getBroadcast(context, callId, answerIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Action.Builder(
                R.drawable.icon_accept,
                Html.fromHtml(
                        "<font color=\""
                                + ContextCompat.getColor(context, R.color.colorGN)
                                + "\">"
                                + context.getString(R.string.notification_call_answer_label)
                                + "</font>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY),
                answerPendingIntent)
                .build();
    }

    public static Notification.Action getCallDeclineAction(Context context, int callId) {
        Intent hangupIntent = new Intent(context, NotificationBroadcastReceiver.class);
        hangupIntent.setAction(INTENT_HANGUP_CALL_NOTIF_ACTION);
        hangupIntent.putExtra(INTENT_NOTIF_ID, callId);

        PendingIntent hangupPendingIntent =
                PendingIntent.getBroadcast(
                        context, callId, hangupIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        return new Notification.Action.Builder(
                R.drawable.icon_decline,
                Html.fromHtml(
                        "<font color=\""
                                + ContextCompat.getColor(context, R.color.colorI)
                                + "\">"
                                + context.getString(R.string.notification_call_hangup_label)
                                + "</font>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY),
                hangupPendingIntent)
                .build();
    }

    public static void closeContentProviderClient(ContentProviderClient client) {
        client.close();
    }
}
