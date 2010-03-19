package net.vvakame.droppshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

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
			Toast.makeText(context, packageName + " " + action,
					Toast.LENGTH_LONG).show();
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			Toast.makeText(context, packageName + " " + action,
					Toast.LENGTH_LONG).show();
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			Toast.makeText(context, packageName + " " + action,
					Toast.LENGTH_LONG).show();
		}
	}
}
