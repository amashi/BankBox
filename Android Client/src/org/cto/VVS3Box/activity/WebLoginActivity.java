package org.cto.VVS3Box.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cto.VVS3Box.AndroidClientAPIStub;
import org.cto.VVS3Box.api.AndroidClientAPI;
import org.lior.VVS3Box.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebLoginActivity extends BaseActivity {
	private static final Log logger = LogFactory.getLog(WebLoginActivity.class);

	private AndroidClientAPI vvapi = AndroidClientAPIStub.getApi();

	private boolean isSimulationMode;
	private String authenticationCookieName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Allow the title bar to show loading progress.
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_web_login);
		
		WebView webLogin = (WebView) findViewById(R.id.webLogin);
		webLogin.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		// test if the 'simulate login' check was pressed
		isSimulationMode = getIntent().getBooleanExtra("bypassLogin", false);
		authenticationCookieName = getIntent().getStringExtra("authCookieName");
		webLogin.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 100);
			}
		});
		webLogin.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}

			// check the cookie each time a page is finished. If the
			// ‘oauth_token’ cookie is found
			@Override
			public void onPageFinished(WebView view, String url) {
				// if the 'simulate login' check was pressed, then bypass the login
				if (isSimulationMode)
				{
					Toast.makeText(
							getApplicationContext(),
							"Simulation mode - returning dummy Identity Key...",
							Toast.LENGTH_LONG).show();
					// put a dummy identity key
					Intent result = new Intent();
					result.putExtra(Intent.EXTRA_TEXT, "123456789");
					setResult(RESULT_OK, result);
					finish();
					return;
				}
				
				CookieSyncManager.getInstance().sync();
				// Get the cookie from cookie jar.
				String cookie = CookieManager.getInstance().getCookie(url);
				if (cookie == null) {
					return;
				}
				// Cookie is a string like NAME=VALUE [; NAME=VALUE]
				String[] pairs = cookie.split(";");
				for (int i = 0; i < pairs.length; ++i) {
					String[] parts = pairs[i].split("=", 2);
					// If token is found, return it to the calling activity.
					if (parts.length == 2
							&& parts[0].equalsIgnoreCase(authenticationCookieName)) {
						logger.info("login successful");
						Toast.makeText(getApplicationContext(), "Login successful! redirecting back to application...", Toast.LENGTH_LONG).show();
						Intent result = new Intent();
						result.putExtra(Intent.EXTRA_TEXT, parts[1]);
						setResult(RESULT_OK, result);
						finish();
					}
				}
			}
		});

		webLogin.loadUrl(getWebLoginUrl());
	}

	/**
	 * gets the web login url from the intent or use the default value
	 * 
	 * @return
	 */
	private String getWebLoginUrl() {
		String url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		if (url == null ) {
			logger.warn("web login url was not supplied. using default: " + url);
			url = vvapi.getWebLoginUrl();
		}

		logger.debug("using web login url: " + url);
		return url;
	}

}
