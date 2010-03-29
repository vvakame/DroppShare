package net.vvakame.droppshare.activity;

import java.util.Date;

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

		String packageName = intent.getData().getSchemeSpecificPart();
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", " + packageName);

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

		String action = intent.getAction();

		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			// ADDの場合はキャッシュを再構成する
			Intent newIntent = new Intent(context, DroppCacheActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0,
					newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmMgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmMgr.set(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + 1000, pIntent);
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			// REMOVEの場合は何もしない 消したはずのものが表示されても困らないだろうから(不気味には思うかも？)
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			// REPLACEDの場合も何もしない 将来的にはVersionNameを再取得するためにキャッシュを作り直したほうがよいかもしれない
		}

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
