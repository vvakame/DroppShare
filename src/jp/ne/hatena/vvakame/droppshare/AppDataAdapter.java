package jp.ne.hatena.vvakame.droppshare;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDataAdapter extends ArrayAdapter<ApplicationInfo> {

	private Context mCon = null;
	private PackageManager mPm = null;
	private List<ApplicationInfo> mAppList = null;
	private Drawable[] mIconArray = null;

	public AppDataAdapter(Context context, int textViewResourceId,
			List<ApplicationInfo> objects) {
		super(context, textViewResourceId, objects);
		mCon = context;
		mPm = mCon.getPackageManager();
		mAppList = objects;
		mIconArray = new Drawable[mAppList.size()];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mCon
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.application_view, null);
		}

		ApplicationInfo appData = mAppList.get(position);

		ImageView iconView = (ImageView) convertView
				.findViewById(R.id.application_icon);
		Drawable iconImg = null;
		if (mIconArray[position] != null) {
			iconImg = mIconArray[position];
		} else {
			iconImg = appData.loadIcon(mPm);
			mIconArray[position] = iconImg;
		}
		iconView.setImageDrawable(iconImg);

		TextView appNameText = (TextView) convertView
				.findViewById(R.id.application_name);
		CharSequence appName = mPm.getApplicationLabel(appData);
		appNameText.setText(appName);

		return convertView;
	}
}
