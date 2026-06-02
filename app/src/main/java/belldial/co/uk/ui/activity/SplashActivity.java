package belldial.co.uk.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import belldial.co.uk.AppController;
import belldial.co.uk.LinphoneService;
import belldial.co.uk.R;
import belldial.co.uk.contacts.ContactsActivity;
import belldial.co.uk.ui.dialog.PrivacyDialog;
import belldial.co.uk.utils.AppUtilities;
import belldial.co.uk.utils.Constant;
import belldial.co.uk.utils.LinphoneUtils;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private boolean isShowing = false;
    private boolean isConsentAccepted = false;

    RelativeLayout layoutConsentNotAccepted, layoutMain;
    AppCompatButton btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);

        layoutConsentNotAccepted = findViewById(R.id.layoutConsentNotAccepted);
        layoutMain = findViewById(R.id.layoutMain);
        btnAccept = findViewById(R.id.btnAccept);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        isConsentAccepted = AppUtilities.getBoolean(this, "isConsentAccepted");

        if (isConsentAccepted){
            layoutMain.setVisibility(View.VISIBLE);
            layoutConsentNotAccepted.setVisibility(View.GONE);
            requestBluePermission();
        }else {
            layoutMain.setVisibility(View.GONE);
            layoutConsentNotAccepted.setVisibility(View.VISIBLE);
            btnAccept.setOnClickListener(v -> {
                layoutMain.setVisibility(View.VISIBLE);
                layoutConsentNotAccepted.setVisibility(View.GONE);
                requestBluePermission();
                AppUtilities.saveBoolean(this, "isConsentAccepted", true);
            });
        }



    }


    private void requestBluePermission() {
        android.util.Log.d("khan", "onStart: 2");
        if (Build.VERSION.SDK_INT > 32) {
            Dexter.withContext(SplashActivity.this)
                    .withPermissions(Manifest.permission.READ_CONTACTS
                            , Manifest.permission.WRITE_CONTACTS
                            , Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE
                            , Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.POST_NOTIFICATIONS)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                            android.util.Log.d("khan", "onStart: 4 " + multiplePermissionsReport.areAllPermissionsGranted());

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                startServiceCommand();
                            } else {
                                Toast.makeText(SplashActivity.this, "Allow permission for app", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
        else if (Build.VERSION.SDK_INT > 30) {
            android.util.Log.d("khan", "onStart: 3");

            Dexter.withContext(SplashActivity.this)
                    .withPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH_CONNECT
                            , Manifest.permission.WRITE_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            android.util.Log.d("khan", "onStart: 4 " + multiplePermissionsReport.areAllPermissionsGranted());

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                startServiceCommand();
                            } else {
                                Toast.makeText(SplashActivity.this, "Allow permission for app eacturesf", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            android.util.Log.d("khan", "onStart: 5");
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
        else {

            Dexter.withContext(SplashActivity.this)
                    .withPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE
                            , Manifest.permission.READ_PHONE_STATE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            android.util.Log.d("khan", "onStart: 4 " + multiplePermissionsReport.areAllPermissionsGranted());

                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                startServiceCommand();
                            } else {
                                Toast.makeText(SplashActivity.this, "Allow permission for app eacturesf", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            android.util.Log.d("khan", "onStart: 5");
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }

    }

    private void startServiceCommand() {

        if (isShowing) return;
        if (PrivacyDialog.shouldShow(SplashActivity.this)) {
            PrivacyDialog privacyDialog = PrivacyDialog.newInstance();
            privacyDialog.addAcceptHandler(
                    () -> {
                        isShowing = false;
                        if (LinphoneService.isReady()) {
                            onServiceReady();
                        } else {
                            startService(new Intent().setClass(SplashActivity.this, LinphoneService.class));
                            new ServiceWaitThread().start();
                        }
                    });
            privacyDialog.setCancelable(false);
            privacyDialog.show(getSupportFragmentManager(), "privacy_dialog");
            isShowing = true;
        } else {
            if (LinphoneService.isReady()) {
                onServiceReady();
            } else {
                startService(new Intent().setClass(SplashActivity.this, LinphoneService.class));
                new ServiceWaitThread().start();
            }
        }

    }

    private void onServiceReady() {
        LinphoneUtils.dispatchOnUIThreadAfter(
                () -> {
                    if (sharedPreferences.getBoolean(Constant.IS_LOGIN, false)) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    }
                    finish();
                },
                200);
    }


    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!LinphoneService.isReady()) {
                try {
                    sleep(30);
                } catch (Exception e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            LinphoneUtils.dispatchOnUIThread(SplashActivity.this::onServiceReady);
//            requestBluePermission();
        }
    }
}
