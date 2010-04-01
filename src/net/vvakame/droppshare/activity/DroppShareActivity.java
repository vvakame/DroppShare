package net.vvakame.droppshare.activity;

import java.util.List;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.AppDataAdapter;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

	private AppDataAdapter mInstalledAdapter = null;
	private AppDataAdapter mHistoryAdapter = null;
	private AppDataAdapter mRecentAdapter = null;
	private View mProgressBar = null;

	private OnItemClickListener mEventImpl = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		setProgressBarIndeterminateVisibility(true);

		// タブの準備
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		TabHost.TabSpec tab;

		// インストール履歴タブ
		tabHost = (TabHost) DroppShareActivity.this.findViewById(R.id.tabhost);
		tab = tabHost.newTabSpec("history");
		tab.setContent(R.id.history);
		tab.setIndicator(getString(R.string.history), getResources()
				.getDrawable(android.R.drawable.ic_menu_myplaces));
		tabHost.addTab(tab);

		// 最近実行したアプリタブ
		tabHost = (TabHost) DroppShareActivity.this.findViewById(R.id.tabhost);
		tab = tabHost.newTabSpec("recent");
		tab.setContent(R.id.recently_used);
		tab.setIndicator(getString(R.string.recent), getResources()
				.getDrawable(android.R.drawable.ic_menu_recent_history));
		tabHost.addTab(tab);

		// 全アプリタブ
		tab = tabHost.newTabSpec("installed");
		tab.setContent(R.id.installed);
		tab.setIndicator(getString(R.string.installed), getResources()
				.getDrawable(android.R.drawable.ic_menu_share));
		tabHost.addTab(tab);

		tabHost.setCurrentTab(0);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mProgressBar = inflater.inflate(R.layout.progress_bar, null);

		mInstalledAdapter = new AppDataAdapter(this, R.layout.application_view);
		ListView listView = (ListView) findViewById(R.id.installed_list);
		listView.addFooterView(mProgressBar);
		listView.setAdapter(mInstalledAdapter);
		listView.setOnItemClickListener(mEventImpl);

		mHistoryAdapter = new AppDataAdapter(this, R.layout.application_view);
		listView = (ListView) findViewById(R.id.history_list);
		listView.addFooterView(mProgressBar);
		listView.setAdapter(mHistoryAdapter);
		listView.setOnItemClickListener(mEventImpl);

		mRecentAdapter = new AppDataAdapter(this, R.layout.application_view);
		listView = (ListView) findViewById(R.id.recent_list);
		listView.addFooterView(mProgressBar);
		listView.setAdapter(mRecentAdapter);
		listView.setOnItemClickListener(mEventImpl);

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
	private boolean mClearFlag = false;

	private void constructCache(boolean clearFlg) {
		mClearFlag = clearFlg;

		mInstalledFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				ListView listView = (ListView) findViewById(R.id.installed_list);
				listView.removeFooterView(mProgressBar);
				setProgressBarIndeterminateVisibility(false);
			}
		};

		mHistoryFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				ListView listView = (ListView) findViewById(R.id.history_list);
				listView.removeFooterView(mProgressBar);
				new DroppRecentlyUsedAsynkTask(DroppShareActivity.this,
						mRecentAdapter, mRecentFunc).execute();
			}
		};

		mRecentFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				ListView listView = (ListView) findViewById(R.id.recent_list);
				listView.removeFooterView(mProgressBar);
				new DroppInstalledAsynkTask(DroppShareActivity.this,
						mInstalledAdapter, mInstalledFunc).execute(mClearFlag);
			}
		};

		new DroppHistoryAsynkTask(DroppShareActivity.this, mHistoryAdapter,
				mHistoryFunc).execute();
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
