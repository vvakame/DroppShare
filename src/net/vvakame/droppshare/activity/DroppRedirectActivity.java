package net.vvakame.droppshare.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vvakame.droppshare.helper.HelperUtil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class DroppRedirectActivity extends Activity {
	private static final String TAG = DroppRedirectActivity.class
			.getSimpleName();

	private static final Pattern MARKET_PATTERN = Pattern
			.compile("(market://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+)");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		String receiveStr = data.getString(Intent.EXTRA_TEXT);

		Matcher matcher = MARKET_PATTERN.matcher(receiveStr);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", before="
				+ receiveStr);

		String uri = null;
		while (matcher.find()) {
			uri = matcher.group();
			break;
		}

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", after=" + uri);
		if (uri == null) {
			finish();
			return;
		}

		try {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(sendIntent);
		} catch (ActivityNotFoundException e) {
			Log.d(TAG, TAG + ":" + HelperUtil.getExceptionLog(e));
			throw e;
		}
		finish();
	}
}
