package net.vvakame.droppshare.activity;

import java.util.List;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.AppDataAdapter;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;

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
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;

public class DroppShareActivity extends Activity implements SimejiIF {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private OnItemClickListener mEventImpl = null;

	private List<AppData> mAppDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		TabHost tabs = (TabHost) this.findViewById(R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec tab1 = tabs.newTabSpec("installed");
		tab1.setContent(R.id.installed);
		tab1.setIndicator(getString(R.string.installed), getResources()
				.getDrawable(R.drawable.icon));
		tabs.addTab(tab1);

		tabs.setCurrentTab(0);

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void constructCache(boolean clearFlg) {
		DroppCacheAsynkTask asyncTask = new DroppCacheAsynkTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
						mAppDataList = arg;
						AppDataAdapter appAdapter = new AppDataAdapter(
								DroppShareActivity.this,
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
			constructCache(true);

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
