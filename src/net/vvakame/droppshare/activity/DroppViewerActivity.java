package net.vvakame.droppshare.activity;

import java.io.File;
import java.io.InvalidClassException;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.asynctask.DroppInstalledAsynkTask;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.AppDiffAdapter;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDiffData;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DroppViewerActivity extends Activity implements LogTagIF {

	private final OnClickListenerImpl mEventImpl = new OnClickListenerImpl();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		Intent intent = getIntent();
		File destFile = new File(intent.getData().getPath());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewer);

		List<AppData> srcList = null;
		try {
			srcList = AppDataUtil
					.readSerializedCaches(DroppInstalledAsynkTask.CACHE_FILE);
		} catch (InvalidClassException e) {
			// TODO エラー通知する
		} catch (ClassNotFoundException e) {
			// TODO エラー通知する
		}

		List<AppData> destList = null;
		try {
			destList = AppDataUtil.readSerializedCaches(destFile);
		} catch (InvalidClassException e) {
			// TODO エラー通知する
		} catch (ClassNotFoundException e) {
			// TODO エラー通知する
		}

		List<AppDiffData> diffList = AppDataUtil.zipAppData(srcList, destList);
		AppDiffAdapter diffAdapter = new AppDiffAdapter(this,
				R.layout.diff_view, diffList);

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(diffAdapter);
		listView.setOnItemClickListener(mEventImpl);
	}

	private class OnClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			AppDiffData diff = (AppDiffData) parent.getItemAtPosition(position);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String marketUri = AppDataUtil.getMarketUriFromAppData(diff
					.getMasterAppData());
			intent.setData(Uri.parse(marketUri));

			startActivity(intent);
		}
	}
}
