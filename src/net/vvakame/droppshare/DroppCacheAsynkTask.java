package net.vvakame.droppshare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class DroppCacheAsynkTask extends AsyncTask<Void, Void, List<AppData>> {
	private static final String TAG = DroppCacheAsynkTask.class.getSimpleName();

	private Activity mActivity = null;
	private PackageManager mPm = null;
	private ProgressDialog mProgDialog = null;
	private List<AppData> mAppDataList = null;

	public DroppCacheAsynkTask(Activity activity) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mActivity = activity;
		mProgDialog = new FunnyProgressDialog(mActivity);
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onPreExecute();

		mProgDialog
				.setTitle(mActivity.getString(R.string.now_reading_app_data));
		mProgDialog.setMessage(mActivity.getString(R.string.wait_a_moment));
		mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDialog.setCancelable(false);
		mProgDialog.show();
	}

	@Override
	protected List<AppData> doInBackground(Void... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		if (AppDataUtil.isExistCache(mActivity)) {
			mAppDataList = AppDataUtil.readSerializedCaches(mActivity);

			for (AppData appData : mAppDataList) {
				AppDataUtil.readIconCache(mActivity, appData);
			}
		} else {
			mAppDataList = new ArrayList<AppData>();

			mPm = mActivity.getPackageManager();
			List<ApplicationInfo> appInfoList = mPm
					.getInstalledApplications(PackageManager.GET_ACTIVITIES);

			for (ApplicationInfo appInfo : appInfoList) {
				Log.d(TAG, "now processing " + appInfo.packageName);

				// デフォルト系アプリを撥ねる
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					continue;
				}

				AppData appData = new AppData();

				appData.setAppName(appInfo.loadLabel(mPm));
				appData.setPackageName(appInfo.packageName);
				appData.setDescription(appInfo.loadDescription(mPm));

				PackageInfo pInfo = null;
				try {
					pInfo = mPm.getPackageInfo(appInfo.packageName,
							PackageManager.GET_ACTIVITIES);
				} catch (NameNotFoundException e) {
					// 握りつぶす
				}
				appData.setVersionName(pInfo.versionName);

				Drawable icon = mPm.getApplicationIcon(appInfo);

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

				mAppDataList.add(appData);
			}

			Collections.sort(mAppDataList, new Comparator<AppData>() {
				@Override
				public int compare(AppData obj1, AppData obj2) {
					return obj1.getAppName().compareTo(obj2.getAppName());
				}
			});

			for (AppData appData : mAppDataList) {
				AppDataUtil.writeIconCache(mActivity, appData);
			}
			AppDataUtil.writeSerializedCache(mActivity, mAppDataList);
		}

		return mAppDataList;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onPostExecute(appDataList);

		mProgDialog.dismiss();
	}
}
