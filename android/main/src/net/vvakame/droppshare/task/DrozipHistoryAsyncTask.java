package net.vvakame.droppshare.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.Func;
import net.vvakame.android.helper.Log;
import net.vvakame.droppshare.common.SerializeUtil;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDataUtil;
import net.vvakame.droppshare.model.InstallLogDao;
import net.vvakame.droppshare.model.InstallLogModel;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.ArrayAdapter;

/**
 * インストールログ読み取り用AsyncTask
 * 
 * @author vvakame
 */
public class DrozipHistoryAsyncTask extends DrozipBaseAsyncTask {

	public static final File CACHE_FILE = new File(SerializeUtil.CACHE_DIR,
			"history.dropp");
	public File cacheFile = CACHE_FILE;

	public DrozipHistoryAsyncTask(Context context,
			ArrayAdapter<AppData> adapter, Func<List<AppData>> postExecFunc) {
		super(context, adapter, postExecFunc);

		Log.d();
	}

	public DrozipHistoryAsyncTask(Context context,
			Func<List<AppData>> postExecFunc) {
		super(context, postExecFunc);

		Log.d();
	}

	@Override
	protected List<AppData> doInBackground(Boolean... params) {
		Log.d();

		List<AppData> appDataList = null;

		appDataList = tryReadCache(cacheFile, params);

		if (appDataList == null) {
			Log.d(Log.getStackName() + ", create cache.");
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

			SerializeUtil
					.writeSerializedCache(mContext, cacheFile, appDataList);
		}

		Log.d(Log.getStackName() + ", get=" + appDataList.size());

		return appDataList;
	}
}
