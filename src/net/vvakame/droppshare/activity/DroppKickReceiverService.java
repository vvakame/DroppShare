package net.vvakame.droppshare.activity;

import net.vvakame.droppshare.helper.HelperUtil;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class DroppKickReceiverService extends Service {
	private static final String TAG = DroppKickReceiverService.class
			.getSimpleName();

	public static final String REGIST_FLG = "regist_flg";

	private DroppShareReceiver mReceiver = new DroppShareReceiver();

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

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
				Log.e(TAG, TAG + ":" + HelperUtil.getExceptionLog(e));
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 使わないので適当実装
		return null;
	}
}
