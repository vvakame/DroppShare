package net.vvakame.droppshare.activity;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.util.AppDataUtil;
import net.vvakame.droppshare.util.HelperUtil;
import net.vvakame.droppshare.view.FunnyProgressDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class DroppCacheAsynkTask extends
		AsyncTask<Boolean, Void, List<AppData>> {
	private static final String TAG = DroppCacheAsynkTask.class.getSimpleName();

	private Context mContext = null;
	private Func<List<AppData>> mFunc = null;

	private ProgressDialog mProgDialog = null;

	private static Object lock = new Object();

	public DroppCacheAsynkTask(Context context, Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context;
		mFunc = postExecFunc;

		mProgDialog = new FunnyProgressDialog(mContext);
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onPreExecute();

		mProgDialog.setTitle(mContext.getString(R.string.now_reading_app_data));
		mProgDialog.setMessage(mContext.getString(R.string.wait_a_moment));
		mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDialog.setCancelable(false);
		mProgDialog.show();
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", clear cache:"
				+ (params.length == 1 ? params[0].toString() : "none"));

		List<AppData> appDataList = null;

		// 画面の縦横変更(=Activity再生成)で複数の場所から同時に入ってくる可能性があるのでロックする
		synchronized (lock) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", lock in!");

			boolean clearFlg = params.length == 1
					&& params[0].booleanValue() == true;
			boolean done = false;

			if (!clearFlg && AppDataUtil.isExistCache(mContext)) {
				Log.d(TAG, TAG + ":" + HelperUtil.getMethodName()
						+ ", use cache.");
				try {
					appDataList = AppDataUtil.readSerializedCaches(mContext);
					done = true;
				} catch (InvalidClassException e) {
					// ここに来るのは、SerializeされたオブジェクトのserialVersionUIDが一致しないときに来る想定
					Log.d(TAG, HelperUtil.getExceptionLog(e));
				} catch (ClassNotFoundException e) {
					// ここに来るのは、Serializeされたオブジェクトのパッケージ名が変更になってたりしたときに来る想定
					Log.d(TAG, HelperUtil.getExceptionLog(e));
				}
			}
			if (!done) {
				Log.d(TAG, TAG + ":" + HelperUtil.getMethodName()
						+ ", create cache.");
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

					appDataList.add(appData);
				}

				Collections.sort(appDataList, new Comparator<AppData>() {
					@Override
					public int compare(AppData obj1, AppData obj2) {
						return obj1.getAppName().compareTo(obj2.getAppName());
					}
				});

				AppDataUtil.writeSerializedCache(mContext, appDataList);
			}

			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", done it!");
		}

		return appDataList;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		if (mProgDialog != null && mProgDialog.isShowing()) {
			mProgDialog.dismiss();
			mProgDialog = null;
		}
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onPostExecute(appDataList);

		mFunc.func(appDataList);
		mProgDialog.dismiss();
	}
}
