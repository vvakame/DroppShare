package net.vvakame.droppshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DroppShareReceiver extends BroadcastReceiver {
	private static final String TAG = DroppShareReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
				+ intent.getAction());
	}
}
