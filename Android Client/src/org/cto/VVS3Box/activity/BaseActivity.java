package org.cto.VVS3Box.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cto.VVS3Box.AndroidClientAPIStub;
import org.cto.VVS3Box.api.AndroidClientAPI;
import org.lior.VVS3Box.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Common activity code that displays the action bar and handles action bar
 * commands
 * 
 * @author lior
 * 
 */
public class BaseActivity extends Activity {

	private static final Log logger = LogFactory.getLog(BaseActivity.class);

	private AndroidClientAPI vvapi = AndroidClientAPIStub.getApi();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_activity_actions, menu);
		// set the 'enabled' state
		return super.onCreateOptionsMenu(menu);
	}

	protected AndroidClientAPI getAPI() {
		return vvapi;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_user_summary)
				.setVisible(
						getAPI().isLoggedIn()
								&& !(this instanceof UserSummaryActivity));
		menu.findItem(R.id.action_uploadFile).setVisible(
				this instanceof RemoteBrowserActivity);
		menu.findItem(R.id.action_refresh).setVisible(
				this instanceof RemoteBrowserActivity);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_user_summary:
			startActivity(new Intent(this, UserSummaryActivity.class));
			return true;
		case R.id.action_settings:
			logger.debug("Setting button pressed");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Display a simple alert dialog with a single OK button
	 */
	protected void showAlertDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do things
					}
				});
		builder.create().show();
	}

	protected void refreshControls() {
		invalidateOptionsMenu();
	}

}
