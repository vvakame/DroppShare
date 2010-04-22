package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DroppViewerActivity extends Activity implements LogTagIF {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.redirect_to_market);

		Intent intent = getIntent();
		intent.toString();
	}
}
