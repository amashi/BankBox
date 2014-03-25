package org.cto.VVS3Box.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cto.VVS3Box.AndroidClientAPIStub;
import org.cto.VVS3Box.api.AndroidClientAPI;
import org.cto.VVS3Box.api.VVEXcption;
import org.cto.VVS3Box.api.VVFileInfo;
import org.cto.VVS3Box.api.VVUserInfo;
import org.lior.VVS3Box.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UserSummaryActivity extends BaseActivity {

	private static final Log logger = LogFactory
			.getLog(UserSummaryActivity.class);

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_summary);
		VVUserInfo userInfo;
		try {
			userInfo = getAPI().getUserInfo();
			((TextView) findViewById(R.id.txtFullName)).setText(userInfo
					.getFullname());
			((TextView) findViewById(R.id.activity_main_txtUrl)).setText(userInfo
					.getUsername());

			int quotaUsed = (int) ((userInfo.getSpaceUsed() * 100) / userInfo
					.getSpaceTotal());
			((ProgressBar) findViewById(R.id.prgStorageInfo))
					.setProgress(quotaUsed);
			String storageText = String
					.format("%s / %s", VVFileInfo
							.humanReadableByteCount(userInfo.getSpaceUsed()),
							VVFileInfo.humanReadableByteCount(userInfo
									.getSpaceTotal()));
			((TextView) findViewById(R.id.txtStorageInfo)).setText(storageText);
			((TextView) findViewById(R.id.activity_userInfo_txtSessionInfo))
					.setText(getAPI().getSession().toString());
		} catch (VVEXcption e) {

			logger.error("error getting user information", e);
		}
	}

	public void cmdOk(View view) {
		finish();
	}

}
