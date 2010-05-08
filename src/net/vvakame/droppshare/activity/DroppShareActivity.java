package net.vvakame.droppshare.activity;

import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.asynctask.DroppHistoryAsyncTask;
import net.vvakame.droppshare.asynctask.DroppInstalledAsyncTask;
import net.vvakame.droppshare.asynctask.DroppRecentlyUsedAsyncTask;
import net.vvakame.droppshare.helper.AppDataAdapter;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.helper.SimejiIF;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.util.shorten.GoogleShorten;
import net.vvakame.util.shorten.ShortenAgent;
import net.vvakame.util.shorten.ShortenFactory;
import net.vvakame.util.shorten.ShortenFailedException;
import android.app.Activity;
import android.app.LocalActivityManager;
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
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * アプリ一覧からみんなと共有したいアプリを選ぶActivity
 * 
 * @author vvakame
 */
public class DroppShareActivity extends Activity implements LogTagIF, SimejiIF {

	public static final int SEND = 0;
	public static final int MENU_DIALOG = 1;

	private AppDataAdapter mInstalledAdapter = null;
	private AppDataAdapter mHistoryAdapter = null;
	private AppDataAdapter mRecentAdapter = null;
	private View mProgressBar = null;
	private View mAppNotExistView = null;

	private OnItemClickListener mClickEventImpl = null;
	private OnItemLongClickListener mLongClickEventImpl = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);

		setProgressBarIndeterminateVisibility(true);

		// タブの準備
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup(new LocalActivityManager(this, true));
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

		EventNormalImpl impl = null;
		if (isCalledBySimeji()) {
			impl = new EventSimejiImpl();
		} else {
			impl = new EventNormalImpl();
		}
		mClickEventImpl = impl;
		mLongClickEventImpl = impl;

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mProgressBar = inflater.inflate(R.layout.progress_bar, null);
		mAppNotExistView = inflater.inflate(R.layout.app_not_exists, null);

		mInstalledAdapter = new AppDataAdapter(this, R.layout.installed_view);
		ListView listView = (ListView) findViewById(R.id.installed_list);
		listView.addFooterView(mProgressBar);
		listView.setAdapter(mInstalledAdapter);
		listView.setOnItemClickListener(mClickEventImpl);
		listView.setOnItemLongClickListener(mLongClickEventImpl);

		mHistoryAdapter = new AppDataAdapter(this, R.layout.history_view);
		listView = (ListView) findViewById(R.id.history_list);
		listView.addFooterView(mProgressBar);
		listView.setAdapter(mHistoryAdapter);
		listView.setOnItemClickListener(mClickEventImpl);
		listView.setOnItemLongClickListener(mLongClickEventImpl);

		mRecentAdapter = new AppDataAdapter(this, R.layout.recent_view);
		listView = (ListView) findViewById(R.id.recent_list);
		listView.addFooterView(mProgressBar);
		listView.setAdapter(mRecentAdapter);
		listView.setOnItemClickListener(mClickEventImpl);
		listView.setOnItemLongClickListener(mLongClickEventImpl);

		startReadingData(false);
	}

	@Override
	public void onResume() {
		Log.d(TAG, HelperUtil.getStackName());

		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MENU_DIALOG && resultCode == RESULT_OK) {
			int result = data.getIntExtra(MenuDialogActivity.RESULT, -1);
			AppData appData = (AppData) data
					.getSerializableExtra(MenuDialogActivity.APP_DATA);

			switch (result) {
			case R.id.http:
			case R.id.market:
			case R.id.googl:
				String message = genPassionateMessage(appData, result);
				if (isCalledBySimeji()) {
					pushToSimeji(message);
				} else {
					callSender(message);
				}

				break;

			default:
				break;
			}
		}
	}

	private Func<List<AppData>> mInstalledFunc = null;
	private Func<List<AppData>> mHistoryFunc = null;
	private Func<List<AppData>> mRecentFunc = null;
	private boolean mClearFlag = false;

	private void startReadingData(boolean clearFlg) {
		mClearFlag = clearFlg;

		setProgressBarIndeterminateVisibility(true);

		mInstalledAdapter.clear();
		ListView listView = (ListView) findViewById(R.id.installed_list);
		if (listView.getAdapter() instanceof HeaderViewListAdapter) {
			listView.removeFooterView(mProgressBar);
		}
		listView.addFooterView(mProgressBar);
		listView.setOnItemClickListener(mClickEventImpl);

		mHistoryAdapter.clear();
		listView = (ListView) findViewById(R.id.history_list);
		if (listView.getAdapter() instanceof HeaderViewListAdapter) {
			listView.removeFooterView(mProgressBar);
		}
		listView.addFooterView(mProgressBar);
		listView.setOnItemClickListener(mClickEventImpl);

		mRecentAdapter.clear();
		listView = (ListView) findViewById(R.id.recent_list);
		if (listView.getAdapter() instanceof HeaderViewListAdapter) {
			listView.removeFooterView(mProgressBar);
		}
		listView.addFooterView(mProgressBar);
		listView.setOnItemClickListener(mClickEventImpl);

		mInstalledFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				ListView listView = (ListView) findViewById(R.id.installed_list);
				if (listView.getAdapter() instanceof HeaderViewListAdapter) {
					listView.removeFooterView(mProgressBar);
				}
				if (arg == null || arg.size() == 0) {
					listView.addFooterView(mAppNotExistView, null, false);
				}
				setProgressBarIndeterminateVisibility(false);
			}
		};

		mHistoryFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				ListView listView = (ListView) findViewById(R.id.history_list);
				if (listView.getAdapter() instanceof HeaderViewListAdapter) {
					listView.removeFooterView(mProgressBar);
				}
				if (arg == null || arg.size() == 0) {
					listView.addFooterView(mAppNotExistView);
				}
				new DroppRecentlyUsedAsyncTask(DroppShareActivity.this,
						mRecentAdapter, mRecentFunc).execute();
			}
		};

		mRecentFunc = new Func<List<AppData>>() {
			@Override
			public void func(List<AppData> arg) {
				ListView listView = (ListView) findViewById(R.id.recent_list);
				if (listView.getAdapter() instanceof HeaderViewListAdapter) {
					listView.removeFooterView(mProgressBar);
				}
				if (arg == null || arg.size() == 0) {
					listView.addFooterView(mAppNotExistView);
				}
				new DroppInstalledAsyncTask(DroppShareActivity.this,
						mInstalledAdapter, mInstalledFunc).execute(mClearFlag);
			}
		};

		new DroppHistoryAsyncTask(DroppShareActivity.this, mHistoryAdapter,
				mHistoryFunc).execute(mClearFlag);
	}

	@Override
	public void onPause() {
		Log.d(TAG, HelperUtil.getStackName());

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
			startReadingData(true);

			break;

		case R.id.show_selector:
			intent = new Intent(this, DroppSelectorActivity.class);
			startActivity(intent);
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

	private String genPassionateMessage(AppData appData, int... overrideOption) {
		int option = -1;
		if (overrideOption.length == 1) {
			option = overrideOption[0];
		}
		boolean http = option == R.id.http;
		boolean market = option == R.id.market;
		boolean googl = option == R.id.http;
		boolean hasOption = http || market || googl;

		String message = PreferencesActivity.getMessageTemplate(this);

		String uri = null;
		if (hasOption) {
			if (http) {
				uri = AppDataUtil.getHttpUriFromAppData(appData);
			} else if (googl) {
				uri = AppDataUtil.getHttpUriFromAppData(appData);
				try {
					uri = new GoogleShorten().getShorten(uri);
				} catch (ShortenFailedException e) {
					// こけた場合はどーしようか
					Toast.makeText(this, R.string.uri_shorten_failure,
							Toast.LENGTH_LONG);
				}
			} else {
				uri = AppDataUtil.getMarketUriFromAppData(appData);
			}
		} else {
			if (PreferencesActivity.isHttp(this)) {
				uri = AppDataUtil.getHttpUriFromAppData(appData);
				ShortenAgent shorten = ShortenFactory
						.getShortenAgent(PreferencesActivity
								.getUriShortenAgent(this));
				try {
					uri = shorten.getShorten(uri);
				} catch (ShortenFailedException e) {
					// こけた場合はどーしようか
					Toast.makeText(this, R.string.uri_shorten_failure,
							Toast.LENGTH_LONG);
				}
			} else {
				uri = AppDataUtil.getMarketUriFromAppData(appData);
			}
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

	private void callSender(String message) {
		Intent data = new Intent();
		data.setAction(Intent.ACTION_SEND);
		data.setType("text/plain");
		data.putExtra(Intent.EXTRA_TEXT, message);

		startActivityForResult(data, SEND);
	}

	private void pushToSimeji(String message) {
		Intent data = new Intent();
		data.putExtra(REPLACE_KEY, message);
		setResult(RESULT_OK, data);
		finish();
	}

	class EventNormalImpl implements OnItemClickListener,
			OnItemLongClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, HelperUtil.getStackName());

			AppDataAdapter adapter = pickAppDataAdapter(parent);
			if (adapter.getCount() <= 0) {
				return;
			}
			AppData appData = adapter.getItem(position);

			callSender(genPassionateMessage(appData));
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Log.d(TAG, HelperUtil.getStackName());

			AppDataAdapter adapter = pickAppDataAdapter(parent);
			if (adapter.getCount() <= 0) {
				return false;
			}
			AppData appData = adapter.getItem(position);

			Intent intent = new Intent(DroppShareActivity.this,
					MenuDialogActivity.class);
			intent.putExtra(MenuDialogActivity.APP_DATA, appData);
			startActivityForResult(intent, MENU_DIALOG);

			return true;
		}
	}

	class EventSimejiImpl extends EventNormalImpl {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, HelperUtil.getStackName());
			AppDataAdapter adapter = pickAppDataAdapter(parent);
			if (adapter.getCount() <= 0) {
				return;
			}
			AppData appData = adapter.getItem(position);

			pushToSimeji(genPassionateMessage(appData));
		}
	}

	private AppDataAdapter pickAppDataAdapter(AdapterView<?> parent) {
		AppDataAdapter adapter;
		Object adapterObj = parent.getAdapter();
		if (adapterObj instanceof HeaderViewListAdapter) {
			HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) adapterObj;
			adapter = (AppDataAdapter) headerAdapter.getWrappedAdapter();
		} else {
			adapter = (AppDataAdapter) adapterObj;
		}
		return adapter;
	}
}
