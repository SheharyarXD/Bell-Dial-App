package belldial.co.uk.notifications;

/*
NotificationBroadcastReceiver.java
Copyright (C) 2018 Belledonne Communications, Grenoble, France

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

import android.app.Notification;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import belldial.co.uk.LinphoneContext;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import belldial.co.uk.ui.activity.CallIncomingActivity;
import belldial.co.uk.compatibility.Compatibility;
import belldial.co.uk.utils.notifications.CustomRingtoneManager;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.Core;

import java.util.Objects;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case Compatibility.INTENT_REPLY_NOTIF_ACTION:
                case Compatibility.INTENT_MARK_AS_READ_ACTION:
                    handleChatNotification(context, intent);
                    break;
                case Compatibility.INTENT_ANSWER_CALL_NOTIF_ACTION:
                case Compatibility.INTENT_HANGUP_CALL_NOTIF_ACTION:
                    handleCallNotification(context, intent);
                    break;
                default:
                    Log.w(TAG, "Unknown action received: " + action);
                    break;
            }
        }
    }

    private void handleChatNotification(Context context, Intent intent) {
        String action = intent.getAction();

        int notifId = intent.getIntExtra(Compatibility.INTENT_NOTIF_ID, 0);
        String localIdentity = intent.getStringExtra(Compatibility.INTENT_LOCAL_IDENTITY);
        String remoteSipAddr = LinphoneContext.instance().getNotificationManager().getSipUriForNotificationId(notifId);

        Core core = LinphoneManager.getCore();
        if (core == null) {
            Log.e(TAG, "Core instance is null");
            return;
        }

        Address remoteAddr = core.interpretUrl(remoteSipAddr);
        Address localAddr = core.interpretUrl(localIdentity);
        if (remoteAddr == null || localAddr == null) {
            Log.e(TAG, "Failed to interpret address: " + remoteSipAddr + ", " + localIdentity);
            return;
        }

        ChatRoom room = core.getChatRoom(remoteAddr, localAddr);
        if (room == null) {
            Log.e(TAG, "Failed to find chat room");
            return;
        }

        room.markAsRead();

        if (action.equals(Compatibility.INTENT_REPLY_NOTIF_ACTION)) {
            CharSequence reply = getMessageText(intent);
            if (reply == null) {
                Log.e(TAG, "Failed to get reply text");
                return;
            }

            ChatMessage message = room.createMessage(reply.toString());
            message.setUserData(notifId);
            message.addListener(LinphoneContext.instance().getNotificationManager().getMessageListener());
            message.send();
            Log.i(TAG, "Reply sent for notification ID: " + notifId);
        } else {
            LinphoneContext.instance().getNotificationManager().dismissNotification(notifId);
        }
    }

    private void handleCallNotification(Context context, Intent intent) {
        Toast.makeText(context, "handleCallNotification", Toast.LENGTH_LONG).show();
        String action = intent.getAction();
        int notifId = intent.getIntExtra(Compatibility.INTENT_NOTIF_ID, 0);
        String remoteAddr = LinphoneContext.instance().getNotificationManager().getSipUriForCallNotificationId(notifId);

        Core core = LinphoneManager.getCore();
        if (core == null) {
            Log.e(TAG, "Core instance is null");
            return;
        }

        Call call = getCall(remoteAddr);
        if (call == null) {
            Log.e(TAG, "Failed to find call from remote address: " + remoteAddr);
            return;
        }

        CustomRingtoneManager audioManager = CustomRingtoneManager.getInstance(context);

        if (Objects.equals(action, Compatibility.INTENT_ANSWER_CALL_NOTIF_ACTION)) {
            audioManager.stopRingtone();
            call.accept();
            startActivity(context);
        } else {
            audioManager.stopRingtone();
            call.terminate();
        }
    }

    private Call getCall(String phoneNo) {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            for (Call call : core.getCalls()) {
                if (call.getRemoteAddress().getDisplayName().equals(phoneNo)
                        || call.getRemoteAddress().getUsername().equals(phoneNo)) {
                    return call;
                }
            }
        }
        return null;
    }

    private void startActivity(Context context) {
        Intent intent = new Intent(context, CallIncomingActivity.class);
        intent.putExtra("from", "broadCast");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(Compatibility.KEY_TEXT_REPLY);
        }
        return null;
    }
}

