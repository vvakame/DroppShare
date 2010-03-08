package jp.ne.hatena.vvakame.droppshare;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class DroppShareService extends Service implements IDroppDataService {
	private static final String TAG = DroppShareService.class.getSimpleName();

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
		public void showToast() throws RemoteException {
			DroppShareService.this.showToast();
		}
	};

	@Override
	public void showToast() throws RemoteException {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		Toast.makeText(this, "by the service.", Toast.LENGTH_LONG);
	}
}
