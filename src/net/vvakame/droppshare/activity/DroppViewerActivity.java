package net.vvakame.droppshare.activity;

import java.io.File;
import java.io.InvalidClassException;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.asynctask.DroppInstalledAsynkTask;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.AppDiffAdapter;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDiffData;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DroppViewerActivity extends Activity implements LogTagIF {

	private final OnClickListenerImpl mEventImpl = new OnClickListenerImpl();

	private static final int DIALOG_PROGRESS = 1;

	private static final int MESSAGE_MATCHING = 1;
	private static final int MESSAGE_FAILURE_SRC = 2;
	private static final int MESSAGE_FAILURE_DEST = 3;

	private boolean mDone = false;
	private ProgressDialog mProgDialog = null;
	private AppDiffAdapter mDiffAdapter = null;

	private Handler mProgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_MATCHING:
				if (mDone) {
					ListView listView = (ListView) findViewById(R.id.list);
					listView.setAdapter(mDiffAdapter);
					listView.setOnItemClickListener(mEventImpl);

					dismissDialog(DIALOG_PROGRESS);
				} else {
					mProgHandler.sendEmptyMessageDelayed(MESSAGE_MATCHING, 100);
				}

				break;
			case MESSAGE_FAILURE_SRC:
				// TODO not tested
				mProgDialog.setMessage(getString(R.string.read_failure_src));
				break;
			case MESSAGE_FAILURE_DEST:
				// TODO not tested
				mProgDialog.setMessage(getString(R.string.read_failure_dest));
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewer);

		showDialog(DIALOG_PROGRESS);
		mProgHandler.sendEmptyMessage(MESSAGE_MATCHING);

		new Thread() {
			@Override
			public void run() {
				Intent intent = getIntent();
				File destFile = new File(intent.getData().getPath());

				List<AppData> srcList = null;
				try {
					srcList = AppDataUtil
							.readSerializedCaches(DroppInstalledAsynkTask.CACHE_FILE);
				} catch (InvalidClassException e) {
					mProgHandler.sendEmptyMessage(MESSAGE_FAILURE_SRC);
				} catch (ClassNotFoundException e) {
					mProgHandler.sendEmptyMessage(MESSAGE_FAILURE_SRC);
				}

				List<AppData> destList = null;
				try {
					destList = AppDataUtil.readSerializedCaches(destFile);
				} catch (InvalidClassException e) {
					mProgHandler.sendEmptyMessage(MESSAGE_FAILURE_DEST);
				} catch (ClassNotFoundException e) {
					mProgHandler.sendEmptyMessage(MESSAGE_FAILURE_DEST);
				}

				List<AppDiffData> diffList = AppDataUtil.zipAppData(srcList,
						destList);
				mDiffAdapter = new AppDiffAdapter(DroppViewerActivity.this,
						R.layout.diff_view, diffList);

				mDone = true;
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

	// TODO いらなかったら消す
	public void onWindowFocusChangged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		ListView listView = (ListView) findViewById(R.id.list);

		for (int i = 0; i < listView.getChildCount(); i++) {
			View view = listView.getChildAt(i);
			View parent = view.findViewById(R.id.boss);
			ImageView label = null;
			label = (ImageView) view.findViewById(R.id.side_1);
			label.setMinimumHeight(parent.getHeight());
			label.forceLayout();
			label = (ImageView) view.findViewById(R.id.side_2);
			label.setMinimumHeight(parent.getHeight());
			label.forceLayout();
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
