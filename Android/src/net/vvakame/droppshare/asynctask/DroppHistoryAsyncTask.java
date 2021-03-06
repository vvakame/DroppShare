package net.vvakame.droppshare.asynctask;

import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.CacheUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.InstallLogDao;
import net.vvakame.droppshare.model.InstallLogModel;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * インストールログ読み取り用AsyncTask
 * 
 * @author vvakame
 */
public class DroppHistoryAsyncTask extends DroppBaseAsyncTask implements
		LogTagIF {

	public static final String CACHE_FILE = "history.dropp";

	public DroppHistoryAsyncTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	public DroppHistoryAsyncTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super(context, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d(TAG, HelperUtil.getStackName());

		List<AppData> appDataList = null;

		appDataList = tryReadCache(CACHE_FILE, params);

		if (appDataList == null) {
			Log.d(TAG, HelperUtil.getStackName() + ", create cache.");
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

			CacheUtil.writeSerializedCache(mContext, CACHE_FILE, appDataList);
		}

		Log.d(TAG, HelperUtil.getStackName() + ", get=" + appDataList.size());

		return appDataList;
	}
}
