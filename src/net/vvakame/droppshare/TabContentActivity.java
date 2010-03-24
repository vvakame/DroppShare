package net.vvakame.droppshare;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class TabContentActivity extends Activity implements SimejiIF {
	private static final String TAG = TabContentActivity.class.getSimpleName();

	private OnItemClickListener mEventImpl = null;

	private List<AppData> mAppDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.tab_contents);

		if (isCalledBySimeji()) {
			mEventImpl = new EventSimejiImpl();
		} else {
			mEventImpl = new EventNormalImpl();
		}

		constructCache(false);
	}

	@Override
	public void onResume() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onResume();
	}

	private void constructCache(boolean clearFlg) {
		DroppCacheAsynkTask asyncTask = new DroppCacheAsynkTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
						mAppDataList = arg;
						AppDataAdapter appAdapter = new AppDataAdapter(
								TabContentActivity.this,
								R.layout.application_view, arg);
						ListView listView = (ListView) findViewById(R.id.app_list);
						listView.setAdapter(appAdapter);
						if (mAppDataList != null && mAppDataList.size() != 0) {
							listView.setOnItemClickListener(mEventImpl);
						}
					}
				});
		asyncTask.execute(clearFlg);
	}

	@Override
	public void onPause() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onPause();
	}

	private String genPassionateMessage(AppData appData) {
		String message = PreferencesActivity.getMessageTemplate(this);

		String uri = null;
		if (PreferencesActivity.isHttp(this)) {
			uri = AppDataUtil.getHttpUriFromAppData(appData);
		} else {
			uri = AppDataUtil.getMarketUriFromAppData(appData);
		}

		message = message.replace("$app", appData.getAppName());
		message = message.replace("$market", uri);
		message = message.replace("$version", appData.getVersionName());
		String description = appData.getDescription() == null ? "" : appData
				.getDescription();
		message = message.replace("$description", description);
		message = message.replaceAll(" +", " ");

		return message;
	}

	private boolean isCalledBySimeji() {
		Intent intent = getIntent();
		String action = intent.getAction();

		// Simejiから呼び出された時
		if (action != null && ACTION_INTERCEPT.equals(action)) {
			return true;
		} else {
			return false;
		}
	}

	class EventNormalImpl implements OnItemClickListener {
		private final String TAG = EventNormalImpl.class.getSimpleName();

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

			AppData appData = mAppDataList.get(position);

			Intent data = new Intent();
			data.setAction(Intent.ACTION_SEND);
			data.setType("text/plain");
			data.putExtra(Intent.EXTRA_TEXT, genPassionateMessage(appData));

			startActivity(data);
			finish();
		}
	}

	class EventSimejiImpl implements OnItemClickListener {
		private final String TAG = EventSimejiImpl.class.getSimpleName();

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

			AppData appData = mAppDataList.get(position);

			pushToSimeji(genPassionateMessage(appData));
		}

		private void pushToSimeji(String result) {
			Intent data = new Intent();
			data.putExtra(REPLACE_KEY, result);
			setResult(RESULT_OK, data);
			finish();
		}
	}
}
