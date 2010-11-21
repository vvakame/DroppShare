package net.vvakame.droppshare.receiver;

import java.util.Date;

import net.vvakame.android.helper.Log;
import net.vvakame.droppshare.common.SerializeUtil;
import net.vvakame.droppshare.model.InstallLogDao;
import net.vvakame.droppshare.model.InstallLogModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * パッケージ操作に関わるBroadcastを受けるReceiver
 * 
 * @author vvakame
 */
public class PackageOperationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Log.getStackName() + ", " + intent.getAction());

		String action = intent.getAction();
		if (!Intent.ACTION_PACKAGE_ADDED.equals(action)
				&& !Intent.ACTION_PACKAGE_REMOVED.equals(action)
				&& !Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			return;
		}

		String packageName = intent.getData().getSchemeSpecificPart();
		Log.d(Log.getStackName() + ", " + packageName);

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
	}
}
