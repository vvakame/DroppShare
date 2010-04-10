package net.vvakame.droppshare.activity;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.activity.DroppRedirectActivity;
import net.vvakame.droppshare.helper.HelperUtil;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.TextView;

public class DroppRedirectActivityTest extends
		ActivityInstrumentationTestCase2<DroppRedirectActivity> {
	private static final String TAG = DroppRedirectActivityTest.class
			.getSimpleName();

	private TextView mText;

	public DroppRedirectActivityTest() {
		super("net.vvakame.droppshare", DroppRedirectActivity.class);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		Intent intent = new Intent(Intent.ACTION_SEND);
		Bundle data = new Bundle();
		data.putString(Intent.EXTRA_TEXT, "hogehoge");
		intent.putExtras(data);

		setActivityIntent(intent);
	}

	@Override
	protected void setUp() throws Exception {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.setUp();
		mText = (TextView) getActivity().findViewById(R.id.receive_text);
	}

	public void test01() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		// UIスレッドに処理をお願いする。
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// 特になんもなし
			}
		});

		// UIスレッドの処理完了を待つ
		getInstrumentation().waitForIdleSync();

		assertEquals("hogehoge", mText.getText().toString());
	}
}
