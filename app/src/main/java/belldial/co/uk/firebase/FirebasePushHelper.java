package belldial.co.uk.firebase;

/*
FirebasePushHelper.java
Copyright (C) 2019 Belledonne Communications, Grenoble, France

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

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.gson.JsonObject;

import java.util.HashMap;

import belldial.co.uk.R;
import belldial.co.uk.api.ApiService;
import belldial.co.uk.api.RetroClient;
import belldial.co.uk.api.model.AddTokenResponse;
import belldial.co.uk.utils.Constant;

import org.linphone.core.tools.Log;

import belldial.co.uk.settings.LinphonePreferences;
import belldial.co.uk.utils.PushNotificationUtils;
import retrofit2.Callback;
import retrofit2.Response;

@Keep
public class FirebasePushHelper implements PushNotificationUtils.PushHelperInterface {

    SharedPreferences sharedPreferences;
    String TAG = "FirebasePushHelper";

    public FirebasePushHelper() {
    }

    @Override
    public void init(Context context) {
        Log.i("[Push Notification] firebase push sender id " + context.getString(R.string.app_name));
        try {
            sharedPreferences = context.getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);

            FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {

                    if (!task.isSuccessful()) {
                        org.linphone.core.tools.Log.e("[Push Notification] firebase getInstanceId failed: " + task.getException());
                        android.util.Log.d(TAG, "Fetching FCM registration token failed: " + task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.i("[Push Notification] firebase token is: " + token);
                    Log.d(TAG, "[Push Notification] firebase token is: " + token);
                    LinphonePreferences.instance().setPushNotificationRegistrationID(token);
                    addpushtoken(sharedPreferences.getString(Constant.USERNAME, "") + "@" + sharedPreferences.getString(Constant.DOMAIN, ""), token);
                }
            });


//            FirebaseInstanceId.getInstance()
//                    .getInstanceId()
//                    .addOnCompleteListener(
//                            task -> {
//                                if (!task.isSuccessful()) {
//                                    Log.e(
//                                            "[Push Notification] firebase getInstanceId failed: "
//                                                    + task.getException());
//                                    return;
//                                }
//                                String token = task.getResult().getToken();
//                                Log.i("[Push Notification] firebase token is: " + token);
//                                LinphonePreferences.instance()
//                                        .setPushNotificationRegistrationID(token);
//                                addpushtoken(
//                                        sharedPreferences.getString(Constant.USERNAME, "")
//                                                + "@"
//                                                + sharedPreferences.getString(Constant.DOMAIN, ""),
//                                        token);
//                            });

        } catch (Exception e) {
            Log.e("[Push Notification] firebase not available.");
            android.util.Log.d(TAG, "init: Push Notification] firebase not available. "+ e.getMessage());
        }
    }

    public void addpushtoken(String Number, String token) {
        // view.showLoader();
        /*HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("username", Number);
        headers.put("pushtoken", token);
        headers.put("platform", "android");*/

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User", Number);
        headers.put("PnTok", token);
        headers.put("PnTtype", "android");

        ApiService api = RetroClient.getApiService();
        // retrofit2.Call<AddTokenResponse> call = api.addPushToken(headers);
        JsonObject data = new JsonObject();
        data.addProperty("User", Number);
        data.addProperty("PnTok", token);
        data.addProperty("PnTtype", "android");
        retrofit2.Call<AddTokenResponse> call = api.addBelldialToken(data);
        call.enqueue(
                new Callback<AddTokenResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<AddTokenResponse> call,
                            Response<AddTokenResponse> response) {
                        // view.hideLoader();
                        /*  if (response.body().getStatus().equalsIgnoreCase("Success")) {
                            view.setDetails(response.body().getMessage());
                        } else {
                            view.detailsNotFound();
                        }*/
                        android.util.Log.e("onSuccess", "onSuccess: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<AddTokenResponse> call, Throwable throwable) {
                        // view.hideLoader();
                        android.util.Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    @Override
    public boolean isAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }
}
