package belldial.co.uk.assistant;

/*
OpenH264DownloadAssistantActivity.java
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

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.PayloadType;
import org.linphone.core.tools.Log;
import belldial.co.uk.settings.LinphonePreferences;

public class OpenH264DownloadAssistantActivity extends AssistantActivity {
    private TextView mYes, mNo;
    private ProgressBar mProgress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assistant_openh264_codec_download);

        LinphonePreferences.instance().setOpenH264CodecDownloadEnabled(false);

        mProgress = findViewById(R.id.progress_bar);

        mYes = findViewById(R.id.answer_yes);
        mYes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mYes.setEnabled(false);
                        mNo.setEnabled(false);
                        Log.e("[OpenH264 Downloader] Start download");
                        mProgress.setVisibility(View.VISIBLE);
                    }
                });

        mNo = findViewById(R.id.answer_no);
        mNo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mYes.setEnabled(false);
                        mNo.setEnabled(false);
                        Log.e("[OpenH264 Downloader] Download refused");
                        goToLinphoneActivity();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void enableH264() {
        for (PayloadType pt : LinphoneManager.getCore().getVideoPayloadTypes()) {
            if (pt.getMimeType().equals("H264")) {
                pt.enable(true);
                break;
            }
        }
    }
}
