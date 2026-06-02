package belldial.co.uk.notifications;

/*
NotificationsManager.java
Copyright (C) 2018  Belledonne Communications, Grenoble, France

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

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.File;
import java.util.HashMap;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.R;
import belldial.co.uk.ui.activity.CallIncomingActivity;
import belldial.co.uk.ui.activity.CallOutgoingActivity;
import belldial.co.uk.ui.activity.HomeActivity;
import belldial.co.uk.chat.ChatActivity;
import belldial.co.uk.compatibility.Compatibility;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageListener;
import org.linphone.core.ChatMessageReaction;
import org.linphone.core.ChatRoom;
import org.linphone.core.Content;
import org.linphone.core.Core;
// import org.linphone.core.CoreListener;
import org.linphone.core.Reason;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.DeviceUtils;
import belldial.co.uk.utils.FileUtils;
import belldial.co.uk.utils.ImageUtils;
import belldial.co.uk.utils.LinphoneUtils;
import belldial.co.uk.utils.MediaScannerListener;

import belldial.co.uk.utils.SimpleCoreListener;

public class NotificationsManager {
    private static final int SERVICE_NOTIF_ID = 1;
    private static final int MISSED_CALLS_NOTIF_ID = 2;

    private final Context mContext;
    private final NotificationManager mNM;
    private final HashMap<String, Notifiable> mChatNotifMap;
    private final HashMap<String, Notifiable> mCallNotifMap;
    private int mLastNotificationId;
    private final Notification mServiceNotification;
    private int mCurrentForegroundServiceNotification;
    private String mCurrentChatRoomAddress;
    private SimpleCoreListener mListener;
    private ChatMessageListener mMessageListener;

    public NotificationsManager(Context context) {
        mContext = context;
        mChatNotifMap = new HashMap<>();
        mCallNotifMap = new HashMap<>();
        mCurrentForegroundServiceNotification = 0;
        mCurrentChatRoomAddress = null;

        mNM = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNM.cancelAll();

        mLastNotificationId = 5; // Do not conflict with hardcoded notifications ids !

        Compatibility.createNotificationChannels(mContext);

        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        } catch (Exception e) {
            Log.e(e);
        }

        Intent notifIntent = new Intent(mContext, HomeActivity.class);
        notifIntent.putExtra("Notification", true);
        addFlagsToIntent(notifIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext, SERVICE_NOTIF_ID, notifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mServiceNotification = Compatibility.createNotification(
                mContext,
                mContext.getString(R.string.service_name),
                "",
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                bm,
                pendingIntent,
                Notification.PRIORITY_MIN,
                true);

        mListener = new SimpleCoreListener() {
            @Override
            public void onMessageSent(Core core, ChatRoom room, ChatMessage message) {
                if (room.hasCapability(ChatRoom.Capabilities.OneToOne.toInt())) {
                    Compatibility.createChatShortcuts(mContext);
                }
            }

            @Override
            public void onMessageReceived(
                    Core core, final ChatRoom cr, final ChatMessage message) {
                if (message.isOutgoing()
                        || mContext.getResources().getBoolean(R.bool.disable_chat)
                        || mContext.getResources()
                                .getBoolean(R.bool.disable_chat_message_notification)) {
                    return;
                }

                if (mCurrentChatRoomAddress != null
                        && mCurrentChatRoomAddress.equals(
                                cr.getPeerAddress().asStringUriOnly())) {
                    Log.i(
                            "[Notifications Manager] Message received for currently displayed chat room, do not make a notification");
                    return;
                }

                if (message.getErrorInfo() != null
                        && message.getErrorInfo().getReason() == Reason.UnsupportedContent) {
                    Log.w(
                            "[Notifications Manager] Message received but content is unsupported, do not notify it");
                    return;
                }

                if (!message.hasTextContent()
                        && message.getFileTransferInformation() == null) {
                    Log.w(
                            "[Notifications Manager] Message has no text or file transfer information to display, ignoring it...");
                    return;
                }

                final Address from = message.getFromAddress();
                final LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(from);
                final String textMessage = (message.hasTextContent())
                        ? message.getTextContent()
                        : mContext.getString(
                                R.string.content_description_incoming_file);

                String file = null;
                for (Content c : message.getContents()) {
                    if (c.isFile()) {
                        file = c.getFilePath();
                        LinphoneManager.getInstance()
                                .getMediaScanner()
                                .scanFile(
                                        new File(file),
                                        new MediaScannerListener() {
                                            @Override
                                            public void onMediaScanned(
                                                    String path, Uri uri) {
                                                createNotification(
                                                        cr,
                                                        contact,
                                                        from,
                                                        textMessage,
                                                        message.getTime(),
                                                        uri,
                                                        FileUtils.getMimeFromFile(path));
                                            }
                                        });
                        break;
                    }
                }

                if (file == null) {
                    createNotification(
                            cr, contact, from, textMessage, message.getTime(), null, null);
                }

                if (cr.hasCapability(ChatRoom.Capabilities.OneToOne.toInt())) {
                    Compatibility.createChatShortcuts(mContext);
                }
            }
        };

        mMessageListener = new ChatMessageListener() {
            @Override
            public void onMsgStateChanged(ChatMessage msg, ChatMessage.State state) {
                if (msg.getUserData() == null)
                    return;
                int notifId = (int) msg.getUserData();
                Log.i(
                        "[Notifications Manager] Reply message state changed ("
                                + state.name()
                                + ") for notif id "
                                + notifId);

                if (state != ChatMessage.State.InProgress) {
                    // There is no need to be called here twice
                    msg.removeListener(this);
                }

                if (state == ChatMessage.State.Delivered
                        || state == ChatMessage.State.Displayed) {
                    Notifiable notif = mChatNotifMap.get(
                            msg.getChatRoom().getPeerAddress().asStringUriOnly());
                    if (notif == null) {
                        Log.e(
                                "[Notifications Manager] Couldn't find message notification for SIP URI "
                                        + msg.getChatRoom()
                                                .getPeerAddress()
                                                .asStringUriOnly());
                        dismissNotification(notifId);
                        return;
                    } else if (notif.getNotificationId() != notifId) {
                        Log.w(
                                "[Notifications Manager] Notif ID doesn't match: "
                                        + notifId
                                        + " != "
                                        + notif.getNotificationId());
                    }

                    displayReplyMessageNotification(msg, notif);
                } else if (state == ChatMessage.State.NotDelivered) {
                    Log.e(
                            "[Notifications Manager] Couldn't send reply, message is not delivered");
                    dismissNotification(notifId);
                }
            }

            @Override
            public void onEphemeralMessageDeleted(ChatMessage message) {
            }

            @Override
            public void onEphemeralMessageTimerStarted(ChatMessage message) {
            }

            @Override
            public void onParticipantImdnStateChanged(ChatMessage message,
                    org.linphone.core.ParticipantImdnState state) {
            }

            @Override
            public void onFileTransferRecv(ChatMessage message, Content content, org.linphone.core.Buffer buffer) {
            }

            @Override
            public org.linphone.core.Buffer onFileTransferSend(ChatMessage message, Content content, int offset,
                    int size) {
                return null;
            }

            @Override
            public void onFileTransferProgressIndication(ChatMessage message, Content content, int offset, int total) {
            }

            @Override
            public void onFileTransferSendChunk(ChatMessage message, Content content, int offset, int size,
                    org.linphone.core.Buffer buffer) {
            }

            @Override
            public void onFileTransferTerminated(ChatMessage message, Content content) {
                // Stub
            }

            @Override
            public void onReactionRemoved(ChatMessage message, Address reaction) {
                // Stub
            }

            @Override
            public void onNewMessageReaction(ChatMessage message, ChatMessageReaction reaction) {
                // Stub
            }
        };
    }

    public void onCoreReady() {
        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.addListener(mListener);
        }
    }

    public void destroy() {
        // mNM.cancelAll();
        // Don't use cancelAll to keep message notifications !
        // When a message is received by a push, it will create a LinphoneService
        // but it might be getting killed quite quickly after that
        // causing the notification to be missed by the user...
        Log.i("[Notifications Manager] Getting destroyed, clearing Service & Call notifications");

        if (mCurrentForegroundServiceNotification > 0) {
            mNM.cancel(mCurrentForegroundServiceNotification);
        }

        for (Notifiable notifiable : mCallNotifMap.values()) {
            mNM.cancel(notifiable.getNotificationId());
        }

        Core core = LinphoneManager.getCore();
        if (core != null) {
            core.removeListener(mListener);
        }
    }

    private void addFlagsToIntent(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    public void startForeground() {
        if (LinphoneService.isReady()) {
            Log.i("[Notifications Manager] Starting Service as foreground");
            LinphoneService.instance().startForeground(SERVICE_NOTIF_ID, mServiceNotification);
            mCurrentForegroundServiceNotification = SERVICE_NOTIF_ID;
        }
    }

    private void startForeground(Notification notification, int id) {
        if (LinphoneService.isReady()) {
            Log.i("[Notifications Manager] Starting Service as foreground while in call");
            LinphoneService.instance().startForeground(id, notification);
            mCurrentForegroundServiceNotification = id;
        }
    }

    public void stopForeground() {
        if (LinphoneService.isReady()) {
            Log.i("[Notifications Manager] Stopping Service as foreground");
            LinphoneService.instance().stopForeground(true);
            mCurrentForegroundServiceNotification = 0;
        }
    }

    public void removeForegroundServiceNotificationIfPossible() {
        if (LinphoneService.isReady()) {
            if (mCurrentForegroundServiceNotification == SERVICE_NOTIF_ID
                    && !isServiceNotificationDisplayed()) {
                Log.i(
                        "[Notifications Manager] Linphone has started after device boot, stopping Service as foreground");
                stopForeground();
            }
        }
    }

    public void setCurrentlyDisplayedChatRoom(String address) {
        mCurrentChatRoomAddress = address;
        if (address != null) {
            resetMessageNotifCount(address);
        }
    }

    public void sendNotification(int id, Notification notif) {
        Log.i("[Notifications Manager] Notifying " + id);
        mNM.notify(id, notif);
    }

    public void dismissNotification(int notifId) {
        Log.i("[Notifications Manager] Dismissing " + notifId);
        mNM.cancel(notifId);
    }

    public void resetMessageNotifCount(String address) {
        Notifiable notif = mChatNotifMap.get(address);
        if (notif != null) {
            notif.resetMessages();
            mNM.cancel(notif.getNotificationId());
        }
    }

    public ChatMessageListener getMessageListener() {
        return mMessageListener;
    }

    private boolean isServiceNotificationDisplayed() {
        return LinphonePreferences.instance().getServiceNotificationVisibility();
    }

    public String getSipUriForNotificationId(int notificationId) {
        for (String addr : mChatNotifMap.keySet()) {
            if (mChatNotifMap.get(addr).getNotificationId() == notificationId) {
                return addr;
            }
        }
        return null;
    }

    private void displayMessageNotificationFromNotifiable(Notifiable notif, String remoteSipUri, String localSipUri) {
        Intent notifIntent = new Intent(mContext, ChatActivity.class);
        notifIntent.putExtra("RemoteSipUri", remoteSipUri);
        notifIntent.putExtra("LocalSipUri", localSipUri);
        addFlagsToIntent(notifIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                notif.getNotificationId(),
                notifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotifiableMessage lastNotifiable = notif.getMessages().get(notif.getMessages().size() - 1);
        String from = lastNotifiable.getSender();
        String message = lastNotifiable.getMessage();
        Bitmap bm = lastNotifiable.getSenderBitmap();
        if (notif.isGroup()) {
            message = mContext.getString(R.string.group_chat_notif)
                    .replace("%1", from)
                    .replace("%2", message);
            from = notif.getGroupTitle();
        }

        Notification notification = Compatibility.createMessageNotification(
                mContext, notif, from, message, bm, pendingIntent);
        sendNotification(notif.getNotificationId(), notification);
    }

    private void displayReplyMessageNotification(ChatMessage msg, Notifiable notif) {
        if (msg == null || notif == null)
            return;
        Log.i(
                "[Notifications Manager] Updating message notification with reply for notif "
                        + notif.getNotificationId());

        NotifiableMessage notifMessage = new NotifiableMessage(
                msg.getTextContent(),
                notif.getMyself(),
                System.currentTimeMillis(),
                null,
                null);
        notif.addMessage(notifMessage);

        ChatRoom cr = msg.getChatRoom();

        displayMessageNotificationFromNotifiable(
                notif,
                cr.getPeerAddress().asStringUriOnly(),
                cr.getLocalAddress().asStringUriOnly());
    }

    public void displayGroupChatMessageNotification(
            String subject,
            String conferenceAddress,
            String fromName,
            Uri fromPictureUri,
            String message,
            Address localIdentity,
            long timestamp,
            Uri filePath,
            String fileMime) {

        Bitmap bm = ImageUtils.getRoundBitmapFromUri(mContext, fromPictureUri);
        Notifiable notif = mChatNotifMap.get(conferenceAddress);
        NotifiableMessage notifMessage = new NotifiableMessage(message, fromName, timestamp, filePath, fileMime);
        if (notif == null) {
            notif = new Notifiable(mLastNotificationId);
            mLastNotificationId += 1;
            mChatNotifMap.put(conferenceAddress, notif);
        }
        Log.i("[Notifications Manager] Creating group chat message notifiable " + notif);

        notifMessage.setSenderBitmap(bm);
        notif.addMessage(notifMessage);
        notif.setIsGroup(true);
        notif.setGroupTitle(subject);
        notif.setMyself(LinphoneUtils.getAddressDisplayName(localIdentity));
        notif.setLocalIdentity(localIdentity.asString());

        displayMessageNotificationFromNotifiable(
                notif, conferenceAddress, localIdentity.asStringUriOnly());
    }

    public void displayMessageNotification(
            String fromSipUri,
            String fromName,
            Uri fromPictureUri,
            String message,
            Address localIdentity,
            long timestamp,
            Uri filePath,
            String fileMime) {
        if (fromName == null) {
            fromName = fromSipUri;
        }

        Bitmap bm = ImageUtils.getRoundBitmapFromUri(mContext, fromPictureUri);
        Notifiable notif = mChatNotifMap.get(fromSipUri);
        NotifiableMessage notifMessage = new NotifiableMessage(message, fromName, timestamp, filePath, fileMime);
        if (notif == null) {
            notif = new Notifiable(mLastNotificationId);
            mLastNotificationId += 1;
            mChatNotifMap.put(fromSipUri, notif);
        }
        Log.i("[Notifications Manager] Creating chat message notifiable " + notif);

        notifMessage.setSenderBitmap(bm);
        notif.addMessage(notifMessage);
        notif.setIsGroup(false);
        notif.setMyself(LinphoneUtils.getAddressDisplayName(localIdentity));
        notif.setLocalIdentity(localIdentity.asString());

        displayMessageNotificationFromNotifiable(
                notif, fromSipUri, localIdentity.asStringUriOnly());
    }

    public void displayMissedCallNotification(Call call) {
        Intent missedCallNotifIntent = new Intent(mContext, HomeActivity.class);
        addFlagsToIntent(missedCallNotifIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                MISSED_CALLS_NOTIF_ID,
                missedCallNotifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int missedCallCount = LinphoneManager.getCore().getMissedCallsCount();
        String body;
        if (missedCallCount > 1) {
            body = mContext.getString(R.string.missed_calls_notif_body)
                    .replace("%i", String.valueOf(missedCallCount));
            Log.i("[Notifications Manager] Creating missed calls notification");
        } else {
            Address address = call.getRemoteAddress();
            LinphoneContact c = ContactsManager.getInstance().findContactFromAddress(address);
            if (c != null) {
                body = c.getFullName();
            } else {
                body = address.getDisplayName();
                if (body == null) {
                    body = address.asStringUriOnly();
                }
            }
            Log.i("[Notifications Manager] Creating missed call notification");
        }

        Notification notif = Compatibility.createMissedCallNotification(
                mContext,
                mContext.getString(R.string.missed_calls_notif_title),
                body,
                pendingIntent);
        sendNotification(MISSED_CALLS_NOTIF_ID, notif);

        /*
         * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         * 
         * android.app.NotificationChannel channel =
         * new android.app.NotificationChannel(
         * "243", "BelldialMissedCall", NotificationManager.IMPORTANCE_HIGH);
         * channel.setDescription("Belldial misssed call");
         * 
         * NotificationCompat.Builder builder =
         * new NotificationCompat.Builder(mContext, channel.getId())
         * .setSmallIcon(R.drawable.call_missed)
         * .setContentTitle("My notification")
         * .setContentText("Hello World!")
         * .setPriority(NotificationCompat.PRIORITY_MAX)
         * .setContentIntent(pendingIntent)
         * .setAutoCancel(true);
         * 
         * NotificationManager notificationManager =
         * mContext.getSystemService(NotificationManager.class);
         * notificationManager.createNotificationChannel(channel);
         * notificationManager.notify(123, builder.build());
         * }
         */
    }

    public void displayCallNotification(Call call) {
        if (call == null)
            return;

        Intent callNotifIntent = new Intent(mContext, CallOutgoingActivity.class);
        Class callNotifIntentClass = CallOutgoingActivity.class;
        if (call.getState() == Call.State.IncomingReceived || call.getState() == Call.State.IncomingEarlyMedia) {
            callNotifIntent = new Intent(mContext, CallIncomingActivity.class);
            callNotifIntent.putExtra("from", "Notification");
            callNotifIntentClass = CallIncomingActivity.class;
        } else if (call.getState() == Call.State.OutgoingInit || call.getState() == Call.State.OutgoingProgress
                || call.getState() == Call.State.OutgoingRinging || call.getState() == Call.State.OutgoingEarlyMedia) {
            callNotifIntent = new Intent(mContext, CallOutgoingActivity.class);
            callNotifIntentClass = CallOutgoingActivity.class;
        }

        callNotifIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, callNotifIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Address address = call.getRemoteAddress();
        String addressAsString = address.getDisplayName();
        Notifiable notif = mCallNotifMap.get(addressAsString);
        if (notif == null) {
            notif = new Notifiable(mLastNotificationId);
            mLastNotificationId += 1;
            mCallNotifMap.put(addressAsString, notif);
        }

        int notificationTextId;
        int iconId;
        switch (call.getState()) {
            case Released:
            case End:
                if (mCurrentForegroundServiceNotification == notif.getNotificationId()) {
                    Log.i(
                            "[Notifications Manager] Call ended, stopping notification used to keep service alive");
                    // Call is released, remove service notification to allow for an other call to
                    // be service notification
                    stopForeground();
                }
                mNM.cancel(notif.getNotificationId());
                mCallNotifMap.remove(addressAsString);
                return;
            case Paused:
            case PausedByRemote:
            case Pausing:
                iconId = R.drawable.topbar_call_notification;
                notificationTextId = R.string.incall_notif_paused;
                break;
            case IncomingEarlyMedia:
            case IncomingReceived:
                iconId = R.drawable.topbar_call_notification;
                notificationTextId = R.string.incall_notif_incoming;

                break;
            case OutgoingEarlyMedia:
            case OutgoingInit:
            case OutgoingProgress:
            case OutgoingRinging:
                iconId = R.drawable.topbar_call_notification;
                notificationTextId = R.string.incall_notif_outgoing;
                break;
            default:
                if (call.getCurrentParams().isVideoEnabled()) {
                    iconId = R.drawable.topbar_videocall_notification;
                    notificationTextId = R.string.incall_notif_video;
                } else {
                    iconId = R.drawable.topbar_call_notification;
                    notificationTextId = R.string.incall_notif_active;
                }
                break;
        }

        if (notif.getIconResourceId() == iconId
                && notif.getTextResourceId() == notificationTextId) {
            // Notification hasn't changed, do not "update" it to avoid blinking
            return;
        } else if (notif.getTextResourceId() == R.string.incall_notif_incoming) {
            // If previous notif was incoming call, as we will switch channels, dismiss it
            // first
            dismissNotification(notif.getNotificationId());
        }

        notif.setIconResourceId(iconId);
        notif.setTextResourceId(notificationTextId);
        Log.i(
                "[Notifications Manager] Call notification notifiable is "
                        + notif
                        + ", pending intent "
                        + callNotifIntentClass);

        LinphoneContact contact = ContactsManager.getInstance().findContactFromAddress(address);
        Uri pictureUri = contact != null ? contact.getThumbnailUri() : null;
        Bitmap bm = ImageUtils.getRoundBitmapFromUri(mContext, pictureUri);
        String name = contact != null
                ? contact.getFullName()
                : LinphoneUtils.getAddressDisplayName(address);
        boolean isIncoming = callNotifIntentClass == CallIncomingActivity.class;

        Notification notification;
        if (isIncoming) {
            notification = Compatibility.createIncomingCallNotification(mContext, notif.getNotificationId(), bm, name,
                    addressAsString, pendingIntent);
        } else {
            notification = Compatibility.createInCallNotification(
                    mContext,
                    notif.getNotificationId(),
                    mContext.getString(notificationTextId),
                    iconId,
                    bm,
                    name,
                    pendingIntent);
        }

        // Don't use incoming call notification as foreground service notif !
        if (!isServiceNotificationDisplayed() && !isIncoming) {
            if (call.getCore().getCallsNb() == 0) {
                Log.i(
                        "[Notifications Manager] Foreground service mode is disabled, stopping call notification used to keep it alive");
                stopForeground();
            } else {
                if (mCurrentForegroundServiceNotification == 0) {
                    if (DeviceUtils.isAppUserRestricted(mContext)) {
                        Log.w(
                                "[Notifications Manager] App has been restricted, can't use call notification to keep service alive !");
                        sendNotification(notif.getNotificationId(), notification);
                    } else {
                        Log.i(
                                "[Notifications Manager] Foreground service mode is disabled, using call notification to keep it alive");
                        startForeground(notification, notif.getNotificationId());
                    }
                } else {
                    sendNotification(notif.getNotificationId(), notification);
                }
            }
        } else {
            sendNotification(notif.getNotificationId(), notification);
        }
    }

    public String getSipUriForCallNotificationId(int notificationId) {
        for (String addr : mCallNotifMap.keySet()) {
            if (mCallNotifMap.get(addr).getNotificationId() == notificationId) {
                return addr;
            }
        }
        return null;
    }

    private void createNotification(
            ChatRoom cr,
            LinphoneContact contact,
            Address from,
            String textMessage,
            long time,
            Uri file,
            String mime) {
        if (cr.hasCapability(ChatRoom.Capabilities.OneToOne.toInt())) {
            if (contact != null) {
                displayMessageNotification(
                        cr.getPeerAddress().asStringUriOnly(),
                        contact.getFullName(),
                        contact.getThumbnailUri(),
                        textMessage,
                        cr.getLocalAddress(),
                        time,
                        file,
                        mime);
            } else {
                displayMessageNotification(
                        cr.getPeerAddress().asStringUriOnly(),
                        from.getUsername(),
                        null,
                        textMessage,
                        cr.getLocalAddress(),
                        time,
                        file,
                        mime);
            }
        } else {
            String subject = cr.getSubject();
            if (contact != null) {
                displayGroupChatMessageNotification(
                        subject,
                        cr.getPeerAddress().asStringUriOnly(),
                        contact.getFullName(),
                        contact.getThumbnailUri(),
                        textMessage,
                        cr.getLocalAddress(),
                        time,
                        file,
                        mime);
            } else {
                displayGroupChatMessageNotification(
                        subject,
                        cr.getPeerAddress().asStringUriOnly(),
                        from.getUsername(),
                        null,
                        textMessage,
                        cr.getLocalAddress(),
                        time,
                        file,
                        mime);
            }
        }
    }
}
