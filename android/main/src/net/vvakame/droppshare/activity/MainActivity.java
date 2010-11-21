package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.Log;
import net.vvakame.droppshare.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d();

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		findViewById(R.id.app_share).setOnClickListener(this);
		findViewById(R.id.hosting_upload).setOnClickListener(this);
		findViewById(R.id.drozip_selector).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;

		switch (v.getId()) {
		case R.id.app_share:
			intent = new Intent(this, AppShareActivity.class);
			startActivity(intent);

			break;

		case R.id.hosting_upload:
			intent = new Intent(this, HostingFrontendActivity.class);
			startActivity(intent);

			break;

		case R.id.drozip_selector:
			intent = new Intent(this, ListShareActivity.class);
			startActivity(intent);

			break;

		default:
			break;
		}
	}
}