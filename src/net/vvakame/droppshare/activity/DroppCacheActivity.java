package net.vvakame.droppshare.activity;

import java.util.List;

import net.vvakame.droppshare.helper.Func;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.util.HelperUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DroppCacheActivity extends Activity {
	private static final String TAG = DroppCacheActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);

		DroppCacheAsynkTask asyncTask = new DroppCacheAsynkTask(this,
				new Func<List<AppData>>() {
					@Override
					public void func(List<AppData> arg) {
						finish();
					}
				});
		asyncTask.execute(true);
	}
}
