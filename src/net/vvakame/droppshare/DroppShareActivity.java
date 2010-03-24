package net.vvakame.droppshare;

import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class DroppShareActivity extends TabActivity {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private OnItemClickListener mEventImpl = null;

	private List<AppData> mAppDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		TabHost tabs = getTabHost();

		TabSpec tspec1 = tabs.newTabSpec("installedAppTab");
		tspec1.setIndicator(getString(R.string.installed));
		Intent tab1Intent = new Intent(getIntent());
		tab1Intent.setClass(this, TabContentActivity.class);
		tspec1.setContent(tab1Intent);
		tabs.addTab(tspec1);
	}

	@Override
	public void onResume() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onResume();
	}

	// TODO TabContentActivityに同一のコードがある
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
}
