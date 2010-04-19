package net.vvakame.droppshare.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DroppInstalledAsynkTask extends DroppBaseAsynkTask {
	private static final String TAG = DroppInstalledAsynkTask.class
			.getSimpleName();

	private static final int MODE_NEW = 0;
	private static final int MODE_CACHE = 1;

	public static final String CACHE_FILE = "installed.dropp";

	private Comparator<AppData> mComparator = new Comparator<AppData>() {
		@Override
		public int compare(AppData obj1, AppData obj2) {
			return obj1.getAppName().compareToIgnoreCase(obj2.getAppName());
		}
	};

	private int mMode = MODE_CACHE;

	public DroppInstalledAsynkTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
	}

	public DroppInstalledAsynkTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super(context, postExecFunc);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", clear cache:"
				+ (params.length == 1 ? params[0].toString() : "none"));

		List<AppData> appDataList = null;

		appDataList = tryReadCache(CACHE_FILE, params);

		if (appDataList == null) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName()
					+ ", create cache.");
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

				AppData appData = new AppData();

				appData.setAppName(appInfo.loadLabel(pm));
				appData.setPackageName(appInfo.packageName);
				appData.setDescription(appInfo.loadDescription(pm));

				PackageInfo pInfo = null;
				try {
					pInfo = pm.getPackageInfo(appInfo.packageName,
							PackageManager.GET_ACTIVITIES);
				} catch (NameNotFoundException e) {
					Log.d(TAG, HelperUtil.getExceptionLog(e));
				}
				appData.setVersionName(pInfo.versionName);

				Drawable icon = pm.getApplicationIcon(appInfo);

				if (icon instanceof BitmapDrawable) {
					Bitmap resizedBitmap = AppDataUtil
							.getResizedBitmapDrawable(((BitmapDrawable) icon)
									.getBitmap());
					icon = new BitmapDrawable(resizedBitmap);
				} else {
					Log.d(TAG, "Not supported icon type: "
							+ icon.getClass().getSimpleName());
				}
				appData.setIcon(icon);

				publishProgress(appData);

				appDataList.add(appData);
			}

			Collections.sort(appDataList, mComparator);

			AppDataUtil.writeSerializedCache(mContext, CACHE_FILE, appDataList);
		}

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", done it!");

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
}
