package jp.ne.hatena.vvakame.droppshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DroppShareReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "broadcast!", Toast.LENGTH_LONG).show();
	}
}
