package jp.ne.hatena.vvakame.droppshare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	@Override
	public void onCreate() {
		super.onCreate();

		mPm = getPackageManager();
		List<ApplicationInfo> appInfoList = mPm
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);

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
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		return mBinder;
	}

	@Override
	public IBinder asBinder() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		return mBinder;
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
