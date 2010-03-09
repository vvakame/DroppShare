package jp.ne.hatena.vvakame.droppshare;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class DroppShareService extends Service implements IDroppDataService {
	private static final String TAG = DroppShareService.class.getSimpleName();

	private List<AppData> appDataList = null;

	private PackageManager mPm = null;
	private NotificationManager mNm = null;

	@Override
	public void onCreate() {
		super.onCreate();

		mPm = getPackageManager();
		List<ApplicationInfo> appInfoList = mPm
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);
		mNm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		appDataList = new ArrayList<AppData>();
		for (ApplicationInfo appInfo : appInfoList) {
			AppData appData = AppData.convApplicationInfo(mPm, appInfo);
			appDataList.add(appData);
		}

		Collections.sort(appDataList, new Comparator<AppData>() {
			@Override
			public int compare(AppData obj1, AppData obj2) {
				return obj1.getAppName().compareTo(obj2.getAppName());
			}
		});

		Notification notification = new Notification(R.drawable.icon, "test",
				System.currentTimeMillis());
		// TODO 引数の見直し
		startForegroundCompat(1, notification);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		// TODO 引数の見直し
		stopForegroundCompat(1);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		return mBinder;
	}

	@Override
	public void onLowMemory() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onLowMemory();
	}

	@Override
	public IBinder asBinder() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		return mBinder;
	}

	private void startForegroundCompat(int id, Notification notification) {

		Method method = null;
		try {
			method = getClass().getMethod("startForeground",
					new Class[] { int.class, Notification.class });
			Object args[] = new Object[2];
			args[0] = id;
			args[1] = notification;
			method.invoke(this, args);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
				+ method.toGenericString());

		// methodとれなかったら古いAPIを試す
		if (method == null) {
			setForeground(true);
			mNm.notify(id, notification);
		}
	}

	private void stopForegroundCompat(int id) {

		Method method = null;
		try {
			method = getClass().getMethod("stopForeground",
					new Class[] { boolean.class });
			Object args[] = new Object[1];
			args[0] = true;
			method.invoke(this, args);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
				+ method.toGenericString());

		// methodとれなかったら古いAPIを試す
		if (method == null) {
			setForeground(false);
			mNm.cancel(id);
		}
	}

	private final IDroppDataService.Stub mBinder = new Stub() {
		@Override
		public List<AppData> getAppDataList() throws RemoteException {
			return appDataList;
		}

		@Override
		public void registerCallback(IDroppServiceCallback callback)
				throws RemoteException {
			// TODO 実装すること
		}

		@Override
		public void unregisterCallback(IDroppServiceCallback callback)
				throws RemoteException {
			// TODO 実装すること
		}
	};

	@Override
	public List<AppData> getAppDataList() throws RemoteException {
		return appDataList;
	}

	@Override
	public void registerCallback(IDroppServiceCallback callback)
			throws RemoteException {
		// TODO 実装すること
	}

	@Override
	public void unregisterCallback(IDroppServiceCallback callback)
			throws RemoteException {
		// TODO 実装すること
	}
}
