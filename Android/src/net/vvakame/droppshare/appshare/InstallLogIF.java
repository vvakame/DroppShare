package net.vvakame.droppshare.appshare;

import net.vvakame.droppshare.common.DatabaseIF;

import android.content.Context;

/**
 * インストールログ保存用クラスのための定数値保持IF
 * 
 * @author vvakame
 */
public interface InstallLogIF {
	public static final String DB_NAME = "install_log.db";

	public static final int DB_VERSION = 2;
	public static final int DB_MODE = Context.MODE_PRIVATE;

	public static final String TABLE_NAME = "install_log";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PACKAGE_NAME = "package_name";
	public static final String COLUMN_VERSION_CODE = "version_code";
	public static final String COLUMN_VERSION_NAME = "version_name";
	public static final String COLUMN_ACTION_TYPE = "action_type";
	public static final String COLUMN_PROCESS_DATE = "process_date";

	public static final String ORDER_BY = COLUMN_PROCESS_DATE + " desc";
	public static final String MAX_LINES = "0, 30";
}
