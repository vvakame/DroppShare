package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.ActivityHelper;
import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.AppDataUtil;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.helper.ZXingIF;
import net.vvakame.droppshare.model.AppData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MenuDialogActivity extends Activity implements LogTagIF, ZXingIF {
	public static final String APP_DATA = "appData";
	public static final String RESULT = "result";

	private AppData mAppData = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		mAppData = (AppData) getIntent().getSerializableExtra(APP_DATA);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_dialog);

		EventQRCodeOnClick eventImpl = new EventQRCodeOnClick();

		findViewById(R.id.http).setOnClickListener(eventImpl);
		findViewById(R.id.market).setOnClickListener(eventImpl);
		findViewById(R.id.googl).setOnClickListener(eventImpl);
		findViewById(R.id.qr_code).setOnClickListener(eventImpl);

		findViewById(R.id.http).setOnTouchListener(eventImpl);
		findViewById(R.id.market).setOnTouchListener(eventImpl);
		findViewById(R.id.googl).setOnTouchListener(eventImpl);
		findViewById(R.id.qr_code).setOnTouchListener(eventImpl);
	}

	class EventQRCodeOnClick implements OnClickListener, OnTouchListener {

		@Override
		public void onClick(View v) {
			Intent data = null;

			switch (v.getId()) {
			case R.id.http:
			case R.id.market:
			case R.id.googl:
				data = new Intent();
				data.putExtra(RESULT, v.getId());
				data.putExtra(APP_DATA, mAppData);
				setResult(Activity.RESULT_OK, data);
				finish();

				break;

			case R.id.qr_code:
				data = new Intent();
				data.setAction(ACTION_ENCODE);
				data.putExtra(ENCODE_TYPE, ENCODE_TYPE_TEXT);
				data.putExtra(ENCODE_DATA, AppDataUtil
						.getHttpUriFromAppData(mAppData));

				boolean canResolve = ActivityHelper.canResolveActivity(
						MenuDialogActivity.this, data,
						getString(R.string.zxing_app_name),
						getString(R.string.zxing_package));
				if (canResolve) {
					startActivity(data);
					finish();
				}

				break;

			default:
				break;
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// StyleかThemeで出来る気もするけどわからなかったのでコードから実装

			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				v.setBackgroundColor(MenuDialogActivity.this.getResources()
						.getColor(R.color.weak));
			} else if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_OUTSIDE) {
				v.setBackgroundColor(MenuDialogActivity.this.getResources()
						.getColor(android.R.color.transparent));
			}

			return false;
		}
	}
}
