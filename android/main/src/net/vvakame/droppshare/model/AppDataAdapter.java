package net.vvakame.droppshare.model;

import java.util.List;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * List<AppData>をListViewなどに表示するためのAdapter
 * 
 * @author vvakame
 */
public class AppDataAdapter extends ArrayAdapter<AppData> {

	private Context mContext = null;
	private int mResId = 0;

	public AppDataAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

		mContext = context.getApplicationContext();
		mResId = textViewResourceId;
	}

	public AppDataAdapter(Context context, int textViewResourceId,
			List<AppData> appDataList) {
		super(context, textViewResourceId, appDataList);

		mContext = context.getApplicationContext();
		mResId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(mResId, null);
		}

		// アプリicon
		ImageView iconView = (ImageView) convertView
				.findViewById(R.id.application_icon);
		if (iconView != null) {
			iconView.setImageDrawable(getItem(position).getIcon());
		}

		// アプリ名
		TextView appNameText = (TextView) convertView
				.findViewById(R.id.application_name);
		if (appNameText != null) {
			appNameText.setText(getItem(position).getAppName());
		}

		// アプリの説明
		TextView appDescText = (TextView) convertView
				.findViewById(R.id.application_description);
		if (appDescText != null) {
			appDescText.setText(getItem(position).getDescription());
		}

		// バージョン名
		TextView appVerText = (TextView) convertView
				.findViewById(R.id.application_version_name);
		if (appVerText != null && getItem(position).getVersionName() != null) {
			appVerText.setText(getItem(position).getVersionName());
		}

		// アクション
		TextView actionView = (TextView) convertView
				.findViewById(R.id.application_action);
		if (actionView != null) {
			actionView.setText(getItem(position).getAction());
		}

		// 処理日時
		TextView processDateView = (TextView) convertView
				.findViewById(R.id.application_process_date);
		if (processDateView != null) {
			processDateView.setText(getItem(position).getProcessDate()
					.toLocaleString());
		}

		return convertView;
	}
}
