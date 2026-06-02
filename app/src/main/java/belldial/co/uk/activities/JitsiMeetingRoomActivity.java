package belldial.co.uk.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class JitsiMeetingRoomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //    implements JitsiMeetActivityInterface


//    private JitsiMeetView view;
//
//    String roomName = "";
//    String userName = "";
//
//    // JitsiMeetUserInfo jitsiMeetUserInfo;
//    JitsiMeetViewListener jitsiMeetViewListener;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // setContentView(R.layout.activity_jitsi_meeting_room);
//        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_color));
//
//        roomName = getIntent().getStringExtra("roomName");
//        userName = getIntent().getStringExtra("name");
//
//        view = new JitsiMeetView(JitsiMeetingRoomActivity.this);
//
//        try {
//            JitsiMeetUserInfo jitsiMeetUserInfo = new JitsiMeetUserInfo();
//            jitsiMeetUserInfo = new JitsiMeetUserInfo();
//            jitsiMeetUserInfo.setDisplayName(userName);
//            JitsiMeetConferenceOptions options =
//                    new JitsiMeetConferenceOptions.Builder()
//                            .setServerURL(new URL("https://meet.belldial.com/"))
//                            .setUserInfo(jitsiMeetUserInfo)
//                            .setRoom(roomName)
//                            .setToken(
//                                    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJncm91cCI6IlJvY2tldC5DaGF0IiwiYXVkIjoiTzk2Q1VRbWdIS2VOZ1R5biIsImlzcyI6Ik85NkNVUW1nSEtlTmdUeW4iLCJzdWIiOiJzaGFyZDAxLmJlbGxkaWFsLmNvbSIsInJvb20iOiIqIiwiYWxnb3JpdGhtIjoiSFMyNTYifQ.9e9TsmyHAoZuFzJFScxQSFf8KBR0o0bYpj9_446HO1E")
//                            .setAudioMuted(false)
//                            .setVideoMuted(false)
//                            .setAudioOnly(false)
//                            .setConfigOverride("requireDisplayName", true)
//                            .build();
//
//            view.join(options);
//
//            setContentView(view);
//
//            jitsiMeetViewListener =
//                    new JitsiMeetViewListener() {
//                        @Override
//                        public void onConferenceJoined(Map<String, Object> map) {}
//
//                        @Override
//                        public void onConferenceTerminated(Map<String, Object> map) {
//                            // Toast.makeText(MyOnlineLiveClassSchoolActivity.this, "Closing
//                            // Meeting", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//
//                        @Override
//                        public void onConferenceWillJoin(Map<String, Object> map) {}
//                    };
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        view.setListener(jitsiMeetViewListener);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        JitsiMeetActivityDelegate.onRequestPermissionsResult(
//                requestCode, permissions, grantResults);
//    }
//
//    @Override
//    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {
//        int PERMISSION_ALL = 1011;
//        String[] PERMISSIONS = {
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.MODIFY_AUDIO_SETTINGS,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//            // android.Manifest.permission.CAMERA
//        };
//        JitsiMeetActivityDelegate.requestPermissions(
//                JitsiMeetingRoomActivity.this, PERMISSIONS, 111, permissionListener);
//    }
//
//    @Override
//    public void onBackPressed() {
//        // super.onBackPressed();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Leave");
//        builder.setMessage("Are you sure, you want to leave this session ?");
//        builder.setPositiveButton(
//                "YES",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do nothing but close the dialog
//                        view.leave();
//                        dialog.dismiss();
//                    }
//                });
//        builder.setNegativeButton(
//                "NO",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Do nothing
//                        dialog.dismiss();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}
