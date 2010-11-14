package net.vvakame.droppshare.model;

import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.AndroidUtil;
import net.vvakame.droppshare.common.LogTagIF;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * インストールログ保持のためのDAO
 * 
 * @author vvakame
 */
public class InstallLogDao implements LogTagIF {

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

	private DBHelper mHelper = null;

	private class DBHelper extends SQLiteOpenHelper {
		// 注意！UNIQUE制約に引っかかった場合REPLACEになります(MERGEな動作)
		private static final String DB_CREATE = "create table " + TABLE_NAME
				+ " (" + COLUMN_ID
				+ " integer primary key autoincrement not null,"
				+ COLUMN_PACKAGE_NAME + " text not null," + COLUMN_VERSION_CODE
				+ " integer not null," + COLUMN_VERSION_NAME
				+ " text not null," + COLUMN_ACTION_TYPE + " text not null,"
				+ COLUMN_PROCESS_DATE + " text not null, UNIQUE ("
				+ COLUMN_PACKAGE_NAME + ", " + COLUMN_VERSION_CODE
				+ ") ON CONFLICT REPLACE )";

		private static final String DB_DROP = "drop table " + TABLE_NAME;

		public DBHelper(Context con) {
			super(con, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion == 1 && newVersion == 2) {
				db.execSQL(DB_DROP);
				db.execSQL(DB_CREATE);
			}
		}
	}

	public InstallLogDao(Context con) {
		mHelper = new DBHelper(con);
	}

	public InstallLogModel save(InstallLogModel model) {
		Log.d(TAG, AndroidUtil.getStackName() + ", " + model);

		SQLiteDatabase db = mHelper.getWritableDatabase();
		InstallLogModel result = null;
		try {
			ContentValues values = new ContentValues();
			values.put(COLUMN_PACKAGE_NAME, model.getPackageName());
			values.put(COLUMN_VERSION_CODE, model.getVersionCode());
			values.put(COLUMN_VERSION_NAME, model.getVersionName());
			values.put(COLUMN_ACTION_TYPE, model.getActionType());
			values.put(COLUMN_PROCESS_DATE, model.getProcessDateString());

			Long rowId = model.getRowId();
			// IDがnullの場合はinsert
			if (rowId == null) {
				rowId = db.insert(TABLE_NAME, null, values);
			} else {
				db.update(TABLE_NAME, values, COLUMN_ID + "=?",
						new String[] { String.valueOf(rowId) });
			}
			result = load(rowId);
		} finally {
			db.close();
		}
		return result;
	}

	public InstallLogModel load(Long rowId) {
		Log.d(TAG, AndroidUtil.getStackName());

		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = null;

		InstallLogModel model = null;
		try {
			cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?",
					new String[] { String.valueOf(rowId) }, null, null, null);
			cursor.moveToFirst();
			model = getInstallLogModel(cursor);
		} finally {
			cursor.close();
			db.close();
		}
		return model;
	}

	public void delete(InstallLogModel model) {
		Log.d(TAG, AndroidUtil.getStackName());

		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, COLUMN_PACKAGE_NAME + "=?",
					new String[] { String.valueOf(model.getPackageName()) });
		} finally {
			db.close();
		}
	}

	public void truncate() {
		Log.d(TAG, AndroidUtil.getStackName());

		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("delete from " + TABLE_NAME);
		} finally {
			db.close();
		}
	}

	public List<InstallLogModel> list() {
		Log.d(TAG, AndroidUtil.getStackName());

		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = null;

		List<InstallLogModel> modelList;
		try {
			cursor = db.query(TABLE_NAME, null, null, null, null, null,
					ORDER_BY, MAX_LINES);
			modelList = new ArrayList<InstallLogModel>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				modelList.add(getInstallLogModel(cursor));
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
			db.close();
		}
		return modelList;
	}

	private InstallLogModel getInstallLogModel(Cursor cursor) {
		InstallLogModel model = new InstallLogModel();

		int rowId = cursor.getColumnIndex(COLUMN_ID);
		int packageName = cursor.getColumnIndex(COLUMN_PACKAGE_NAME);
		int versionCode = cursor.getColumnIndex(COLUMN_VERSION_CODE);
		int versionName = cursor.getColumnIndex(COLUMN_VERSION_NAME);
		int actionType = cursor.getColumnIndex(COLUMN_ACTION_TYPE);
		int processDate = cursor.getColumnIndex(COLUMN_PROCESS_DATE);

		model.setRowId(cursor.getLong(rowId));
		model.setPackageName(cursor.getString(packageName));
		model.setVersionCode(cursor.getInt(versionCode));
		model.setVersionName(cursor.getString(versionName));
		model.setActionType(cursor.getString(actionType));
		model.setProcessDate(cursor.getString(processDate));

		return model;
	}
}
