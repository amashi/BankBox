package org.cto.VVS3Box.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cto.VVS3Box.AndroidClientAPIStub;
import org.cto.VVS3Box.api.AndroidClientAPI;
import org.lior.VVS3Box.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends BaseActivity {

	private static final Log logger = LogFactory.getLog(TestActivity.class);

	private AndroidClientAPI vvapi = AndroidClientAPIStub.getApi();
	private static final int REQUEST_BROWSE_LOCAL = 1;
	private TextView txtMessage;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
	}
	
	
	public void cmdBrowseLocal(View view) {
		Intent intent1 = new Intent(this, LocalFileBrowserActivity.class);
		startActivityForResult(intent1, REQUEST_BROWSE_LOCAL);
	}

	public void cmdS3Test(View view) {
		if (!vvapi.isLoggedIn()) {
			Toast.makeText(getApplicationContext(), "Please Login First!",
					Toast.LENGTH_SHORT).show();
			return;
		}
//		File[] allFiles;
//		try {
//			allFiles = vvapi.getLocalFileSystem().findFiles(
//					Selectors.EXCLUDE_SELF);
//			Toast.makeText(getApplicationContext(),
//					"files in bucket: " + allFiles.length, Toast.LENGTH_SHORT)
//					.show();
//		} catch (IOException e) {
//			logger.error("error listing remote files ", e);
//		}

	}

	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		if (requestCode == REQUEST_BROWSE_LOCAL) {
			if (resultCode == RESULT_OK) {
				String curFileName = data.getStringExtra("GetFileName");
				txtMessage.setText(curFileName);
			}
		}
	}

}
