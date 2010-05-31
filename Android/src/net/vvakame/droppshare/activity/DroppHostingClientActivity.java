package net.vvakame.droppshare.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.HttpPostMultipartWrapper;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.helper.TwitterOAuthAccessor;
import net.vvakame.droppshare.model.OAuthData;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class DroppHostingClientActivity extends Activity implements LogTagIF {
	private static final int REQUEST_TWIT_INFO = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		OAuthData oauth = restoreOAuth();
		if (oauth == null) {
			Intent intent = new Intent(this, TwitterOAuthDialog.class);
			startActivityForResult(intent, REQUEST_TWIT_INFO);
		} else {
			try {
				pool(oauth);
			} catch (IOException e) {
				Log.e(TAG, HelperUtil.getExceptionLog(e));
				// TODO エラー処理ちゃんとする
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TWIT_INFO) {
			if (resultCode == RESULT_OK) {
				String name = data
						.getStringExtra(TwitterOAuthDialog.SCREEN_NAME);
				String password = data
						.getStringExtra(TwitterOAuthDialog.PASSWORD);

				try {
					OAuthData oath = TwitterOAuthAccessor.getAuthorizedData(
							name, password);
					saveOAuth(oath);
				} catch (IllegalStateException e) {
					throw e;
				} catch (IOException e) {
					throw new IllegalStateException(e);
				} catch (XmlPullParserException e) {
					throw new IllegalStateException(e);
				}
			} else if (resultCode == RESULT_CANCELED) {
				finish();
			}
		}
	}

	private void saveOAuth(OAuthData oauth) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
				.edit();
		editor.putString("screen_name", oauth.getScreenName());
		editor.putInt("oauth_hashcode", oauth.getOauthHashCode());
		editor.commit();
	}

	private OAuthData restoreOAuth() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		OAuthData oauth = new OAuthData();
		oauth.setScreenName(pref.getString("screen_name", null));
		oauth.setOauthHashCode(pref.getInt("oauth_hashcode", 0));

		return oauth.getScreenName() != null && oauth.getOauthHashCode() != 0 ? oauth
				: null;
	}

	public void pool(OAuthData oauth) throws IOException {
		if (oauth == null) {
			throw new IllegalArgumentException("Not authorized twitter oauth.");
		}

		Intent intent = getIntent();
		Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		File drozip = new File(fileUri.getPath());

		try {
			HttpPostMultipartWrapper post = new HttpPostMultipartWrapper(
					"http://192.168.0.6:8888/upload");
			post.pushString("screen_name", oauth.getScreenName());
			post.pushString("oauth_hashcode", oauth.getOauthHashCode());
			post.pushFile("drozip", drozip);
			post.close();

			String str = post.readResponse();
			str.toString();

		} catch (FileNotFoundException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
			throw e;
		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
			throw e;
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
			throw e;
		}
	}
}
