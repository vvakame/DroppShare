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
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

public class DroppRecentlyUsedAsynkTask extends
		AsyncTask<Void, Void, List<AppData>> {
	private static final String TAG = DroppRecentlyUsedAsynkTask.class
			.getSimpleName();

	private static final int MAX_NUM = 30;

	private Context mContext = null;
	private Func<List<AppData>> mFunc = null;

	public DroppRecentlyUsedAsynkTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context;
		mFunc = postExecFunc;
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onPreExecute();
	}

	@Override
	protected List<AppData> doInBackground(Void... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RecentTaskInfo> taskInfoList = am.getRecentTasks(MAX_NUM,
				ActivityManager.RECENT_WITH_EXCLUDED);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", get="
				+ (taskInfoList == null ? -1 : taskInfoList.size()));

		List<AppData> appDataList = new ArrayList<AppData>();
		for (RecentTaskInfo taskInfo : taskInfoList) {
			String packageName = taskInfo.baseIntent.getComponent()
					.getPackageName();
			AppData appData = null;
			try {
				appData = AppDataUtil.convert(mContext, packageName);
			} catch (NameNotFoundException e) {
				// ただ単に握りつぶす
			}
			if (appData != null) {
				appDataList.add(appData);
			}
		}

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", get="
				+ appDataList.size());

		return appDataList;
	}

	@Override
	protected void onPostExecute(List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", size="
				+ (appDataList == null ? -1 : appDataList.size()));
		super.onPostExecute(appDataList);

		mFunc.func(appDataList);
	}
}
