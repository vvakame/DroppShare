package net.vvakame.droppshare.activity;

import java.util.List;

import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DroppCacheService extends Service {
	private static final String TAG = DroppCacheService.class.getSimpleName();

	private DroppInstalledAsynkTask mInstalledAsyncTask = null;
	private DroppHistoryAsynkTask mHistoryAsyncTask = null;

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		if (AppDataUtil.isExistCache(DroppInstalledAsynkTask.CACHE_FILE)) {
			// Installedのキャッシュがあれば両方あるということにしちゃう
			return;
		}
		mHistoryAsyncTask = new DroppHistoryAsynkTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
						mInstalledAsyncTask.execute(false);
					}
				});

		mInstalledAsyncTask = new DroppInstalledAsynkTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
					}
				});

		mHistoryAsyncTask.execute(false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 使わないので適当実装
		return null;
	}
}
