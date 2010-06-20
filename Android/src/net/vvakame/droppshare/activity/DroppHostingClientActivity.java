package net.vvakame.droppshare.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import net.vvakame.android.helper.Closure;
import net.vvakame.android.helper.DrivenHandler;
import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.HttpPostMultipartWrapper;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.model.OAuthData;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DroppHostingClientActivity extends Activity implements LogTagIF {
	private static final int DIALOG_PROGRESS = 1;

	private static final int MESSAGE_START_PROGRESS = 1;
	private static final int MESSAGE_FINISH_PROGRESS = 2;
	private static final int MESSAGE_FAILED_PROGRESS = 3;
	private static final int MESSAGE_EXCEPTION_PROGRESS = 4;

	private ProgressDialog mProgDialog = null;
	private final DrivenHandler mHandler = new DrivenHandler(this,
			DIALOG_PROGRESS);

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		process();
	}

	@Override
	public void onNewIntent(Intent intent) {
		switchAction(intent);
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
			progDialog.setTitle(getString(R.string.uploading));
			progDialog.setMessage(getString(R.string.wait_a_moment));
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setCancelable(false);

			break;
		default:
			break;
		}
	}

	private void switchAction(Intent receive) {
		if (receive.getAction().equals(Intent.ACTION_SEND)) {
			process();
		} else if (receive.getAction().equals(Intent.ACTION_VIEW)) {
			doProcessRedirect(receive.getData());
		}
	}

	private void doOAuthAuthorize() {
		Intent oauthIntent = null;

		oauthIntent = new Intent();
		oauthIntent.setAction(Intent.ACTION_VIEW);
		oauthIntent.setData(Uri.parse("http://drphost.appspot.com/twitter"));

		startActivity(oauthIntent);
	}

	private void doProcessRedirect(Uri data) {
		String str = data.getSchemeSpecificPart();

		String screenName = str.substring(0, str.indexOf("?"));
		String hash = str.substring(str.indexOf("hash=") + "hash=".length());

		OAuthData oauth = new OAuthData();
		oauth.setScreenName(screenName);
		oauth.setOauthHashCode(Long.parseLong(hash));

		saveOAuth(oauth);
		// setIntentしてないので、ACTION_SENDのIntentが取れるはず
		onNewIntent(getIntent());
	}

	public void process() {
		final OAuthData oauth = restoreOAuth();
		if (oauth == null) {
			doOAuthAuthorize();
		} else {
			Closure cloFinish = new Closure() {
				@Override
				public void exec() {
					Toast.makeText(DroppHostingClientActivity.this,
							getString(R.string.done_upload), Toast.LENGTH_LONG)
							.show();
					finish();
				}
			};

			Closure cloFailed = new Closure() {
				@Override
				public void exec() {
					Toast.makeText(DroppHostingClientActivity.this,
							getString(R.string.failed_upload),
							Toast.LENGTH_LONG).show();
					finish();
				}
			};

			mHandler.pushEventWithShowDialog(MESSAGE_START_PROGRESS, null);
			mHandler.pushEventWithDissmiss(MESSAGE_FINISH_PROGRESS, cloFinish);
			mHandler.pushEventWithDissmiss(MESSAGE_FAILED_PROGRESS, cloFailed);

			new Thread() {
				@Override
				public void run() {
					boolean done = false;

					mHandler.sendEmptyMessage(MESSAGE_START_PROGRESS);
					try {
						done = doUpload(oauth);
					} catch (IOException e) {
						sanitizeException(e);
					}

					if (done) {
						mHandler.sendEmptyMessage(MESSAGE_FINISH_PROGRESS);
					} else {
						mHandler.sendEmptyMessage(MESSAGE_FAILED_PROGRESS);
					}
				}
			}.start();
		}
	}

	public boolean doUpload(OAuthData oauth) throws IOException {
		boolean success = false;

		if (oauth == null) {
			throw new IllegalArgumentException("Not authorized twitter oauth.");
		}

		Intent intent = getIntent();
		Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		File drozip = new File(fileUri.getPath());

		try {
			HttpPostMultipartWrapper post = new HttpPostMultipartWrapper(
					"http://drphost.appspot.com/upload");
			post.pushString("screen_name", oauth.getScreenName());
			post.pushString("oauth_hashcode", String.valueOf(oauth
					.getOauthHashCode()));
			post.pushString("variant", "default");
			post.pushFile("drozip", drozip);
			post.close();

			post.readResponse();
			success = true;

		} catch (FileNotFoundException e) {
			deleteOAuth();
			sanitizeException(e);
		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
			throw e;
		} catch (IOException e) {
			sanitizeException(e);
		}

		return success;
	}

	private void saveOAuth(OAuthData oauth) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		editor.putString("screen_name", oauth.getScreenName());
		editor.putLong("oauth_hashcode", oauth.getOauthHashCode());
		editor.commit();
	}

	private OAuthData restoreOAuth() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		OAuthData oauth = new OAuthData();
		oauth.setScreenName(pref.getString("screen_name", null));
		oauth.setOauthHashCode(pref.getLong("oauth_hashcode", 0));

		return oauth.getScreenName() != null && oauth.getOauthHashCode() != 0 ? oauth
				: null;
	}

	private void deleteOAuth() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		pref.edit().remove("oauth_hashcode").commit();
	}

	public void sanitizeException(Exception e) {
		Log.e(TAG, HelperUtil.getExceptionLog(e));

		final String message = e.getMessage();

		Closure clo = new Closure() {
			@Override
			public void exec() {
				Toast.makeText(DroppHostingClientActivity.this, message,
						Toast.LENGTH_LONG).show();
				finish();
			}
		};

		mHandler.pushEventWithDissmiss(MESSAGE_EXCEPTION_PROGRESS, clo);
		mHandler.sendEmptyMessage(MESSAGE_EXCEPTION_PROGRESS);
	}
}
