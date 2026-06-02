package belldial.co.uk.firebase;

/*
FirebaseMessaging.java
Copyright (C) 2017-2019 Belledonne Communications, Grenoble, France

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

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.compatibility.Compatibility;
import org.linphone.core.Core;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.LinphoneUtils;

public class FirebaseMessaging extends FirebaseMessagingService {
    private final Runnable mPushReceivedRunnable =
            () -> {
                if (!LinphoneService.isReady()) {
                    android.util.Log.i(
                            "FirebaseMessaging", "[Push Notification] Starting context");
                    /*new LinphoneContext(getApplicationContext());
                    LinphoneContext.instance().start(true);*/
                    Intent serviceIntent = new Intent(Intent.ACTION_MAIN);
                    serviceIntent.setClass(getApplicationContext(), LinphoneService.class);
                    serviceIntent.putExtra("ForceStartForeground", true);
                    Compatibility.startService(getApplicationContext(), serviceIntent);
                } else {
                    Log.i("[Push Notification] Notifying Core");
                    if (LinphoneManager.getInstance() != null) {
                        Core core = LinphoneManager.getCore();
                        if (core != null) {
                            core.ensureRegistered();
                            core.refreshRegisters();
                        }
                    }
                }
            };

    public FirebaseMessaging() {}

    @Override
    public void onNewToken(@NonNull final String token) {
        android.util.Log.i("FirebaseIdService", "[Push Notification] Refreshed token: " + token);

        LinphoneUtils.dispatchOnUIThread(() -> LinphonePreferences.instance().setPushNotificationRegistrationID(token));
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        android.util.Log.i("FirebaseMessaging", "[Push Notification] Received");
        LinphoneUtils.dispatchOnUIThread(mPushReceivedRunnable);
    }
}
