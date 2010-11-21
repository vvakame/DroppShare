package net.vvakame.droppshare.task;

import java.io.File;
import java.io.InvalidClassException;
import java.util.List;

import net.vvakame.android.helper.Func;
import net.vvakame.android.helper.Log;
import net.vvakame.droppshare.common.SerializeUtil;
import net.vvakame.droppshare.model.AppData;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

/**
 * アプリデータをキャッシュから読み込む共通処理と画面への反映部分を実装したAsyncTask
 * 
 * @author vvakame
 */
public abstract class DrozipBaseAsyncTask extends
		AsyncTask<Boolean, AppData, List<AppData>> {

	protected Context mContext = null;
	protected ArrayAdapter<AppData> mAdapter = null;
	protected Func<List<AppData>> mFunc = null;

	public DrozipBaseAsyncTask(Context context, ArrayAdapter<AppData> adapter,
			Func<List<AppData>> postExecFunc) {
		super();

		Log.d();
		mContext = context.getApplicationContext();
		mAdapter = adapter;
		mFunc = postExecFunc;
	}

	public DrozipBaseAsyncTask(Context context, Func<List<AppData>> postExecFunc) {
		super();

		Log.d();
		mContext = context.getApplicationContext();
		mFunc = postExecFunc;
	}

	protected List<AppData> tryReadCache(File file, Boolean... params) {
		Log.d();

		List<AppData> appDataList = null;

		boolean clearFlg = params.length == 1
				&& params[0].booleanValue() == true;

		if (!clearFlg && file.exists()) {
			Log.d(Log.getStackName() + ", use cache.");
			try {
				appDataList = SerializeUtil.readSerializedCaches(file);
				if (appDataList != null) {
					for (AppData appData : appDataList) {
						publishProgress(appData);
					}
				}
			} catch (InvalidClassException e) {
				// ここに来るのは、SerializeされたオブジェクトのserialVersionUIDが一致しないときに来る想定
				Log.e(e);
			} catch (ClassNotFoundException e) {
				// ここに来るのは、Serializeされたオブジェクトのパッケージ名が変更になってたりしたときに来る想定
				Log.e(e);
			}
		}

		return appDataList;
	}

	@Override
	protected void onProgressUpdate(AppData... values) {
		super.onProgressUpdate(values);

		if (mAdapter != null && values.length == 1) {
			mAdapter.add(values[0]);
		}
	}

	@Override
	protected void onPostExecute(List<AppData> appDataList) {
		Log.d();
		super.onPostExecute(appDataList);

		if (mFunc != null) {
			mFunc.func(appDataList);
		}
	}
}
