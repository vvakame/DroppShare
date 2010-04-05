package net.vvakame.droppshare.activity;

import java.util.ArrayList;
import java.util.List;

import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DroppRecentlyUsedAsynkTask extends
		AsyncTask<Void, AppData, List<AppData>> {
	private static final String TAG = DroppRecentlyUsedAsynkTask.class
			.getSimpleName();

	private static final int MAX_NUM = 30;

	private Context mContext = null;
	private ArrayAdapter<AppData> mAdapter = null;
	private Func<List<AppData>> mFunc = null;

	public DroppRecentlyUsedAsynkTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context.getApplicationContext();
		mAdapter = adapter;
		mFunc = postExecFunc;
	}

	@Deprecated
	public DroppRecentlyUsedAsynkTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context.getApplicationContext();
		mFunc = postExecFunc;
	}

	@Override
	protected List<AppData> doInBackground(Void... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Activity.ACTIVITY_SERVICE);
		PackageManager pm = mContext.getPackageManager();
		List<RecentTaskInfo> taskInfoList = am.getRecentTasks(MAX_NUM,
				ActivityManager.RECENT_WITH_EXCLUDED);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", get="
				+ (taskInfoList == null ? -1 : taskInfoList.size()));

		List<AppData> appDataList = new ArrayList<AppData>();
		for (RecentTaskInfo taskInfo : taskInfoList) {
			String packageName = taskInfo.baseIntent.getComponent()
					.getPackageName();
			ApplicationInfo appInfo = null;
			try {
				appInfo = pm.getApplicationInfo(packageName,
						PackageManager.GET_UNINSTALLED_PACKAGES);
			} catch (NameNotFoundException e) {
				// 見つからなかったらこの後もどうせ続かないので諦める
				continue;
			}

			// デフォルト系アプリを撥ねる
			if (appInfo != null
					&& (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				continue;
			}

			AppData appData = null;
			try {
				appData = AppDataUtil.convert(mContext, packageName);
			} catch (NameNotFoundException e) {
				// ただ単に握りつぶす
			}
			if (appData != null) {
				publishProgress(appData);
				appDataList.add(appData);
			}
		}

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", get="
				+ appDataList.size());

		return appDataList;
	}

	@Override
	protected void onProgressUpdate(AppData... values) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", appData="
				+ values);

		super.onProgressUpdate(values);

		if (mAdapter != null && values.length == 1) {
			mAdapter.add(values[0]);
		}
	}

	@Override
	protected void onPostExecute(List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", size="
				+ (appDataList == null ? -1 : appDataList.size()));
		super.onPostExecute(appDataList);

		mFunc.func(appDataList);
	}
}
