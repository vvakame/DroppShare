package net.vvakame.droppshare;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDataAdapter extends ArrayAdapter<AppData> {

	private Context mCon = null;

	private List<AppData> mAppDataList = null;

	public AppDataAdapter(Context context, int textViewResourceId,
			List<AppData> appDataList) {
		super(context, textViewResourceId, appDataList);

		mCon = context;
		mAppDataList = appDataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mCon
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.application_view, null);
		}

		ImageView iconView = (ImageView) convertView
				.findViewById(R.id.application_icon);
		iconView.setImageDrawable(mAppDataList.get(position).getIcon());

		TextView appNameText = (TextView) convertView
				.findViewById(R.id.application_name);
		appNameText.setText(mAppDataList.get(position).getAppName());

		TextView appDescText = (TextView) convertView
				.findViewById(R.id.application_description);
		appDescText.setText(mAppDataList.get(position).getDescription());

		TextView appVerText = (TextView) convertView
				.findViewById(R.id.application_version_name);
		appVerText.setText(mAppDataList.get(position).getVersionName());

		return convertView;
	}
}
