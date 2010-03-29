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
import android.os.AsyncTask;
import android.util.Log;

public class DroppHistoryAsynkTask extends AsyncTask<Void, Void, List<AppData>> {
	private static final String TAG = DroppHistoryAsynkTask.class
			.getSimpleName();

	private Context mContext = null;
	private Func<List<AppData>> mFunc = null;

	public DroppHistoryAsynkTask(Context context,
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

		InstallLogDao dao = new InstallLogDao(mContext);
		List<InstallLogModel> installedList = dao.list();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", get="
				+ (installedList == null ? -1 : installedList.size()));

		List<AppData> appDataList = new ArrayList<AppData>();
		for (InstallLogModel model : installedList) {
			AppData appData = null;
			try {
				appData = AppDataUtil.convert(mContext, model);
			} catch (NameNotFoundException e) {
				// 握り潰す 単に表示しない
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
