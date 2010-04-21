package net.vvakame.droppshare.receiver;

import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.service.CreateCacheService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SleepSignalReceiver extends BroadcastReceiver {
	private static final String TAG = SleepSignalReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
				+ intent.getAction());

		String action = intent.getAction();
		if (Intent.ACTION_SCREEN_OFF.equals(action)) {
			// 画面がOFFになった場合、こっそり処理させたい。

			Intent newIntent = new Intent(context, CreateCacheService.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0,
					newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmMgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + 25000, pIntent);
		} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
			// もし画面がOFFになってすぐONにされたら、意図しないOFFだろうからキャッシュ生成しない
			Intent newIntent = new Intent(context, CreateCacheService.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0,
					newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			pIntent.cancel();
		}
	}
}
