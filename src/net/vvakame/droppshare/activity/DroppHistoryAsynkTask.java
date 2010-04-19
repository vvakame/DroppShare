package net.vvakame.droppshare.activity;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.InstallLogDao;
import net.vvakame.droppshare.model.InstallLogModel;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DroppHistoryAsynkTask extends
		AsyncTask<Boolean, AppData, List<AppData>> {
	private static final String TAG = DroppHistoryAsynkTask.class
			.getSimpleName();

	public static final String CACHE_FILE = "history.dropp";

	private Context mContext = null;
	private ArrayAdapter<AppData> mAdapter = null;
	private Func<List<AppData>> mFunc = null;

	public DroppHistoryAsynkTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context.getApplicationContext();
		mAdapter = adapter;
		mFunc = postExecFunc;
	}

	public DroppHistoryAsynkTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context.getApplicationContext();
		mFunc = postExecFunc;
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		InstallLogDao dao = new InstallLogDao(mContext);
		List<InstallLogModel> installedList = dao.list();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", get="
				+ (installedList == null ? -1 : installedList.size()));

		List<AppData> appDataList = null;

		boolean clearFlg = params.length == 1
				&& params[0].booleanValue() == true;
		boolean done = false;

		if (!clearFlg && AppDataUtil.isExistCache(CACHE_FILE)) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", use cache.");
			try {
				appDataList = AppDataUtil.readSerializedCaches(CACHE_FILE);
				for (AppData appData : appDataList) {
					publishProgress(appData);
				}
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
			for (InstallLogModel model : installedList) {
				AppData appData = null;
				try {
					appData = AppDataUtil.convert(mContext, model);
				} catch (NameNotFoundException e) {
					// 握り潰す 単に表示しない
				}
				if (appData != null) {
					publishProgress(appData);
					appDataList.add(appData);
				}
			}

			AppDataUtil.writeSerializedCache(mContext, CACHE_FILE, appDataList);
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
