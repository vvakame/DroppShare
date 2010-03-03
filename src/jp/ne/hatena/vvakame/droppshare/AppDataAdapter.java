package jp.ne.hatena.vvakame.droppshare;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDataAdapter extends ArrayAdapter<ApplicationInfo> {

	private Context mCon = null;
	private AppData[] mAppData = null;

	private List<ApplicationInfo> mAppInfoList = null;
	private PackageManager mPm = null;

	private Thread mTh = null;

	public AppDataAdapter(Context context, int textViewResourceId,
			List<ApplicationInfo> appInfoList) {
		super(context, textViewResourceId, appInfoList);

		mCon = context;
		mAppData = new AppData[appInfoList.size()];
		mAppInfoList = appInfoList;
		mPm = mCon.getPackageManager();

		for (int i = 0; i < appInfoList.size(); i++) {
			AppData appData = new AppData();
			appData.setAppName(mPm.getApplicationLabel(appInfoList.get(i)));

			mAppData[i] = appData;
		}

		mTh = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < mAppInfoList.size(); i++) {
					mAppData[i].setIcon(mAppInfoList.get(i).loadIcon(mPm));
				}
				// GCされたい
				mAppInfoList = null;
			}
		};
		mTh.start();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mCon
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.application_view, null);
		}

		if (mAppData[position].getIcon() != null) {
			ImageView iconView = (ImageView) convertView
					.findViewById(R.id.application_icon);
			iconView.setImageDrawable(mAppData[position].getIcon());
		}

		TextView appNameText = (TextView) convertView
				.findViewById(R.id.application_name);
		appNameText.setText(mAppData[position].getAppName());

		return convertView;
	}
}
