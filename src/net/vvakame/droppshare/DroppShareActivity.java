package net.vvakame.droppshare;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DroppShareActivity extends Activity {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
	private static final String REPLACE_KEY = "replace_key";

	private static final int DIALOG_PROGRESS = 1;

	private static final int MESSAGE_READING_APP_DATA = 1;
	private static final int MESSAGE_UPDATE_PROGRESS = 2;

	private boolean mDone = false;
	private ProgressDialog mProgDialog = null;

	private PackageManager mPm = null;

	private OnItemClickListener mEventImpl = null;

	private List<AppData> mAppDataList = null;

	private Thread mTh = new Thread() {
		@Override
		public void run() {

			if (AppDataUtil.isExistCache(DroppShareActivity.this)) {
				mAppDataList = AppDataUtil
						.readSerializedCaches(DroppShareActivity.this);

				for (AppData appData : mAppDataList) {
					AppDataUtil.readIconCache(DroppShareActivity.this, appData);
				}
			} else {
				mAppDataList = new ArrayList<AppData>();

				mPm = getPackageManager();
				List<ApplicationInfo> appInfoList = mPm
						.getInstalledApplications(PackageManager.GET_ACTIVITIES);

				for (ApplicationInfo appInfo : appInfoList) {
					Log.d(TAG, "now processing " + appInfo.packageName);

					AppData appData = new AppData();

					appData.setAppName(mPm.getApplicationLabel(appInfo)
							.toString());
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
					AppDataUtil
							.writeIconCache(DroppShareActivity.this, appData);

					mAppDataList.add(appData);
				}

				Collections.sort(mAppDataList, new Comparator<AppData>() {
					@Override
					public int compare(AppData obj1, AppData obj2) {
						return obj1.getAppName().compareTo(obj2.getAppName());
					}
				});

				AppDataUtil.writeSerializedCache(DroppShareActivity.this,
						mAppDataList);
			}

			mDone = true;
		}
	};

	private Handler mProgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_READING_APP_DATA:

				if (mDone) {
					AppDataAdapter appAdapter = new AppDataAdapter(
							DroppShareActivity.this, R.layout.application_view,
							mAppDataList);
					ListView listView = (ListView) findViewById(R.id.app_list);
					listView.setAdapter(appAdapter);
					dismissDialog(DIALOG_PROGRESS);

				} else {
					mProgHandler.sendEmptyMessageDelayed(
							MESSAGE_READING_APP_DATA, 100);
				}

				break;

			case MESSAGE_UPDATE_PROGRESS:
				mProgDialog.setMessage(msg.obj.toString());
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onResume();

		if (mDone) {
			return;
		}

		showDialog(DIALOG_PROGRESS);
		mProgHandler.sendEmptyMessage(MESSAGE_READING_APP_DATA);

		setContentView(R.layout.main);

		if (isCalledBySimeji()) {
			mEventImpl = new EventSimejiImpl();
		} else {
			mEventImpl = new EventNormalImpl();
		}

		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setOnItemClickListener(mEventImpl);

		mTh.start();
	}

	@Override
	public void onPause() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onPause();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			mProgDialog = new ProgressDialog(this);
			onPrepareDialog(id, mProgDialog);

			return mProgDialog;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_PROGRESS:
			ProgressDialog progDialog = (ProgressDialog) dialog;
			progDialog.setTitle(getString(R.string.now_reading_app_data));
			progDialog.setMessage(getString(R.string.wait_a_moment));
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setCancelable(false);

			break;
		default:
			break;
		}
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