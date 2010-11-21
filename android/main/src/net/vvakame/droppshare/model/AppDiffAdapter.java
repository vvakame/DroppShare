package net.vvakame.droppshare.model;

import java.util.List;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * List<AppDiffData>をListViewなどに表示するためのAdapter
 * 
 * @author vvakame
 */
public class AppDiffAdapter extends ArrayAdapter<AppDiffData> {

	private Context mContext = null;
	private int mResId = 0;

	int mWeak;
	int mStrong;
	int mSide1exists;
	int mSide2exists;
	int mNone;

	public AppDiffAdapter(Context context, int textViewResourceId,
			List<AppDiffData> diffList) {
		super(context, textViewResourceId, diffList);

		mContext = context.getApplicationContext();
		mResId = textViewResourceId;

		mWeak = mContext.getResources().getColor(R.color.weak);
		mStrong = mContext.getResources().getColor(R.color.strong);
		mSide1exists = mContext.getResources().getColor(R.color.side_1_exists);
		mSide2exists = mContext.getResources().getColor(R.color.side_2_exists);
		mNone = mContext.getResources().getColor(R.color.none);
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
			holder.layout = (LinearLayout) view.findViewById(R.id.boss);
			holder.side1View = (ImageView) view.findViewById(R.id.side_1);
			holder.side2View = (ImageView) view.findViewById(R.id.side_2);
			holder.iconView = (ImageView) view
					.findViewById(R.id.application_icon);
			holder.appNameText = (TextView) view
					.findViewById(R.id.application_name);
			holder.appDescText = (TextView) view
					.findViewById(R.id.application_description);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// 差分取得
		AppDiffData diffData = getItem(position);

		AppData master = diffData.getMasterAppData();
		boolean side1 = diffData.hasSrcAppData();
		boolean side2 = diffData.hasDestAppData();
		int textColor = mWeak;
		if (side1 && side2) {
			textColor = mStrong;
		} else if (side1) {
			textColor = mSide1exists;
		} else if (side2) {
			textColor = mSide2exists;
		}

		holder.layout.setBackgroundDrawable(new ColorDrawable(R.color.none));

		// 左の耳の色
		if (side1) {
			holder.side1View.setImageDrawable(new ColorDrawable(mSide1exists));
		} else {
			holder.side1View.setImageDrawable(new ColorDrawable(mNone));
		}

		// 右の耳の色
		if (side2) {
			holder.side2View.setImageDrawable(new ColorDrawable(mSide2exists));
		} else {
			holder.side2View.setImageDrawable(new ColorDrawable(mNone));
		}

		// アプリicon
		if (holder.iconView != null) {
			holder.iconView.setImageDrawable(master.getIcon());
		}

		// アプリ名
		if (holder.appNameText != null) {
			holder.appNameText.setText(master.getAppName());
			holder.appNameText.setTextColor(textColor);
		}

		// アプリの説明
		if (holder.appDescText != null) {
			holder.appDescText.setText(master.getDescription());
			holder.appDescText.setTextColor(textColor);
		}

		return view;
	}

	private static class ViewHolder {
		LinearLayout layout;
		ImageView side1View;
		ImageView side2View;
		ImageView iconView;
		TextView appNameText;
		TextView appDescText;
	}
}
