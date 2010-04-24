package net.vvakame.droppshare.helper;

import java.util.List;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDiffData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDiffAdapter extends ArrayAdapter<AppDiffData> {

	private Context mContext = null;
	private int mResId = 0;

	public AppDiffAdapter(Context context, int textViewResourceId,
			List<AppDiffData> diffList) {
		super(context, textViewResourceId, diffList);

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

		// 差分取得
		AppDiffData diffData = getItem(position);

		AppData master = diffData.getMasterAppData();
		boolean side1 = diffData.hasSrcAppData();
		boolean side2 = diffData.hasDestAppData();
		int textColor = mContext.getResources().getColor(R.color.weak);
		if (side1 && side2) {
			textColor = mContext.getResources().getColor(R.color.strong);
		} else if (side1) {
			textColor = mContext.getResources().getColor(R.color.side_1_exists);
		} else if (side2) {
			textColor = mContext.getResources().getColor(R.color.side_2_exists);
		}

		// 左の耳の色
		View side1View = convertView.findViewById(R.id.side_1);
		if (side1) {
			side1View.setBackgroundColor(mContext.getResources().getColor(
					R.color.side_1_exists));
		} else {
			side1View.setBackgroundColor(mContext.getResources().getColor(
					R.color.weak));
		}

		// 右の耳の色
		View side2View = convertView.findViewById(R.id.side_2);
		if (side2) {
			side2View.setBackgroundColor(mContext.getResources().getColor(
					R.color.side_2_exists));
		} else {
			side2View.setBackgroundColor(mContext.getResources().getColor(
					R.color.weak));
		}

		// アプリicon
		ImageView iconView = (ImageView) convertView
				.findViewById(R.id.application_icon);
		if (iconView != null) {
			iconView.setImageDrawable(master.getIcon());
		}

		// アプリ名
		TextView appNameText = (TextView) convertView
				.findViewById(R.id.application_name);
		if (appNameText != null) {
			appNameText.setText(master.getAppName());
			appNameText.setTextColor(textColor);
		}

		// アプリの説明
		TextView appDescText = (TextView) convertView
				.findViewById(R.id.application_description);
		if (appDescText != null) {
			appDescText.setText(master.getDescription());
			appDescText.setTextColor(textColor);
		}

		return convertView;
	}
}
