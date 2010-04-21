package net.vvakame.droppshare.service;

import java.util.List;

import net.vvakame.droppshare.asynctask.DroppHistoryAsynkTask;
import net.vvakame.droppshare.asynctask.DroppInstalledAsynkTask;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CreateCacheService extends Service {
	private static final String TAG = CreateCacheService.class.getSimpleName();

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
						// キャッシュ作成処理終了後にあっちのサービスを終わらせてやる
						Log.d(TAG, TAG + ": Kill "
								+ SleepWatcherService.class.getSimpleName()
								+ ".");
						Intent intent = new Intent(CreateCacheService.this,
								SleepWatcherService.class);
						intent.putExtra(SleepWatcherService.REGIST_FLG, false);
						stopService(intent);
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
