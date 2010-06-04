package net.vvakame.droppshare.service;

import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.asynctask.DroppHistoryAsyncTask;
import net.vvakame.droppshare.asynctask.DroppInstalledAsyncTask;
import net.vvakame.droppshare.helper.CacheUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.model.AppData;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * バックグラウンドでキャッシュを作成するためのService
 * 
 * @author vvakame
 */
public class CreateCacheService extends Service implements LogTagIF {

	private DroppInstalledAsyncTask mInstalledAsyncTask = null;
	private DroppHistoryAsyncTask mHistoryAsyncTask = null;

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, HelperUtil.getStackName());

		if (CacheUtil.isExistCache(DroppInstalledAsyncTask.CACHE_FILE)) {
			// Installedのキャッシュがあれば両方あるということにしちゃう
			return;
		}
		mHistoryAsyncTask = new DroppHistoryAsyncTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
						mInstalledAsyncTask.execute(false);
					}
				});

		mInstalledAsyncTask = new DroppInstalledAsyncTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
						// キャッシュ作成処理終了後にあっちのサービスを終わらせてやる
						Log.d(TAG, HelperUtil.getStackName() + ", Kill "
								+ SleepWatcherService.class.getSimpleName());
						Intent intent = new Intent(CreateCacheService.this,
								SleepWatcherService.class);
						intent.putExtra(SleepWatcherService.REGIST_FLG, false);
						stopService(intent);
						CreateCacheService.this.stopSelf();
					}
				});

		mHistoryAsyncTask.execute(false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, HelperUtil.getStackName());

		// 使わないので適当実装
		return null;
	}
}