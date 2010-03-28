package net.vvakame.droppshare.helper;

import net.vvakame.util.twitter.UserModel;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Twitterのデータを格納するDBの用意
 * 
 * @author vvakame
 */
public class DBHelper extends SQLiteOpenHelper implements DBHelperIF {

	// private static final String DB_DROP = "drop table " +
	// UserModel.TABLE_NAME;

	private static final String DB_CREATE = "create table "
			+ UserModel.TABLE_NAME + " (" + UserModel.COLUMN_ID
			+ " integer primary key autoincrement not null, "
			+ UserModel.COLUMN_SCREEN_NAME + " text not null, "
			+ UserModel.COLUMN_NAME + " text not null,"
			+ UserModel.COLUMN_FAVORITE + " text)";

	private static final String DB_ALTER_TABLE_1_2 = "ALTER TABLE "
			+ UserModel.TABLE_NAME + " ADD " + UserModel.COLUMN_FAVORITE
			+ " text";

	public DBHelper(Context con) {
		super(con, DB_NAME, null, DB_VERSION);
	}

	@SuppressWarnings("all")
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// まだ、ない。
		if (oldVersion == 1 && newVersion == 2) {
			upgrade1to2(db);
		}
	}

	private void upgrade1to2(SQLiteDatabase db) {
		db.execSQL(DB_ALTER_TABLE_1_2);
	}
}
