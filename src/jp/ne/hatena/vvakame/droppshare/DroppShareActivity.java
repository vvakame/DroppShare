package jp.ne.hatena.vvakame.droppshare;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DroppShareActivity extends Activity {
	private static final String TAG = DroppShareActivity.class.getSimpleName();

	private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
	private static final String REPLACE_KEY = "replace_key";

	private OnItemClickListener mEventImpl = null;

	private PackageManager mPm = null;

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

		mPm = getPackageManager();
		List<ApplicationInfo> appInfoList = mPm
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);

		// TODO getApplicationLabelは決して安くない
		Collections.sort(appInfoList, new Comparator<ApplicationInfo>() {

			@Override
			public int compare(ApplicationInfo obj1, ApplicationInfo obj2) {
				String str1 = mPm.getApplicationLabel(obj1).toString();
				String str2 = mPm.getApplicationLabel(obj2).toString();
				return str1.compareTo(str2);
			}
		});

		AppDataAdapter appDataAdapter = new AppDataAdapter(this,
				R.layout.application_view, appInfoList);
		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setAdapter(appDataAdapter);
		listView.setOnItemClickListener(mEventImpl);
	}

	private IDroppDataService mServiceInterface = null;
	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mServiceInterface = IDroppDataService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceInterface = null;
		}
	};

	@Override
	public void onResume() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onResume();
		Intent service = new Intent(this, DroppShareService.class);
		bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
		try {
			mServiceInterface.showToast();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		super.onPause();
		unbindService(mServiceConnection);
	}

	private String getUriFromAppData(String appName)
			throws UnsupportedEncodingException {
		return "http://market.android.com/search?q="
				+ URLEncoder.encode(appName, "UTF-8");
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
			TextView appNameText = (TextView) view
					.findViewById(R.id.application_name);
			String appName = appNameText.getText().toString();
			String marketUri = null;
			try {
				marketUri = getUriFromAppData(appName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			Intent data = new Intent();
			data.setAction(Intent.ACTION_SEND);
			data.setType("text/plain");
			data.putExtra(Intent.EXTRA_TEXT, appName + " " + marketUri);

			startActivity(data);
			finish();
		}
	}

	class EventSimejiImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			TextView appNameText = (TextView) view
					.findViewById(R.id.application_name);
			String appName = appNameText.getText().toString();
			String marketUri = null;
			try {
				marketUri = getUriFromAppData(appName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			pushToSimeji(appName + " " + marketUri);
		}
	}
}