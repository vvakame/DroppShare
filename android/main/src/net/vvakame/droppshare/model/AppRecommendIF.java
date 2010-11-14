package net.vvakame.droppshare.model;

import net.vvakame.droppshare.common.DatabaseIF;

/**
 * インストールログ保存用クラスのための定数値保持IF
 * 
 * @author vvakame
 */
public interface AppRecommendIF extends DatabaseIF {

	public static final String TABLE_NAME = "app_recommend";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PACKAGE_NAME = "package_name";
	public static final String COLUMN_VERSION_CODE = "version_code";
	public static final String COLUMN_VERSION_NAME = "version_name";
	public static final String COLUMN_GOOD_THING = "good_thing";
	public static final String COLUMN_IMPROVEMENT = "improvement";
}
