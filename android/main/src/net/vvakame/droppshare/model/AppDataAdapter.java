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
		ViewHolder holder;

		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResId, null);

			holder = new ViewHolder();
			holder.iconView = (ImageView) view
					.findViewById(R.id.application_icon);
			holder.appNameText = (TextView) view
					.findViewById(R.id.application_name);
			holder.appDescText = (TextView) view
					.findViewById(R.id.application_description);
			holder.appVerText = (TextView) view
					.findViewById(R.id.application_version_name);
			holder.actionView = (TextView) view
					.findViewById(R.id.application_action);
			holder.processDateView = (TextView) view
					.findViewById(R.id.application_process_date);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// アプリicon
		if (holder.iconView != null) {
			holder.iconView.setImageDrawable(getItem(position).getIcon());
		}

		// アプリ名
		if (holder.appNameText != null) {
			holder.appNameText.setText(getItem(position).getAppName());
		}

		// アプリの説明
		if (holder.appDescText != null) {
			holder.appDescText.setText(getItem(position).getDescription());
		}

		// バージョン名
		if (holder.appVerText != null
				&& getItem(position).getVersionName() != null) {
			holder.appVerText.setText(getItem(position).getVersionName());
		}

		// アクション
		if (holder.actionView != null) {
			holder.actionView.setText(getItem(position).getAction());
		}

		// 処理日時
		if (holder.processDateView != null) {
			holder.processDateView.setText(getItem(position).getProcessDate()
					.toLocaleString());
		}

		return view;
	}

	private static class ViewHolder {
		ImageView iconView;
		TextView appNameText;
		TextView appDescText;
		TextView appVerText;
		TextView actionView;
		TextView processDateView;
	}
}
