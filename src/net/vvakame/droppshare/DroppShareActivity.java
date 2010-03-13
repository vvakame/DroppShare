package net.vvakame.droppshare;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DroppShareActivity extends Activity {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
	private static final String REPLACE_KEY = "replace_key";

	private PackageManager mPm = null;

	private OnItemClickListener mEventImpl = null;

	private List<AppData> mAppDataList = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (isCalledBySimeji()) {
			mEventImpl = new EventSimejiImpl();
		} else {
			mEventImpl = new EventNormalImpl();
		}

		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setOnItemClickListener(mEventImpl);

		mAppDataList = new ArrayList<AppData>();

		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".data");
			}
		};
		File[] serializedFiles = getFilesDir().listFiles(filenameFilter);

		if (serializedFiles.length != 0) {
			for (File file : serializedFiles) {
				AppData appData = AppDataUtil.readSerializedFile(this, file);
				if (appData != null) {
					mAppDataList.add(appData);
				}
			}
		} else {
			mPm = getPackageManager();
			List<ApplicationInfo> appInfoList = mPm
					.getInstalledApplications(PackageManager.GET_ACTIVITIES);

			for (ApplicationInfo appInfo : appInfoList) {
				Log.d(TAG, "now processing " + appInfo.packageName);

				AppData appData = new AppData();

				appData.setAppName(mPm.getApplicationLabel(appInfo).toString());
				appData.setPackageName(appInfo.packageName);

				Drawable icon = mPm.getApplicationIcon(appInfo);

				if (icon instanceof BitmapDrawable) {
					Bitmap resizedBitmap = AppDataUtil
							.getResizedBitmapDrawable(((BitmapDrawable) icon)
									.getBitmap());
					icon = new BitmapDrawable(resizedBitmap);
				} else {
					Log.d(TAG, "Not supported icon type: "
							+ icon.getClass().getSimpleName());
				}
				appData.setIcon(icon);

				// AppDataUtil.writeSerializedFile(this, appData);

				mAppDataList.add(appData);
			}

			Collections.sort(mAppDataList, new Comparator<AppData>() {
				@Override
				public int compare(AppData obj1, AppData obj2) {
					return obj1.getAppName().compareTo(obj2.getAppName());
				}
			});
		}

		AppDataAdapter appAdapter = new AppDataAdapter(this,
				R.layout.application_view, mAppDataList);
		listView.setAdapter(appAdapter);
	}

	@Override
	public void onResume() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onPause();
	}

	private boolean isCalledBySimeji() {
		Intent intent = getIntent();
		String action = intent.getAction();

		// Simejiから呼び出された時
		if (action != null && ACTION_INTERCEPT.equals(action)) {
			return true;
		} else {
			return false;
		}
	}

	private void pushToSimeji(String result) {
		Intent data = new Intent();
		data.putExtra(REPLACE_KEY, result);
		setResult(RESULT_OK, data);
		finish();
	}

	class EventNormalImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			AppData appData = mAppDataList.get(position);

			String marketUri = null;
			try {
				marketUri = AppDataUtil.getUriFromAppData(appData);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Intent data = new Intent();
			data.setAction(Intent.ACTION_SEND);
			data.setType("text/plain");
			data.putExtra(Intent.EXTRA_TEXT, appData.getAppName() + " "
					+ marketUri);

			startActivity(data);
			finish();
		}
	}

	class EventSimejiImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			AppData appData = mAppDataList.get(position);

			String marketUri = null;
			try {
				marketUri = AppDataUtil.getUriFromAppData(mAppDataList
						.get(position));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			pushToSimeji(appData.getAppName() + " " + marketUri);
		}
	}
}