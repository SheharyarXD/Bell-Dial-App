package belldial.co.uk.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import belldial.co.uk.R;
import belldial.co.uk.ui.provider.AppNavigationProvider;
import belldial.co.uk.ui.base.BaseActivity;

public class AuthActivity extends AppNavigationProvider {
    private static final int PERMISSION_REQUEST_CODE_CONTACT = 1;
    private static final int PERMISSION_REQUEST_CODE_CALL = 2;
    private static final int PERMISSION_REQUEST_CODE_PHONE = 3;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 4;

    @Override
    public int getPlaceHolder() {
        return R.id.placeHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        openLoginFragment(BaseActivity.PerformFragment.REPLACE);

        if (!checkContactPermission()) {
            requestContactPermission();
        }
        if (!checkCallPermission()) {
            requestCallPermission();
        }
        if (!checkPhonePermission()) {
            requestPhonePermission();
        }
        if (!checkStoragePermission()) {
            requestStorgaePermission();
        }
        if (!checkPhoneSPermission()) {
            requestPhoneSPermission();
        }
    }

    private void requestContactPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                    Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS
                },
                PERMISSION_REQUEST_CODE_CONTACT);
    }

    public void requestCallPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.RECORD_AUDIO},
                PERMISSION_REQUEST_CODE_CALL);
    }

    private void requestPhonePermission() {
        ActivityCompat.requestPermissions(
                this, new String[] {Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE_PHONE);
    }

    private void requestStorgaePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSION_REQUEST_CODE_STORAGE);
    }

    private void requestPhoneSPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[] {Manifest.permission.READ_PHONE_STATE},
                PERMISSION_REQUEST_CODE_PHONE);
    }

    public boolean checkContactPermission() {

        int readContacts =
                getPackageManager()
                        .checkPermission(Manifest.permission.READ_CONTACTS, getPackageName());
        int writeContacts =
                getPackageManager()
                        .checkPermission(Manifest.permission.WRITE_CONTACTS, getPackageName());
        return readContacts == PackageManager.PERMISSION_GRANTED
                && writeContacts == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkStoragePermission() {

        int readContacts =
                getPackageManager()
                        .checkPermission(
                                Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName());
        int writeContacts =
                getPackageManager()
                        .checkPermission(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName());
        return readContacts == PackageManager.PERMISSION_GRANTED
                && writeContacts == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkCallPermission() {
        int recordAudio =
                getPackageManager()
                        .checkPermission(Manifest.permission.RECORD_AUDIO, getPackageName());
        return recordAudio == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPhonePermission() {
        int call =
                getPackageManager()
                        .checkPermission(Manifest.permission.CALL_PHONE, getPackageName());
        return call == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPhoneSPermission() {
        int call =
                getPackageManager()
                        .checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName());
        return call == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE_CONTACT:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!checkCallPermission()) {
                            requestCallPermission();
                        }
                    }
                }
                break;
            case PERMISSION_REQUEST_CODE_CALL:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!checkPhonePermission()) {
                            requestPhonePermission();
                        }
                    }
                }
                break;
            case PERMISSION_REQUEST_CODE_PHONE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!checkStoragePermission()) {
                            requestStorgaePermission();
                        }
                    }
                }
                break;
            case PERMISSION_REQUEST_CODE_STORAGE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (!checkPhoneSPermission()) {
                            requestPhoneSPermission();
                        }
                    }
                }
                break;
        }
    }
}
