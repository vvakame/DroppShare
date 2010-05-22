package net.vvakame.droppshare.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.vvakame.android.helper.HelperUtil;
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

	public void pool(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		File drozip = new File(fileUri.getPath());

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "drozip";

		int bytesRead, bytesAvailable, bufferSize;

		int maxBufferSize = 1 * 1024 * 1024;

		String responseFromServer = "";

		HttpURLConnection conn = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(drozip);

			URL url = new URL("http://192.168.0.6:8888/upload");

			conn = (HttpURLConnection) url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ drozip.getName() + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				Log.i(TAG, "Message: " + line);
				responseFromServer += line;
			}
			rd.close();

		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}
	}
}
