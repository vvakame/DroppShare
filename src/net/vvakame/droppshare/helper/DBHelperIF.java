package net.vvakame.droppshare.helper;

import android.content.Context;

/**
 * Twitterのデータを格納するDBについての定数を定義
 * 
 * @author vvakame
 */
public interface DBHelperIF {
	public static final String DB_NAME = "data.db";

	public static final int DB_VERSION = 2;
	public static final int DB_MODE = Context.MODE_PRIVATE;
}
