package net.vvakame.droppshare.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.FileListAdapter;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DroppSelectorActivity extends Activity implements LogTagIF {
	private static final String SUFFIX = ".dropp";

	private static final FilenameFilter sDroppFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(SUFFIX);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.selector);

		File droppDir = new File(Environment.getExternalStorageDirectory(),
				"DroppShare/");
		File droppCacheDir = new File(droppDir, "caches/");
		File[] droppFiles = droppCacheDir.listFiles(sDroppFilter);

		setFileSet(droppDir.getName(), droppFiles);

		File hoccerDir = new File("/sdcard/hoccer/");
		File[] hoccerFiles = hoccerDir.listFiles(sDroppFilter);

		setFileSet(hoccerDir.getName(), hoccerFiles);
	}

	private void setFileSet(String dirName, File[] droppFiles) {
		List<File> fileList = Arrays.asList(droppFiles);
		addFileSet(dirName, fileList);
	}

	private void addFileSet(String dirName, List<File> fileList) {
		FileListAdapter fileAdapter = new FileListAdapter(this,
				R.layout.file_view, fileList);
		ListView droppList = new ListView(this);
		droppList.setAdapter(fileAdapter);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dirLayout = inflater.inflate(R.layout.dir_header_view, null);
		TextView dirTitleView = (TextView) dirLayout.findViewById(R.id.title);
		dirTitleView.setText(dirName);

		LinearLayout layout = (LinearLayout) findViewById(R.id.dirs_layout);
		layout.addView(dirLayout);
		layout.addView(droppList);
	}
}
