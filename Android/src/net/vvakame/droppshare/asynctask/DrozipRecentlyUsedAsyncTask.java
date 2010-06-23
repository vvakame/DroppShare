package net.vvakame.droppshare.asynctask;

import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.model.AppData;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * 最近使ったアプリ一覧読み取り用AsyncTask<BR>
 * 水物なのでキャッシュ化は行わない
 * 
 * @author vvakame
 */
public class DrozipRecentlyUsedAsyncTask extends DrozipBaseAsyncTask implements
		LogTagIF {

	/** 表示するアプリの最大量 */
	private static final int MAX_NUM = 30;

	public DrozipRecentlyUsedAsyncTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	@Deprecated
	public DrozipRecentlyUsedAsyncTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super(context, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, HelperUtil.getStackName());

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Activity.ACTIVITY_SERVICE);
		PackageManager pm = mContext.getPackageManager();
		List<RecentTaskInfo> taskInfoList = am.getRecentTasks(MAX_NUM,
				ActivityManager.RECENT_WITH_EXCLUDED);

		Log.d(TAG, HelperUtil.getStackName() + ", get="
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

		Log.d(TAG, HelperUtil.getStackName() + ", get=" + appDataList.size());

		return appDataList;
	}
}
