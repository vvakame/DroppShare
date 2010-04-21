package net.vvakame.droppshare.asynctask;

import java.io.InvalidClassException;
import java.util.List;

import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.helper.HelperUtil;
import net.vvakame.droppshare.model.AppData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public abstract class DroppBaseAsynkTask extends
		AsyncTask<Boolean, AppData, List<AppData>> {
	private static final String TAG = DroppBaseAsynkTask.class.getSimpleName();

	protected Context mContext = null;
	protected ArrayAdapter<AppData> mAdapter = null;
	protected Func<List<AppData>> mFunc = null;

	public DroppBaseAsynkTask(Context context, ArrayAdapter<AppData> adapter,
			Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context.getApplicationContext();
		mAdapter = adapter;
		mFunc = postExecFunc;
	}

	public DroppBaseAsynkTask(Context context, Func<List<AppData>> postExecFunc) {
		super();

		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		mContext = context.getApplicationContext();
		mFunc = postExecFunc;
	}

	protected List<AppData> tryReadCache(String fileName, Boolean... params) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		List<AppData> appDataList = null;

		boolean clearFlg = params.length == 1
				&& params[0].booleanValue() == true;

		if (!clearFlg && AppDataUtil.isExistCache(fileName)) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", use cache.");
			try {
				appDataList = AppDataUtil.readSerializedCaches(fileName);
				for (AppData appData : appDataList) {
					publishProgress(appData);
				}
			} catch (InvalidClassException e) {
				// ここに来るのは、SerializeされたオブジェクトのserialVersionUIDが一致しないときに来る想定
				Log.d(TAG, HelperUtil.getExceptionLog(e));
			} catch (ClassNotFoundException e) {
				// ここに来るのは、Serializeされたオブジェクトのパッケージ名が変更になってたりしたときに来る想定
				Log.d(TAG, HelperUtil.getExceptionLog(e));
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
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());
		super.onPostExecute(appDataList);

		mFunc.func(appDataList);
	}
}
