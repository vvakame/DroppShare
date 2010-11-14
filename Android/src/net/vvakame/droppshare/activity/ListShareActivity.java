package net.vvakame.droppshare.activity;

import static net.vvakame.droppshare.model.SelectorHelper.installWatchingFile;
import static net.vvakame.droppshare.model.SelectorHelper.readWatchingFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.vvakame.android.helper.AppExistsUtil;
import net.vvakame.android.helper.Closure;
import net.vvakame.android.helper.DrivenHandler;
import net.vvakame.android.helper.AndroidUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.common.LogTagIF;
import net.vvakame.droppshare.common.OpenIntentIF;
import net.vvakame.droppshare.common.XmlUtil;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.FileListAdapter;
import net.vvakame.droppshare.task.DrozipInstalledAsyncTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * アプリ一覧読み込み元ファイル選択用Activity
 * 
 * @author vvakame
 */
public class ListShareActivity extends Activity implements LogTagIF,
		OpenIntentIF, OnClickListener {
	/** 探すデータファイルの拡張子 */
	private static final String SUFFIX = ".drozip";

	private static final int REQUEST_PICK_DIR = 0;

	private static final int DIALOG_PROGRESS = 1;

	private static final int MESSAGE_START_PROGRESS = 1;
	private static final int MESSAGE_FINISH_PROGRESS = 2;

	/** ".drozip"なファイルを探すフィルタ */
	private static final FilenameFilter sDroppFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String filename) {
			return filename.endsWith(SUFFIX);
		}
	};

	private final OnItemClickListenerImpl mClickEventImpl = new OnItemClickListenerImpl();
	private final OnItemLongClickListenerImpl mLongClickEventImpl = new OnItemLongClickListenerImpl();

	private ProgressDialog mProgDialog = null;
	private final DrivenHandler mHandler = new DrivenHandler(this,
			DIALOG_PROGRESS);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, AndroidUtil.getStackName());

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.selector);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.selector_titlebar);

		ImageView button = (ImageView) findViewById(R.id.gen_drozip);
		button.setOnClickListener(this);

		if (!Environment.getExternalStorageDirectory().canRead()) {
			Toast.makeText(this,
					getString(R.string.external_storage_not_exists),
					Toast.LENGTH_LONG).show();
		}

		// ファイルの作成
		installWatchingFile(this);

		// /sdcard/DroppShare/caches
		if (!XmlUtil.DATA_DIR.exists()) {
			XmlUtil.DATA_DIR.mkdirs();
		}

		init();
	}

	private void init() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.dirs_layout);
		layout.removeAllViews();

		addFileSet(XmlUtil.DATA_DIR);

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
		case R.id.gen_drozip:

			askFilename();

			break;

		case R.id.add_dir:

			intent = new Intent(ACTION_PICK_DIRECTORY);
			intent.setData(Uri.parse("file://"
					+ Environment.getExternalStorageDirectory()
							.getAbsolutePath()));
			intent.putExtra(TITLE, getString(R.string.add_dir_title));
			intent.putExtra(BUTTON_TEXT,
					getString(R.string.add_dir_button_text));

			boolean canResolve = AppExistsUtil.canResolveActivity(this, intent,
					getString(R.string.oi_file_manager_app_name),
					getString(R.string.oi_file_manager_package));

			if (canResolve) {
				startActivityForResult(intent, REQUEST_PICK_DIR);
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
		Log.d(TAG,
				AndroidUtil.getStackName() + ", req=" + requestCode + ", res="
						+ resultCode + ", data="
						+ (data != null ? data.getDataString() : "none"));

		if (requestCode == REQUEST_PICK_DIR) {
			Uri uri = data != null ? data.getData() : null;
			if (uri != null) {
				String path = data.getData().getPath();
				addFileSet(path);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			mProgDialog = new ProgressDialog(this);
			onPrepareDialog(id, mProgDialog);

			return mProgDialog;
		default:
			break;
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_PROGRESS:
			ProgressDialog progDialog = (ProgressDialog) dialog;
			progDialog.setTitle(getString(R.string.now_compress_data));
			progDialog.setMessage(getString(R.string.wait_a_moment));
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setCancelable(false);

			break;
		default:
			break;
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
		Log.d(TAG, AndroidUtil.getStackName());

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

	private void askFilename() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.ask_filename, null);

		EditText text = (EditText) layout.findViewById(R.id.name);
		text.setText(PreferencesActivity.getDrozipName(this));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.gen_drozip_title));
		builder.setView(layout);

		builder.setPositiveButton(getString(R.string.gen_drozip_positive),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog alDialog = (AlertDialog) dialog;
						EditText text = (EditText) alDialog.getWindow()
								.findViewById(R.id.name);
						String value = text.getText().toString();

						if ("".equals(value)) {
							return;
						}

						PreferencesActivity.setDrozipName(
								ListShareActivity.this, value);
						dialog.dismiss();

						genDrozip();
					}
				});

		builder.setNegativeButton(getString(R.string.gen_drozip_negative),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setCancelable(true);

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void genDrozip() {

		Closure clo = new Closure() {
			@Override
			public void exec() {
				init();
			}
		};

		mHandler.pushEventWithShowDialog(MESSAGE_START_PROGRESS, null);
		mHandler.pushEventWithDissmiss(MESSAGE_FINISH_PROGRESS, clo);

		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(MESSAGE_START_PROGRESS);

				// 同期実行
				DrozipInstalledAsyncTask async = new DrozipInstalledAsyncTask(
						ListShareActivity.this, null);

				List<AppData> appList = null;
				try {
					appList = async.execute(false).get();
				} catch (InterruptedException e) {
					Log.d(TAG, AndroidUtil.getExceptionLog(e));
				} catch (ExecutionException e) {
					Log.d(TAG, AndroidUtil.getExceptionLog(e));
				}

				XmlUtil.writeXmlCache(ListShareActivity.this,
						PreferencesActivity
								.getDrozipName(ListShareActivity.this), appList);

				mHandler.sendEmptyMessage(MESSAGE_FINISH_PROGRESS);
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		askFilename();
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			File file = (File) parent.getItemAtPosition(position);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.parse("file://" + file.getAbsolutePath()),
					DiffViewerActivity.TYPE);

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
			intent.setType(DiffViewerActivity.TYPE);
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + file.getAbsolutePath()));

			startActivity(intent);

			return false;
		}
	}
}
