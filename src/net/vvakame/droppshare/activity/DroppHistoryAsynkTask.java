package net.vvakame.droppshare.activity;

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
import android.util.Log;
import android.widget.ArrayAdapter;

public class DroppHistoryAsynkTask extends DroppBaseAsynkTask {
	private static final String TAG = DroppHistoryAsynkTask.class
			.getSimpleName();

	public static final String CACHE_FILE = "history.dropp";

	public DroppHistoryAsynkTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
	}

	public DroppHistoryAsynkTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super(context, postExecFunc);

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		List<AppData> appDataList = null;

		appDataList = tryReadCache(CACHE_FILE, params);

		if (appDataList == null) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName()
					+ ", create cache.");
			InstallLogDao dao = new InstallLogDao(mContext);
			List<InstallLogModel> installedList = dao.list();

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
}
