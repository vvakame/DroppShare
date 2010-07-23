package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.LogTagIF;
import net.vvakame.droppshare.helper.OAuthHelper;
import net.vvakame.droppshare.model.OAuthData;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class HostingFrontendActivity extends Activity implements LogTagIF,
		OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hosting_frontend);

		findViewById(R.id.authorize).setOnClickListener(this);
		findViewById(R.id.remove_authorize).setOnClickListener(this);

		switchViewEnable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		switch (item.getItemId()) {
		case R.id.create:

			break;

		default:
			ret = super.onOptionsItemSelected(item);
			break;
		}
		return ret;
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;

		switch (v.getId()) {
		case R.id.authorize:
			intent = new Intent(this, TwitterOAuthActivity.class);
			startActivityForResult(intent, 0);
			break;

		case R.id.remove_authorize:
			OAuthHelper.removeOAuth(this);
			switchViewEnable();
			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			switchViewEnable();
		}
	}

	private void switchViewEnable() {

		Button authorizeButton = (Button) findViewById(R.id.authorize);
		ImageButton removeButton = (ImageButton) findViewById(R.id.remove_authorize);

		OAuthData oauth = OAuthHelper.restoreOAuth(this);
		if (oauth != null) {
			authorizeButton.setEnabled(false);
			authorizeButton.setText(getString(R.string.authorized, oauth
					.getScreenName()));
			removeButton.setEnabled(true);
		} else {
			authorizeButton.setEnabled(true);
			authorizeButton.setText(getString(R.string.not_authorized));
			removeButton.setEnabled(false);
		}
	}

}
