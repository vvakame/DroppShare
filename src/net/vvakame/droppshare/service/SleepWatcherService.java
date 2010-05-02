package net.vvakame.droppshare.service;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.receiver.SleepSignalReceiver;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 画面ON/OFFのReceiverを蹴ったり止めたりするService
 * 
 * @author vvakame
 */
public class SleepWatcherService extends Service implements LogTagIF {

	public static final String REGIST_FLG = "regist_flg";

	private SleepSignalReceiver mReceiver = new SleepSignalReceiver();

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, HelperUtil.getStackName());

		boolean registFlg = intent.getBooleanExtra(REGIST_FLG, false);
		if (registFlg) {
			IntentFilter filter = null;
			filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			registerReceiver(mReceiver, filter);
		} else {
			try {
				unregisterReceiver(mReceiver);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, HelperUtil.getExceptionLog(e));
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, HelperUtil.getStackName());

		unregisterReceiver(mReceiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 使わないので適当実装
		Log.d(TAG, HelperUtil.getStackName());

		return null;
	}
}
