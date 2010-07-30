package net.vvakame.droppshare.appshare;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.vvakame.android.helper.Func;
import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.common.AppData;
import net.vvakame.droppshare.common.AppDataUtil;
import net.vvakame.droppshare.common.LogTagIF;
import net.vvakame.droppshare.common.SerializeUtil;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * 全アプリ一覧読み取り用AsyncTask
 * 
 * @author vvakame
 */
public class DrozipInstalledAsyncTask extends DrozipBaseAsyncTask implements
		LogTagIF {

	private static final int MODE_NEW = 0;
	private static final int MODE_CACHE = 1;

	public static final File CACHE_FILE = new File(SerializeUtil.CACHE_DIR,
			"installed.dropp");
	public File cacheFile = CACHE_FILE;

	private Comparator<AppData> mComparator = new Comparator<AppData>() {
		@Override
		public int compare(AppData obj1, AppData obj2) {
			return obj1.getAppName().compareToIgnoreCase(obj2.getAppName());
		}
	};

	private int mMode = MODE_CACHE;

	public DrozipInstalledAsyncTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	public DrozipInstalledAsyncTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super(context, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, HelperUtil.getStackName() + ", clear cache:"
				+ (params.length == 1 ? params[0].toString() : "none"));

		List<AppData> appDataList = null;

		appDataList = tryReadCache(cacheFile, params);

		if (appDataList == null) {
			Log.d(TAG, HelperUtil.getStackName() + ", create cache.");
			mMode = MODE_NEW;
			appDataList = new ArrayList<AppData>();

			PackageManager pm = mContext.getPackageManager();
			List<ApplicationInfo> appInfoList = pm
					.getInstalledApplications(PackageManager.GET_ACTIVITIES);

			for (ApplicationInfo appInfo : appInfoList) {
				Log.d(TAG, "now processing " + appInfo.packageName);

				// デフォルト系アプリを撥ねる
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					continue;
				}

				PackageInfo pInfo = null;
				AppData appData = null;
				try {
					pInfo = pm.getPackageInfo(appInfo.packageName,
							PackageManager.GET_ACTIVITIES);
					appData = AppDataUtil.convert(mContext, pm, pInfo, appInfo);
				} catch (NameNotFoundException e) {
					Log.d(TAG, HelperUtil.getExceptionLog(e));
					continue;
				}

				publishProgress(appData);

				appDataList.add(appData);
			}

			Collections.sort(appDataList, mComparator);

			SerializeUtil
					.writeSerializedCache(mContext, cacheFile, appDataList);
		}

		Log.d(TAG, HelperUtil.getStackName() + ", done it!");

		return appDataList;
	}

	@Override
	protected void onProgressUpdate(AppData... values) {
		if (mAdapter != null && values.length == 1) {
			mAdapter.add(values[0]);
			if (mMode == MODE_NEW) {
				mAdapter.sort(mComparator);
			}
		}
	}

	public File getCatcheFile() {
		return cacheFile;
	}

	public void setCatcheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}
}
