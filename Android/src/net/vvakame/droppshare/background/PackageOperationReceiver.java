package net.vvakame.droppshare.background;

import java.util.Date;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.appshare.InstallLogDao;
import net.vvakame.droppshare.appshare.InstallLogModel;
import net.vvakame.droppshare.common.LogTagIF;
import net.vvakame.droppshare.common.PreferencesActivity;
import net.vvakame.droppshare.common.SerializeUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * パッケージ操作に関わるBroadcastを受けるReceiver
 * 
 * @author vvakame
 */
public class PackageOperationReceiver extends BroadcastReceiver implements
		LogTagIF {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, HelperUtil.getStackName() + ", " + intent.getAction());

		String action = intent.getAction();
		if (!Intent.ACTION_PACKAGE_ADDED.equals(action)
				&& !Intent.ACTION_PACKAGE_REMOVED.equals(action)
				&& !Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			return;
		}

		String packageName = intent.getData().getSchemeSpecificPart();
		Log.d(TAG, HelperUtil.getStackName() + ", " + packageName);

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

		// キャッシュ再構成のためとりあえず消しておく
		SerializeUtil.deleteOwnCache();

		// 操作されたパッケージの情報をDBに書く
		// 一意性制約とconflict時の挙動の定義で同一アプリ同一バージョンの情報が重複しないようにしている
		int versionCode = -1;
		String versionName = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(
					appInfo.packageName, PackageManager.GET_ACTIVITIES);
			versionCode = pInfo.versionCode;
			versionName = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// 握りつぶす
		}
		InstallLogModel model = new InstallLogModel();
		model.setPackageName(packageName);
		model.setVersionCode(versionCode);
		model.setVersionName(versionName);
		model.setActionType(action);
		model.setProcessDate(new Date());
		InstallLogDao dao = new InstallLogDao(context);
		dao.save(model);

		// キャッシュ生成用サービスを作成
		boolean cacheFlg = PreferencesActivity.isAllowAutoCache(context);
		Log
				.d(TAG, HelperUtil.getStackName() + ", isAllowAutoCache="
						+ cacheFlg);
		if (cacheFlg) {
			Intent cacheIntent = new Intent(context, SleepWatcherService.class);
			cacheIntent.putExtra(SleepWatcherService.REGIST_FLG, cacheFlg);
			context.startService(cacheIntent);
		}
	}
}
