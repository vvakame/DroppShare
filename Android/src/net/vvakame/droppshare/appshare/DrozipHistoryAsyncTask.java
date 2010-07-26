package net.vvakame.droppshare.appshare;

import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.Func;
import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.common.AppData;
import net.vvakame.droppshare.common.AppDataUtil;
import net.vvakame.droppshare.common.LogTagIF;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * インストールログ読み取り用AsyncTask
 * 
 * @author vvakame
 */
public class DrozipHistoryAsyncTask extends DrozipBaseAsyncTask implements
		LogTagIF {

	public static final String CACHE_FILE = "history.dropp";

	public DrozipHistoryAsyncTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d(TAG, HelperUtil.getStackName());
	}

	public DrozipHistoryAsyncTask(Context context,
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
