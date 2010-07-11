package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.Closure;
import net.vvakame.android.helper.DrivenHandler;
import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.DroppHostingHelper;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.helper.OAuthHelper;
import net.vvakame.droppshare.model.OAuthData;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TwitterOAuthActivity extends Activity implements LogTagIF {
	private static final int DIALOG_PROGRESS = 1;

	private static final int MESSAGE_START_PROGRESS = 1;
	private static final int MESSAGE_FINISH_PROGRESS = 2;
	private static final int MESSAGE_FAILED_PROGRESS = 3;

	private ProgressDialog mProgDialog = null;
	private final DrivenHandler mHandler = new DrivenHandler(this,
			DIALOG_PROGRESS);

	Closure mCloFailed = new Closure() {
		@Override
		public void exec() {
			Toast.makeText(TwitterOAuthActivity.this,
					getString(R.string.failed_connect), Toast.LENGTH_LONG)
					.show();
		}
	};

	private WebViewClient mClient = new WebViewClient() {
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Uri uri = Uri.parse(url);
			if (uri.getScheme().startsWith(getString(R.string.drphost_scheme))) {
				OAuthData oauth = createOAuthData(uri);
				OAuthHelper.saveOAuth(TwitterOAuthActivity.this, oauth);
				setResult(RESULT_OK);
				finish();
			}

			if (!url.contains("twitter.com")) {
				mHandler.sendEmptyMessage(MESSAGE_START_PROGRESS);
			}
		}

		public void onPageFinished(WebView view, String url) {
			mHandler.sendEmptyMessage(MESSAGE_FINISH_PROGRESS);
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			mHandler.sendEmptyMessage(MESSAGE_FAILED_PROGRESS);

			Toast.makeText(TwitterOAuthActivity.this, description,
					Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
			finishActivity(0);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		OAuthHelper.removeOAuth(this);

		mHandler.pushEventWithShowDialog(MESSAGE_START_PROGRESS, null);
		mHandler.pushEventWithDissmiss(MESSAGE_FINISH_PROGRESS, null);
		mHandler.pushEventWithDissmiss(MESSAGE_FAILED_PROGRESS, mCloFailed);

		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();

		setContentView(R.layout.twitter_oauth);
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.setWebViewClient(mClient);
		WebSettings websettings = webview.getSettings();
		websettings.setBuiltInZoomControls(true);
		websettings.setJavaScriptEnabled(true);
		websettings.setSaveFormData(false);
		websettings.setSavePassword(false);

		webview.loadUrl(DroppHostingHelper.getTwitterUri(this));
	}

	private OAuthData createOAuthData(Uri data) {
		String str = data.getSchemeSpecificPart();

		String screenName = str.substring(0, str.indexOf("?"));
		String hash = str.substring(str.indexOf("hash=") + "hash=".length());

		OAuthData oauth = new OAuthData();
		oauth.setScreenName(screenName);
		oauth.setOauthHashCode(Long.parseLong(hash));

		return oauth;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			mProgDialog = new ProgressDialog(this);
			onPrepareDialog(id, mProgDialog);

			return mProgDialog;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_PROGRESS:
			ProgressDialog progDialog = (ProgressDialog) dialog;
			progDialog.setTitle(getString(R.string.connecting_server));
			progDialog.setMessage(getString(R.string.wait_a_moment));
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setCancelable(false);

			break;
		default:
			break;
		}
	}
}
