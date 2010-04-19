package net.vvakame.droppshare.activity;

import java.util.Date;

import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.InstallLogDao;
import net.vvakame.droppshare.model.InstallLogModel;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class DroppShareReceiver extends BroadcastReceiver {
	private static final String TAG = DroppShareReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
				+ intent.getAction());

		String action = intent.getAction();

		boolean packageOpe = false;
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			packageOpe = true;
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			packageOpe = true;
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			packageOpe = true;
		} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
			// 画面がOFFになった場合、こっそり処理させたい。

			Intent newIntent = new Intent(context, DroppCacheService.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0,
					newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmMgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + 25000, pIntent);
		} else if (Intent.ACTION_SCREEN_ON.equals(action)) {
			// もし画面がOFFになってすぐONにされたら、意図しないOFFだろうからキャッシュ生成しない
			Intent newIntent = new Intent(context, DroppCacheService.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0,
					newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			pIntent.cancel();
		}

		// パッケージ関係のアクションの場合
		if (packageOpe) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName()
					+ ", package info was changed.");

			String packageName = intent.getData().getSchemeSpecificPart();
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
					+ packageName);

			PackageInfo pInfo = null;
			ApplicationInfo appInfo = null;
			try {
				pInfo = context.getPackageManager().getPackageInfo(packageName,
						PackageManager.GET_UNINSTALLED_PACKAGES);
			} catch (NameNotFoundException e) {
				// 握りつぶす
				return;
			}
			appInfo = pInfo.applicationInfo;

			// DEBUGGABLEなアプリだったら無視する(開発中に反応するとうざいので)
			// AndroidManifest,xmlで明示的に設定されていないと役にたたないけど。
			if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
				Log.d(TAG, TAG + ":" + HelperUtil.getMethodName()
						+ " Target is DEBUGGABLE!");
				return;
			}

			// キャッシュ再構成のためとりあえず消しておく
			// TODO テストが終わったらコメントアウト解除すること
			AppDataUtil.deleteOwnCache();

			// 操作されたパッケージの情報をDBに書く
			// 一意性制約とconflict時の挙動の定義で同一アプリ同一バージョンの情報が重複しないようにしている
			String versionName = null;
			try {
				versionName = context.getPackageManager().getPackageInfo(
						appInfo.packageName, PackageManager.GET_ACTIVITIES).versionName;
			} catch (NameNotFoundException e) {
				// 握りつぶす
			}
			InstallLogModel model = new InstallLogModel();
			model.setPackageName(packageName);
			model.setVersionName(versionName);
			model.setActionType(action);
			model.setProcessDate(new Date());
			InstallLogDao dao = new InstallLogDao(context);
			dao.save(model);
			// dao.compress();
		}
	}
}
