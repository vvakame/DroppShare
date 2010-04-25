package net.vvakame.droppshare.activity;

import static net.vvakame.droppshare.helper.SelectorHelper.installWatchingFile;
import static net.vvakame.droppshare.helper.SelectorHelper.readWatchingFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.FileListAdapter;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class DroppSelectorActivity extends Activity implements LogTagIF {
	private static final String SUFFIX = ".dropp";
	private static final String TYPE = "application/droppshare";

	private static final String PICK_DIRECTORY = "org.openintents.action.PICK_DIRECTORY";
	private static final String TITLE = "org.openintents.extra.TITLE";
	private static final String BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";

	private static final int REQUEST_PICK_DIR = 0;

	private static final FilenameFilter sDroppFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(SUFFIX);
		}
	};

	private final OnItemClickListenerImpl mClickEventImpl = new OnItemClickListenerImpl();
	private final OnItemLongClickListenerImpl mLongClickEventImpl = new OnItemLongClickListenerImpl();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.selector);

		// ファイルの作成
		installWatchingFile(this);

		// /sdcard/DroppShare/caches
		addFileSet(AppDataUtil.CACHE_DIR);

		// ファイルから読み込んじゃうのぜ！！
		List<String> dirList = readWatchingFile();

		for (String dirName : dirList) {
			addFileSet(dirName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.selector, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.add_dir:
			intent = new Intent(PICK_DIRECTORY);
			intent.setData(Uri.parse("file://"
					+ Environment.getExternalStorageDirectory()
							.getAbsolutePath()));
			intent.putExtra(TITLE, getString(R.string.add_dir_title));
			intent.putExtra(BUTTON_TEXT,
					getString(R.string.add_dir_button_text));

			try {
				startActivityForResult(intent, REQUEST_PICK_DIR);
			} catch (ActivityNotFoundException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);
				alertDialogBuilder
						.setTitle(getString(R.string.not_resolve_pick_dir_title));
				alertDialogBuilder
						.setMessage(getString(R.string.not_resolve_pick_dir_message));
				alertDialogBuilder.setPositiveButton(
						getString(R.string.go_market),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent marketIntent = new Intent(
										Intent.ACTION_VIEW);
								marketIntent
										.setData(Uri
												.parse("market://search?q=pname:org.openintents.filemanager"));
								startActivity(marketIntent);
							}
						});
				alertDialogBuilder.setNegativeButton(
						getString(R.string.ignore),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
				alertDialogBuilder.setCancelable(true);
				alertDialogBuilder.create();
				alertDialogBuilder.show();
			}

			break;

		default:
			ret = super.onOptionsItemSelected(item);
			break;
		}

		return ret;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, HelperUtil.getStackName() + ", req=" + requestCode
				+ ", res=" + resultCode + ", data="
				+ (data != null ? data.getDataString() : "none"));

		if (requestCode == REQUEST_PICK_DIR) {
			Uri uri = data != null ? data.getData() : null;
			if (uri != null) {
				String path = data.getData().getPath();
				addFileSet(path);
			}
		}
	}

	private void addFileSet(String path) {
		addFileSet(new File(path));
	}

	private void addFileSet(File dir) {
		if (!dir.exists()) {
			return;
		}
		addFileSet(dir.getAbsolutePath(), dir.listFiles(sDroppFilter));
	}

	private void addFileSet(String dirName, File[] fileArray) {
		List<File> fileList = Arrays.asList(fileArray);
		addFileSet(dirName, fileList);
	}

	private void addFileSet(String dirName, List<File> fileList) {
		Log.d(TAG, HelperUtil.getStackName());

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
		if (fileList.size() == 0) {
			View appNotExistView = inflater.inflate(R.layout.files_not_exists,
					null);
			layout.addView(appNotExistView);
		} else {
			droppList.setOnItemClickListener(mClickEventImpl);
			droppList.setOnItemLongClickListener(mLongClickEventImpl);
		}
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			File file = (File) parent.getItemAtPosition(position);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.parse("file://" + file.getAbsolutePath()), TYPE);

			startActivity(intent);
		}
	}

	private class OnItemLongClickListenerImpl implements
			OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			File file = (File) parent.getItemAtPosition(position);
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType(TYPE);
			intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"
					+ file.getAbsolutePath()));

			startActivity(intent);

			return false;
		}
	}
}
