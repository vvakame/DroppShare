package net.vvakame.droppshare.activity;

import java.io.File;
import java.io.InvalidClassException;
import java.util.List;

import net.vvakame.android.helper.Closure;
import net.vvakame.android.helper.DrivenHandler;
import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.asynctask.DroppInstalledAsynkTask;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.AppDiffAdapter;
import net.vvakame.droppshare.helper.CacheUtil;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.helper.XmlUtil;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDiffData;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 指定されたデータファイルの内容を表示するActivity
 * 
 * @author vvakame
 */
public class DroppViewerActivity extends Activity implements LogTagIF {
	/** 受け付けるデータタイプ */
	public static final String TYPE = "application/droppshare";

	private final OnClickListenerImpl mEventImpl = new OnClickListenerImpl();

	private static final int DIALOG_PROGRESS = 1;

	private static final int MESSAGE_START_PROGRESS = 1;
	private static final int MESSAGE_FINISH_PROGRESS = 2;
	private static final int MESSAGE_FAILURE_SRC = 3;
	private static final int MESSAGE_FAILURE_DEST = 4;

	private ProgressDialog mProgDialog = null;
	private AppDiffAdapter mDiffAdapter = null;

	private DrivenHandler mHandler = new DrivenHandler(this, DIALOG_PROGRESS);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewer);

		Closure cloMain = new Closure() {
			public void exec() {
				ListView listView = (ListView) findViewById(R.id.list);
				listView.setAdapter(mDiffAdapter);
				listView.setOnItemClickListener(mEventImpl);
			}
		};

		Closure cloSrcFail = new Closure() {
			public void exec() {
				mProgDialog.setMessage(getString(R.string.read_failure_src));
			}
		};
		Closure cloDestFail = new Closure() {
			public void exec() {
				mProgDialog.setMessage(getString(R.string.read_failure_dest));
			}
		};

		mHandler.pushEventWithShowDialog(MESSAGE_START_PROGRESS, null);
		mHandler.pushEvent(MESSAGE_FAILURE_SRC, cloSrcFail);
		mHandler.pushEvent(MESSAGE_FAILURE_DEST, cloDestFail);
		mHandler.pushEventWithDissmiss(MESSAGE_FINISH_PROGRESS, cloMain);

		new Thread() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(MESSAGE_START_PROGRESS);

				Intent intent = getIntent();
				File destFile = new File(intent.getData().getPath());

				List<AppData> srcList = null;
				try {
					srcList = CacheUtil
							.readSerializedCaches(DroppInstalledAsynkTask.CACHE_FILE);
				} catch (InvalidClassException e) {
					mHandler.sendEmptyMessage(MESSAGE_FAILURE_SRC);
				} catch (ClassNotFoundException e) {
					mHandler.sendEmptyMessage(MESSAGE_FAILURE_SRC);
				}

				List<AppData> destList = null;
				destList = XmlUtil.readXmlCache(DroppViewerActivity.this,
						destFile);

				List<AppDiffData> diffList = AppDataUtil.zipAppData(srcList,
						destList);
				mDiffAdapter = new AppDiffAdapter(DroppViewerActivity.this,
						R.layout.diff_view, diffList);

				mHandler.sendEmptyMessage(MESSAGE_FINISH_PROGRESS);
			}

		}.start();
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
			progDialog.setTitle(getString(R.string.now_matchting_data));
			progDialog.setMessage(getString(R.string.wait_a_moment));
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setCancelable(false);

			break;
		default:
			break;
		}
	}

	private class OnClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			AppDiffData diff = (AppDiffData) parent.getItemAtPosition(position);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			String marketUri = AppDataUtil.getMarketUriFromAppData(diff
					.getMasterAppData());
			intent.setData(Uri.parse(marketUri));

			startActivity(intent);
		}
	}
}
