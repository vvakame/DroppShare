package net.vvakame.droppshare.helper;

import java.util.List;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.model.AppData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDataAdapter extends ArrayAdapter<AppData> {

	private Context mCon = null;

	public AppDataAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

		mCon = context;
	}

	public AppDataAdapter(Context context, int textViewResourceId,
			List<AppData> appDataList) {
		super(context, textViewResourceId, appDataList);

		mCon = context;
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
		iconView.setImageDrawable(getItem(position).getIcon());

		TextView appNameText = (TextView) convertView
				.findViewById(R.id.application_name);
		appNameText.setText(getItem(position).getAppName());

		TextView appDescText = (TextView) convertView
				.findViewById(R.id.application_description);
		appDescText.setText(getItem(position).getDescription());

		TextView appVerText = (TextView) convertView
				.findViewById(R.id.application_version_name);
		appVerText.setText(getItem(position).getVersionName());

		return convertView;
	}
}
