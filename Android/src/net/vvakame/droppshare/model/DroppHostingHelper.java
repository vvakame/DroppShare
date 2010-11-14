package net.vvakame.droppshare.model;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.content.pm.ApplicationInfo;

public class DroppHostingHelper {

	public static String getUri(Context context) {
		ApplicationInfo appInfo = context.getApplicationInfo();
		if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			return context.getString(R.string.drphost_address_debug);
		} else {
			return context.getString(R.string.drphost_address);
		}
	}

	public static String getTwitterUri(Context context) {
		return getUri(context) + "/twitter";
	}

	public static String getUploadUri(Context context) {
		return getUri(context) + "/upload";
	}
}
