package belldial.co.uk.utils;

import org.linphone.core.Account;
import org.linphone.core.Address;
import org.linphone.core.Alert;
import org.linphone.core.AuthInfo;
import org.linphone.core.AuthMethod;
import org.linphone.core.AudioDevice;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageReaction;
import org.linphone.core.ChatRoom;
import org.linphone.core.Conference;
import org.linphone.core.ConferenceInfo;
import org.linphone.core.Core;
import org.linphone.core.Headers;
import org.linphone.core.CoreListener;
import org.linphone.core.ConfiguringState;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Event;
import org.linphone.core.MessageWaitingIndication;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.core.Content;
import org.linphone.core.FriendList;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;
import org.linphone.core.RegistrationState;

import org.linphone.core.CallStats;
import org.linphone.core.Friend;
import org.linphone.core.InfoMessage;
import org.linphone.core.PresenceModel;
import org.linphone.core.VersionUpdateCheckResult;
import org.linphone.core.GlobalState;

public class SimpleCoreListener implements CoreListener {
    @Override
    public void onNewAlertTriggered(Core lc, Alert alert) {
        // Stub
    }

    @Override
    public void onGlobalStateChanged(Core lc, GlobalState state, String message) {
        // Stub
    }

    @Override
    public void onCallStateChanged(Core lc, Call call, Call.State state, String message) {
        // Stub
    }

    @Override
    public void onNotifyReceived(Core lc, Event event, String eventName, Content content) {
        // Stub
    }

    @Override
    public void onInfoReceived(Core lc, Call call, InfoMessage message) {
        // Stub
    }

    @Override
    public void onCallStatsUpdated(Core lc, Call call, CallStats stats) {
        // Stub
    }

    @Override
    public void onBuddyInfoUpdated(Core lc, Friend friend) {
        // Stub
    }

    @Override
    public void onTransferStateChanged(Core lc, Call transfered, Call.State newCallState) {
        // Stub
    }

    @Override
    public void onCallReceiveMasterKeyChanged(Core lc, Call call, String masterKey) {
        // Stub
    }

    @Override
    public void onCallSendMasterKeyChanged(Core lc, Call call, String masterKey) {
        // Stub
    }

    @Override
    public void onCallEncryptionChanged(Core lc, Call call, boolean on, String authenticationToken) {
        // Stub
    }

    @Override
    public void onReferReceived(Core lc, Address referTo, Headers headers, Content body) {
        // Stub
    }

    @Override
    public void onRegistrationStateChanged(Core lc, org.linphone.core.ProxyConfig cfg, RegistrationState cstate,
            String message) {
        // Stub
    }

    @Override
    public void onConferenceInfoReceived(Core lc, ConferenceInfo conferenceInfo) {
        // Stub
    }

    @Override
    public void onPushNotificationReceived(Core lc, String payload) {
        // Stub
    }

    @Override
    public void onPreviewDisplayErrorOccurred(Core lc, int error_code) {
        // Stub
    }

    @Override
    public void onNotifyPresenceReceived(Core lc, Friend friend) {
        // Stub
    }

    @Override
    public void onNotifyPresenceReceivedForUriOrTel(Core lc, Friend friend, String uri, PresenceModel presence) {
        // Stub
    }

    @Override
    public void onNewSubscriptionRequested(Core lc, Friend friend, String url) {
        // Stub
    }

    @Override
    public void onAuthenticationRequested(Core lc, AuthInfo authInfo, AuthMethod method) {
        // Stub
    }

    @Override
    public void onCallIdUpdated(Core lc, String previousCallId, String currentCallId) {
        // Stub
    }

    @Override
    public void onRemainingNumberOfFileTransferChanged(Core lc, int current, int total) {
        // Stub
    }

    @Override
    public void onMessageReceived(Core lc, ChatRoom chatRoom, ChatMessage message) {
        // Stub
    }

    @Override
    public void onNewMessageReaction(Core lc, ChatRoom chatRoom, ChatMessage message, ChatMessageReaction reaction) {
        // Stub
    }

    @Override
    public void onReactionRemoved(Core lc, ChatRoom chatRoom, ChatMessage message, Address reaction) {
        // Stub
    }

    @Override
    public void onMessagesReceived(Core lc, ChatRoom chatRoom, ChatMessage[] messages) {
        // Stub
    }

    @Override
    public void onMessageSent(Core lc, ChatRoom chatRoom, ChatMessage message) {
        // Stub
    }

