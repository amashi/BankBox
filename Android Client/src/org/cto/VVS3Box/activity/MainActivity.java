package org.cto.VVS3Box.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cto.VVS3Box.api.VVEXcption;
import org.lior.VVS3Box.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private static final Log logger = LogFactory.getLog(MainActivity.class);

	private static final int REQUEST_WEB_LOGIN = 0;

	EditText txtwebLoginUrl;

	private TextView txtWelcomeMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// hide the action bar on the main screen
		// getActionBar().hide();
		txtwebLoginUrl = (EditText) findViewById(R.id.activity_main_txtUrl);
		txtWelcomeMessage = (TextView) findViewById(R.id.activity_login_welcomeMsg);
		txtwebLoginUrl.setText(getAPI().getWebLoginUrl());

		refreshControls();
	}

	public void cmdDebugMode(View view) {
		refreshControls();
	}

	public void cmdWebLogin(View view) {
		logger.debug("launching web login activity");
		String cookieName = ((EditText) findViewById(R.id.activity_main_txtCookie)).getText().toString();
		Intent intent1 = new Intent(this, WebLoginActivity.class);
		// pass the url to the activity
		intent1.putExtra(Intent.EXTRA_TEXT, txtwebLoginUrl.getText().toString());
		// tell the webView that we want to bypass the web login
		intent1.putExtra("bypassLogin",
				((CheckBox) findViewById(R.id.activity_main_chkSimulateLogin))
						.isChecked());
		intent1.putExtra("authCookieName", cookieName);
		startActivityForResult(intent1, REQUEST_WEB_LOGIN);
	}

	public void cmdLogout(View view) {
		logger.info("logging out");
		logoutCurrentUser();
	}
	
	
	public void cmdContinue(View view) {
		Intent intent1 = new Intent(this, RemoteBrowserActivity.class);
		startActivity(intent1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_WEB_LOGIN:
			if (resultCode != RESULT_OK || data == null) {
				// invalid login result - perform logout
				logoutCurrentUser();
				return;
			}
			// Get the token.
			String identityKey = data.getStringExtra(Intent.EXTRA_TEXT);
			if (identityKey != null) {
				// perform login
				loginWithIdentityKey(identityKey);
			} else
				logoutCurrentUser();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * login to the API using the identity key obtained from the Web Login
	 * 
	 * @param identityKey
	 */
	private void loginWithIdentityKey(String identityKey) {
		logger.debug("using identity key from Web Login: " + identityKey);
		try {
			getAPI().login(identityKey);
		} catch (VVEXcption e) {
			showAlertDialog("Login error",
					"Unable to contact VV Server for login. the message was:\n"
							+ e.getMessage());
			logoutCurrentUser();
		}
		refreshControls();
	}

	/**
	 * logout the current user
	 */
	private void logoutCurrentUser() {
		try {
			getAPI().logout();
		} catch (VVEXcption e) {
			logger.error("error logging out!", e);
		}
		refreshControls();
	}

	/**
	 * update UI elements data and state according to the current state
	 */
	@Override
	protected void refreshControls() {
		super.refreshControls();
		boolean debugMode = ((CheckBox) findViewById(R.id.activity_main_chkDebugMode))
				.isChecked();
		findViewById(R.id.activity_main_debugView).setVisibility(
				debugMode ? View.VISIBLE : View.GONE);
		if (getAPI().isLoggedIn()) {
			// set user nama
			try {
				String msg = txtWelcomeMessage.getText().toString();
				msg = msg.replace("[user]", getAPI().getUserInfo()
						.getFullname());
				txtWelcomeMessage.setText(msg);
			} catch (VVEXcption e) {
				logger.warn("unable to get user info", e);
			}
			// hide the login input boxes and show the welcome boxes
			findViewById(R.id.activity_main_loggedOut).setVisibility(View.GONE);
			findViewById(R.id.activity_login_alreadyLoggedIn).setVisibility(
					View.VISIBLE);
		} else {
			findViewById(R.id.activity_main_loggedOut).setVisibility(
					View.VISIBLE);
			findViewById(R.id.activity_login_alreadyLoggedIn).setVisibility(
					View.GONE);
		}
	}
}
