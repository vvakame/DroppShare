package jp.ne.hatena.vvakame.droppshare;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DroppShare extends Activity implements OnClickListener {
	private Handler mHandler = null;
	private List<ResolveInfo> mInfoList = null;
	private PackageManager mPm = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button testButton = (Button) findViewById(R.id.sample_button);
		testButton.setOnClickListener(this);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				ApplicationInfo ai = (ApplicationInfo) msg.obj;

				ImageView imgView = (ImageView) findViewById(R.id.sample_view);
				imgView.setBackgroundDrawable(ai.loadIcon(mPm));

				TextView appNameText = (TextView) findViewById(R.id.app_name_text);
				appNameText.setText(mPm.getApplicationLabel(ai));
			}
		};
	}

	@Override
	public void onClick(View v) {
		mPm = getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		mInfoList = mPm.queryIntentActivities(intent, 0);

		Thread th = new Thread() {
			public void run() {
				for (ResolveInfo app : mInfoList) {
					try {
						ApplicationInfo ai = mPm.getApplicationInfo(
								app.activityInfo.packageName,
								PackageManager.GET_ACTIVITIES);
						Message msg = Message.obtain(mHandler);
						msg.obj = ai;
						msg.sendToTarget();
						sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		};
		th.start();
	}
}