package belldial.co.uk.history;

/*
HistoryDetailFragment.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import belldial.co.uk.contacts.ContactsManager;
import belldial.co.uk.contacts.LinphoneContact;
import org.linphone.core.Address;
import org.linphone.core.CallLog;
import org.linphone.core.ChatRoom;
import org.linphone.core.Account;
import org.linphone.core.Core;
import org.linphone.core.EventLog;
import org.linphone.core.Factory;
import org.linphone.core.Friend;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageReaction;
import org.linphone.core.ChatRoomParams;
import org.linphone.core.ChatRoomListener;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.LinphoneUtils;
import belldial.co.uk.views.ContactAvatar;

public class HistoryDetailFragment extends Fragment {
    private ImageView mAddToContacts;
    private ImageView mGoToContact;
    private TextView mContactName, mContactAddress;
    private String mSipUri, mDisplayName;
    private RelativeLayout mWaitLayout, mAvatarLayout, mChatSecured;
    private LinphoneContact mContact;
    private ChatRoom mChatRoom;
    private ChatRoomListener mChatRoomCreationListener;
    private ListView mLogsList;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSipUri = getArguments().getString("SipUri");
        mDisplayName = getArguments().getString("DisplayName");

        View view = inflater.inflate(R.layout.history_detail, container, false);

        mWaitLayout = view.findViewById(R.id.waitScreen);
        mWaitLayout.setVisibility(View.GONE);

        ImageView dialBack = view.findViewById(R.id.call);
        dialBack.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinphoneManager.getCallManager().newOutgoingCall(mSipUri, mDisplayName);
                    }
                });

        ImageView back = view.findViewById(R.id.back);
        back.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HistoryActivity) getActivity()).goBack();
                    }
                });
        back.setVisibility(
                getResources().getBoolean(R.bool.isTablet) ? View.INVISIBLE : View.VISIBLE);

        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToChat(false);
                    }
                });

        mChatSecured = view.findViewById(R.id.chat_secured);
        mChatSecured.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToChat(true);
                    }
                });

        if (getResources().getBoolean(R.bool.force_end_to_end_encryption_in_chat)) {
            chat.setVisibility(View.GONE);
        }
        if (getResources().getBoolean(R.bool.disable_chat)) {
            chat.setVisibility(View.GONE);
            mChatSecured.setVisibility(View.GONE);
        }

        mAddToContacts = view.findViewById(R.id.add_contact);
        mAddToContacts.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Address addr = Factory.instance().createAddress(mSipUri);
                        if (addr != null) {
                            addr.clean();
                            ((HistoryActivity) getActivity())
                                    .showContactsListForCreationOrEdition(addr);
                        }
                    }
                });

        mGoToContact = view.findViewById(R.id.goto_contact);
        mGoToContact.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((HistoryActivity) getActivity()).showContactDetails(mContact);
                    }
                });

        mAvatarLayout = view.findViewById(R.id.avatar_layout);
        mContactName = view.findViewById(R.id.contact_name);
        mContactAddress = view.findViewById(R.id.contact_address);

        mChatRoomCreationListener = new ChatRoomListener() {
            @Override
            public void onUndecryptableMessageReceived(ChatRoom chatRoom, ChatMessage message) {
                // Stub
            }

            @Override
            public void onParticipantRemoved(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onParticipantAdded(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onChatMessageSent(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onChatMessageSending(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onChatMessagesReceived(ChatRoom chatRoom, EventLog[] eventLogs) {
                // Stub
            }

            @Override
            public void onChatMessageReceived(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onNewEvents(ChatRoom chatRoom, EventLog[] eventLogs) {
                // Stub
            }

            @Override
            public void onNewEvent(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onMessagesReceived(ChatRoom chatRoom, ChatMessage[] messages) {
                // Stub
            }

            @Override
            public void onIsComposingReceived(ChatRoom chatRoom, Address address, boolean isComposing) {
                // Stub
            }

            @Override
            public void onNewMessageReaction(
                    ChatRoom chatRoom, ChatMessage message, ChatMessageReaction reaction) {
                // Stub
            }

            @Override
            public void onChatRoomRead(ChatRoom chatRoom) {
                // Stub
            }

            @Override
            public void onParticipantAdminStatusChanged(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onChatMessageParticipantImdnStateChanged(ChatRoom chatRoom, ChatMessage message,
                    org.linphone.core.ParticipantImdnState state) {
                // Stub
            }

            @Override
            public void onChatMessageShouldBeStored(ChatRoom chatRoom, ChatMessage message) {
                // Stub
            }

            @Override
            public void onParticipantRegistrationUnsubscriptionRequested(ChatRoom chatRoom, Address participant) {
                // Stub
            }

            @Override
            public void onParticipantRegistrationSubscriptionRequested(ChatRoom chatRoom, Address participant) {
                // Stub
            }

            @Override
            public void onConferenceAddressGeneration(ChatRoom chatRoom) {
                // Stub
            }

            @Override
            public void onEphemeralMessageDeleted(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onEphemeralMessageTimerStarted(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onEphemeralEvent(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onConferenceLeft(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onConferenceJoined(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onParticipantDeviceMediaAvailabilityChanged(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onParticipantDeviceStateChanged(ChatRoom chatRoom, EventLog eventLog,
                    org.linphone.core.ParticipantDevice.State state) {
                // Stub
            }

            @Override
            public void onParticipantDeviceRemoved(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onParticipantDeviceAdded(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onSubjectChanged(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onSecurityEvent(ChatRoom chatRoom, EventLog eventLog) {
                // Stub
            }

            @Override
            public void onMessageReceived(ChatRoom chatRoom, ChatMessage message) {
                // Stub
            }

            @Override
            public void onStateChanged(ChatRoom cr, ChatRoom.State newState) {
                if (newState == ChatRoom.State.Created) {
                    mWaitLayout.setVisibility(View.GONE);
                    ((HistoryActivity) getActivity())
                            .showChatRoom(cr.getLocalAddress(), cr.getPeerAddress());
                } else if (newState == ChatRoom.State.CreationFailed) {
                    mWaitLayout.setVisibility(View.GONE);
                    ((HistoryActivity) getActivity()).displayChatRoomError();
                    Log.e(
                            "Group mChat room for address "
                                    + cr.getPeerAddress()
                                    + " has failed !");
                }
            }
        };

        mLogsList = view.findViewById(R.id.logs_list);
        displayHistory();

        return view;
    }

    @Override
    public void onPause() {
        if (mChatRoom != null) {
            mChatRoom.removeListener(mChatRoomCreationListener);
        }
        super.onPause();
    }

    private void displayHistory() {
        if (mSipUri != null) {
            Address address = Factory.instance().createAddress(mSipUri);
            mChatSecured.setVisibility(View.GONE);

            Core core = LinphoneManager.getCore();
            if (address != null && core != null) {
                address.clean();
                Account account = core.getDefaultAccount();
                CallLog[] logs;
                if (account != null) {
                    logs = core.getCallHistory(address, account.getParams().getIdentityAddress());
                } else {
                    logs = core.getCallHistory(address, null);
                }
                List<CallLog> logsList = Arrays.asList(logs);
                mLogsList.setAdapter(
                        new HistoryLogAdapter(
                                getActivity(), R.layout.history_detail_cell, logsList));

                mContactAddress.setText(LinphoneUtils.getDisplayableAddress(address));
                mContact = ContactsManager.getInstance().findContactFromAddress(address);

                if (mDisplayName == null) {
                    mDisplayName = LinphoneUtils.getAddressDisplayName(address);
                }

                if (mContact != null) {
                    mContactName.setText(mContact.getFullName());
                    ContactAvatar.displayAvatar(mContact, mAvatarLayout);
                    mAddToContacts.setVisibility(View.GONE);
                    mGoToContact.setVisibility(View.VISIBLE);

                    if (!getResources().getBoolean(R.bool.disable_chat)
                            && mContact.hasPresenceModelForUriOrTelCapability(
                                    address.asStringUriOnly(), Friend.Capability.LimeX3Dh)) {
                        mChatSecured.setVisibility(View.VISIBLE);
                    }
                } else {
                    mContactName.setText(mDisplayName);
                    ContactAvatar.displayAvatar(mDisplayName, mAvatarLayout);
                    mAddToContacts.setVisibility(View.VISIBLE);
                    mGoToContact.setVisibility(View.GONE);
                }
            } else {
                mContactAddress.setText(mSipUri);
                mContactName.setText(
                        mDisplayName == null
                                ? LinphoneUtils.getAddressDisplayName(mSipUri)
                                : mDisplayName);
            }
        }
    }

    private void goToChat(boolean isSecured) {
        Core core = LinphoneManager.getCore();
        Address participant = Factory.instance().createAddress(mSipUri);

        Account defaultAccount = core.getDefaultAccount();
        if (defaultAccount == null)
            return;

        ChatRoom room = core.findOneToOneChatRoom(
                defaultAccount.getContactAddress(), participant, isSecured);
        if (room != null) {
            ((HistoryActivity) getActivity())
                    .showChatRoom(room.getLocalAddress(), room.getPeerAddress());
        } else {
            Account lpc = core.getDefaultAccount();
            if (lpc != null
                    && lpc.getParams().getConferenceFactoryUri() != null
                    && (isSecured || !LinphonePreferences.instance().useBasicChatRoomFor1To1())) {
                mWaitLayout.setVisibility(View.VISIBLE);

                ChatRoomParams params = core.createDefaultChatRoomParams();
                params.setEncryptionEnabled(isSecured);
                params.setGroupEnabled(false);
                // We don't want a basic chat room
                params.setBackend(ChatRoom.Backend.FlexisipChat);

                Address[] participants = new Address[1];
                participants[0] = participant;

                mChatRoom = core.createChatRoom(
                        params, getString(R.string.dummy_group_chat_subject), participants);
                if (mChatRoom != null) {
                    mChatRoom.addListener(mChatRoomCreationListener);
                } else {
                    Log.w("[History Detail Fragment] createChatRoom returned null...");
                    mWaitLayout.setVisibility(View.GONE);
                }
            } else {
                room = core.getChatRoom(participant);
                if (room != null) {
                    ((HistoryActivity) getActivity())
                            .showChatRoom(room.getLocalAddress(), room.getPeerAddress());
                }
            }
        }
    }
}
