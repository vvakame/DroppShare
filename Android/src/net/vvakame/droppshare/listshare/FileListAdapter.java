package net.vvakame.droppshare.listshare;

import java.io.File;
import java.util.List;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * List<File>をListViewなどに表示するためのAdapter
 * 
 * @author vvakame
 */
public class FileListAdapter extends ArrayAdapter<File> {

	private Context mContext = null;
	private int mResId = 0;

	public FileListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

		mContext = context.getApplicationContext();
		mResId = textViewResourceId;
	}

	public FileListAdapter(Context context, int textViewResourceId,
			List<File> fileList) {
		super(context, textViewResourceId, fileList);

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

		// ファイル名
		TextView nameText = (TextView) convertView.findViewById(R.id.file_name);
		if (nameText != null) {
			nameText.setText(getItem(position).getName());
		}

		return convertView;
	}
}
