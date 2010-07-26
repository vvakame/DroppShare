package net.vvakame.droppshare.model;

import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.LogTagIF;
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
public class AppRecommendDao implements AppRecommendIF, LogTagIF {

	private DBHelper mHelper = null;

	private class DBHelper extends SQLiteOpenHelper {
		private static final String DB_CREATE = "create table " + TABLE_NAME
				+ " (" + COLUMN_ID
				+ " integer primary key autoincrement not null,"
				+ COLUMN_PACKAGE_NAME + " text not null," + COLUMN_VERSION_CODE
				+ " integer not null," + COLUMN_VERSION_NAME
				+ " text not null," + COLUMN_GOOD_THING + " text not null,"
				+ COLUMN_IMPROVEMENT + " text , UNIQUE (" + COLUMN_PACKAGE_NAME
				+ ") )";

		@SuppressWarnings("unused")
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
		}
	}

	public AppRecommendDao(Context con) {
		mHelper = new DBHelper(con);
	}

	public AppRecommendModel save(AppRecommendModel model) {
		Log.d(TAG, HelperUtil.getStackName() + ", " + model);

		SQLiteDatabase db = mHelper.getWritableDatabase();
		AppRecommendModel result = null;
		try {
			ContentValues values = new ContentValues();
			values.put(COLUMN_PACKAGE_NAME, model.getPackageName());
			values.put(COLUMN_VERSION_CODE, model.getVersionCode());
			values.put(COLUMN_VERSION_NAME, model.getVersionName());
			values.put(COLUMN_GOOD_THING, model.getGoodThing());
			values.put(COLUMN_IMPROVEMENT, model.getImprovement());

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

	public AppRecommendModel load(Long rowId) {
		Log.d(TAG, HelperUtil.getStackName());

		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = null;

		AppRecommendModel model = null;
		try {
			cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?",
					new String[] { String.valueOf(rowId) }, null, null, null);
			cursor.moveToFirst();
			model = getAppRecommendModel(cursor);
		} finally {
			cursor.close();
			db.close();
		}
		return model;
	}

	public void delete(AppRecommendModel model) {
		Log.d(TAG, HelperUtil.getStackName());

		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, COLUMN_PACKAGE_NAME + "=?",
					new String[] { String.valueOf(model.getPackageName()) });
		} finally {
			db.close();
		}
	}

	public void truncate() {
		Log.d(TAG, HelperUtil.getStackName());

		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.execSQL("delete from " + TABLE_NAME);
		} finally {
			db.close();
		}
	}

	public List<AppRecommendModel> list() {
		Log.d(TAG, HelperUtil.getStackName());

		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = null;

		List<AppRecommendModel> modelList;
		try {
			cursor = db.query(TABLE_NAME, null, null, null, null, null, null,
					null);
			modelList = new ArrayList<AppRecommendModel>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				modelList.add(getAppRecommendModel(cursor));
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
			db.close();
		}
		return modelList;
	}

	private AppRecommendModel getAppRecommendModel(Cursor cursor) {
		AppRecommendModel model = new AppRecommendModel();

		int rowId = cursor.getColumnIndex(COLUMN_ID);
		int packageName = cursor.getColumnIndex(COLUMN_PACKAGE_NAME);
		int versionCode = cursor.getColumnIndex(COLUMN_VERSION_CODE);
		int versionName = cursor.getColumnIndex(COLUMN_VERSION_NAME);
		int goodThing = cursor.getColumnIndex(COLUMN_GOOD_THING);
		int improvement = cursor.getColumnIndex(COLUMN_IMPROVEMENT);

		model.setRowId(cursor.getLong(rowId));
		model.setPackageName(cursor.getString(packageName));
		model.setVersionCode(cursor.getInt(versionCode));
		model.setVersionName(cursor.getString(versionName));
		model.setGoodThing(cursor.getString(goodThing));
		model.setImprovement(cursor.getString(improvement));

		return model;
	}
}
