package net.vvakame.droppshare;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DroppShareActivity extends Activity {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
	private static final String REPLACE_KEY = "replace_key";

	private boolean mDone = false;

	private OnItemClickListener mEventImpl = null;

	private List<AppData> mAppDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		if (isCalledBySimeji()) {
			mEventImpl = new EventSimejiImpl();
		} else {
			mEventImpl = new EventNormalImpl();
		}
	}

	@Override
	public void onResume() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onResume();

		if (mDone && mAppDataList != null && mAppDataList.size() != 0) {
			return;
		}

		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setOnItemClickListener(mEventImpl);

		constructCache();
	}

	private void constructCache() {
		mDone = false;

		// TODO Cacheの考慮を入れる
		try {
			mAppDataList = new DroppCacheAsynkTask(this).execute().get();
		} catch (InterruptedException e) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
					+ e.getClass().getSimpleName() + ", " + e.getMessage());
			return;
		} catch (ExecutionException e) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
					+ e.getClass().getSimpleName() + ", " + e.getMessage());
			return;
		}

		AppDataAdapter appAdapter = new AppDataAdapter(DroppShareActivity.this,
				R.layout.application_view, mAppDataList);
		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setAdapter(appAdapter);

		mDone = true;
	}

	@Override
	public void onPause() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.cache_refresh:
			File[] allCacheFiles = getFilesDir().listFiles();
			for (File file : allCacheFiles) {
				file.delete();
			}

			constructCache();

			break;

		case R.id.preferences:
			intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			break;

		default:
			ret = super.onOptionsItemSelected(item);
			break;
		}
		return ret;
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
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