    @Override
    public void onChatRoomSessionStateChanged(Core lc, ChatRoom chatRoom, Call.State state, String message) {
        // Stub
    }

    @Override
    public void onChatRoomRead(Core lc, ChatRoom chatRoom) {
        // Stub
    }

    @Override
    public void onMessageReceivedUnableDecrypt(Core lc, ChatRoom chatRoom, ChatMessage message) {
        // Stub
    }

    @Override
    public void onIsComposingReceived(Core lc, ChatRoom chatRoom) {
        // Stub
    }

    @Override
    public void onDtmfReceived(Core lc, Call call, int dtmf) {
        // Stub
    }

    @Override
    public void onCallGoclearAckSent(Core lc, Call call) {
        // Stub
    }

    @Override
    public void onNotifySent(Core lc, Event event, Content content) {
        // Stub
    }

    @Override
    public void onSnapshotTaken(Core lc, String filePath) {
        // Stub
    }

    @Override
    public void onMessageWaitingIndicationChanged(Core lc, Event event, MessageWaitingIndication mwi) {
        // Stub
    }

    @Override
    public void onAccountRemoved(Core lc, Account account) {
        // Stub
    }

    @Override
    public void onAccountAdded(Core lc, Account account) {
        // Stub
    }

    @Override
    public void onDefaultAccountChanged(Core lc, Account account) {
        // Stub
    }

    @Override
    public void onAccountRegistrationStateChanged(Core lc, Account account, RegistrationState state, String message) {
        // Stub
    }

    @Override
    public void onEcCalibrationAudioUninit(Core lc) {
        // Stub
    }

    @Override
    public void onEcCalibrationAudioInit(Core lc) {
        // Stub
    }

    @Override
    public void onEcCalibrationResult(Core lc, EcCalibratorStatus status, int delayMs) {
        // Stub
    }

    @Override
    public void onAudioDevicesListUpdated(Core lc) {
        // Stub
    }

    @Override
    public void onAudioDeviceChanged(Core lc, AudioDevice audioDevice) {
        // Stub
    }

    @Override
    public void onLastCallEnded(Core lc) {
        // Stub
    }

    @Override
    public void onFirstCallStarted(Core lc) {
        // Stub
    }

    @Override
    public void onQrcodeFound(Core lc, String result) {
        // Stub
    }

    @Override
    public void onImeeUserRegistration(Core lc, boolean status, String userId, String info) {
        // Stub
    }

    @Override
    public void onChatRoomEphemeralMessageDeleted(Core lc, ChatRoom chatRoom) {
        // Stub
    }

    @Override
    public void onChatRoomSubjectChanged(Core lc, ChatRoom chatRoom) {
        // Stub
    }

    @Override
    public void onChatRoomStateChanged(Core lc, ChatRoom chatRoom, ChatRoom.State state) {
        // Stub
    }

    @Override
    public void onConferenceStateChanged(Core lc, Conference conference, Conference.State state) {
        // Stub
    }

    @Override
    public void onVersionUpdateCheckResultReceived(Core lc, VersionUpdateCheckResult result, String version,
            String url) {
        // Stub
    }

    @Override
    public void onCallCreated(Core lc, Call call) {
        // Stub
    }

    @Override
    public void onCallLogUpdated(Core lc, CallLog callLog) {
        // Stub
    }

    @Override
    public void onFriendListCreated(Core lc, FriendList list) {
        // Stub
    }

    @Override
    public void onFriendListRemoved(Core lc, FriendList list) {
        // Stub
    }

    @Override
    public void onLogCollectionUploadProgressIndication(Core lc, int offset, int total) {
        // Stub
    }

    @Override
    public void onLogCollectionUploadStateChanged(Core lc, Core.LogCollectionUploadState state, String info) {
        // Stub
    }

    @Override
    public void onNetworkReachable(Core lc, boolean reachable) {
        // Stub
    }

    @Override
    public void onConfiguringStatus(Core lc, ConfiguringState state, String message) {
        // Stub
    }

    @Override
    public void onPublishReceived(Core lc, Event event, String eventName, Content content) {
        // Stub
    }

    @Override
    public void onPublishStateChanged(Core lc, Event event, PublishState state) {
        // Stub
    }

    @Override
    public void onSubscribeReceived(Core lc, Event event, String eventName, Content content) {
        // Stub
    }

    @Override
    public void onSubscriptionStateChanged(Core lc, Event event, SubscriptionState state) {
        // Stub
    }
}
