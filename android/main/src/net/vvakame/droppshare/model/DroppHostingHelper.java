package net.vvakame.droppshare.model;

import net.vvakame.droppshare.R;
import android.content.Context;

public class DroppHostingHelper {

	public static String getUri(Context context) {
		return context.getString(R.string.drphost_address);
	}

	public static String getTwitterUri(Context context) {
		return getUri(context) + "/twitter";
	}

	public static String getUploadUri(Context context) {
		return getUri(context) + "/upload";
	}
}
