package belldial.co.uk.utils;

import androidx.annotation.NonNull;

import org.linphone.core.Address;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageReaction;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomListener;
import org.linphone.core.EventLog;
import org.linphone.core.ParticipantDevice;
import org.linphone.core.ParticipantImdnState;

public class SimpleChatRoomListener implements ChatRoomListener {
    @Override
    public void onChatMessageSent(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onConferenceAddressGeneration(ChatRoom cr) {
    }

    @Override
    public void onParticipantRegistrationSubscriptionRequested(ChatRoom cr, Address participantAddr) {
    }

    @Override
    public void onParticipantRegistrationUnsubscriptionRequested(ChatRoom cr, Address participantAddr) {
    }

    @Override
    public void onUndecryptableMessageReceived(ChatRoom cr, ChatMessage msg) {
    }

    @Override
    public void onChatMessageReceived(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onChatMessagesReceived(@NonNull ChatRoom chatRoom, @NonNull EventLog[] eventLogs) {
    }

    @Override
    public void onChatMessageSending(@NonNull ChatRoom chatRoom, @NonNull EventLog eventLog) {
    }

    @Override
    public void onIsComposingReceived(ChatRoom cr, Address remoteAddr, boolean isComposing) {
    }

    @Override
    public void onMessageReceived(ChatRoom cr, ChatMessage msg) {
    }

    @Override
    public void onMessagesReceived(@NonNull ChatRoom chatRoom, @NonNull ChatMessage[] chatMessages) {
    }

    @Override
    public void onNewEvent(@NonNull ChatRoom chatRoom, @NonNull EventLog eventLog) {
    }

    @Override
    public void onNewEvents(@NonNull ChatRoom chatRoom, @NonNull EventLog[] eventLogs) {
    }

    @Override
    public void onEphemeralMessageDeleted(ChatRoom cr, EventLog eventLog) {
    }

    @Override
    public void onConferenceJoined(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onConferenceLeft(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onParticipantAdminStatusChanged(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onParticipantDeviceRemoved(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onParticipantDeviceStateChanged(@NonNull ChatRoom chatRoom, @NonNull EventLog eventLog,
            ParticipantDevice.State state) {
    }

    @Override
    public void onParticipantDeviceMediaAvailabilityChanged(@NonNull ChatRoom chatRoom, @NonNull EventLog eventLog) {
    }

    @Override
    public void onParticipantRemoved(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onEphemeralMessageTimerStarted(ChatRoom cr, EventLog eventLog) {
    }

    @Override
    public void onChatMessageShouldBeStored(ChatRoom cr, ChatMessage msg) {
    }

    @Override
    public void onChatMessageParticipantImdnStateChanged(@NonNull ChatRoom chatRoom, @NonNull ChatMessage message,
            @NonNull ParticipantImdnState state) {
    }

    @Override
    public void onChatRoomRead(@NonNull ChatRoom chatRoom) {
    }

    @Override
    public void onNewMessageReaction(@NonNull ChatRoom chatRoom, @NonNull ChatMessage message,
            @NonNull ChatMessageReaction reaction) {
    }

    @Override
    public void onParticipantDeviceAdded(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onSecurityEvent(ChatRoom cr, EventLog eventLog) {
    }

    @Override
    public void onStateChanged(ChatRoom cr, ChatRoom.State newState) {
    }

    @Override
    public void onParticipantAdded(ChatRoom cr, EventLog event) {
    }

    @Override
    public void onEphemeralEvent(ChatRoom cr, EventLog eventLog) {
    }

    @Override
    public void onSubjectChanged(ChatRoom cr, EventLog event) {
    }
}
