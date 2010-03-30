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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;

public class DroppShareActivity extends Activity implements SimejiIF {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private OnItemClickListener mEventImpl = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarVisibility(true);

		setContentView(R.layout.main);
		TabHost tarHost = (TabHost) findViewById(R.id.tabhost);
		tarHost.setup();

		TabHost.TabSpec tab = tarHost.newTabSpec("installed");
		tab.setContent(R.id.installed);
		tab.setIndicator(getString(R.string.installed), getResources()
				.getDrawable(android.R.drawable.ic_menu_share));
		tarHost.addTab(tab);

		tarHost.setCurrentTab(0);

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

	private Func<List<AppData>> mInstalledFunc = null;
	private Func<List<AppData>> mHistoryFunc = null;
	private Func<List<AppData>> mRecentFunc = null;

	private void constructCache(boolean clearFlg) {
		// あまりにも分かりにくいのでコメントとして何がしたいかを残す。要は、
		// 1. DroppInstalledAsyncTaskを非同期で蹴る
		// 2. 1が終わったら、DroppHistoryAsyncTaskを蹴る

		mInstalledFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				if (arg != null && arg.size() != 0) {
					AppDataAdapter appAdapter = new AppDataAdapter(
							DroppShareActivity.this, R.layout.application_view,
							arg);

					ListView listView = (ListView) findViewById(R.id.installed_list);
					listView.setAdapter(appAdapter);
					listView.setOnItemClickListener(mEventImpl);
				}

				new DroppHistoryAsynkTask(DroppShareActivity.this, mHistoryFunc)
						.execute();
			}
		};

		mHistoryFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				if (arg != null && arg.size() != 0) {
					AppDataAdapter appAdapter = new AppDataAdapter(
							DroppShareActivity.this, R.layout.application_view,
							arg);

					ListView listView = (ListView) findViewById(R.id.history_list);
					listView.setAdapter(appAdapter);
					listView.setOnItemClickListener(mEventImpl);
				}

				TabHost tabHost = (TabHost) DroppShareActivity.this
						.findViewById(R.id.tabhost);
				TabHost.TabSpec tab = tabHost.newTabSpec("history");
				tab.setContent(R.id.history);
				tab.setIndicator(getString(R.string.history), getResources()
						.getDrawable(android.R.drawable.ic_menu_myplaces));
				tabHost.addTab(tab);

				new DroppRecentlyUsedAsynkTask(DroppShareActivity.this,
						mRecentFunc).execute();
			}
		};

		mRecentFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				if (arg != null && arg.size() != 0) {
					AppDataAdapter appAdapter = new AppDataAdapter(
							DroppShareActivity.this, R.layout.application_view,
							arg);

					ListView listView = (ListView) findViewById(R.id.recent_list);
					listView.setAdapter(appAdapter);
					listView.setOnItemClickListener(mEventImpl);
				}

				TabHost tabHost = (TabHost) DroppShareActivity.this
						.findViewById(R.id.tabhost);
				TabHost.TabSpec tab = tabHost.newTabSpec("recent");
				tab.setContent(R.id.recently_used);
				tab
						.setIndicator(
								getString(R.string.recent),
								getResources()
										.getDrawable(
												android.R.drawable.ic_menu_recent_history));
				tabHost.addTab(tab);
			}
		};

		new DroppInstalledAsynkTask(this, mInstalledFunc).execute(clearFlg);
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
			AppDataAdapter adapter = (AppDataAdapter) parent.getAdapter();
			AppData appData = adapter.getItem(position);

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
			AppDataAdapter adapter = (AppDataAdapter) parent.getAdapter();
			AppData appData = adapter.getItem(position);

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
