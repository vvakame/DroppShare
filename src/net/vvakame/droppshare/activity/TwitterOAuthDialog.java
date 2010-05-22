package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class TwitterOAuthDialog extends Activity implements LogTagIF,
		OnClickListener {

	public static final String PASSWORD = "password";
	public static final String SCREEN_NAME = "screenName";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setTitle(R.string.oauth_title);
		setContentView(R.layout.twitter_dialog);

		findViewById(R.id.ok_button).setOnClickListener(this);
		findViewById(R.id.cancel_button).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_button:
			EditText name = (EditText) findViewById(R.id.name);
			EditText pass = (EditText) findViewById(R.id.password);

			// TODO 入力値チェックするべき

			Intent data = new Intent();
			data.putExtra(SCREEN_NAME, name.getText().toString());
			data.putExtra(PASSWORD, pass.getText().toString());
			setResult(RESULT_OK, data);
			finish();

			break;

		case R.id.cancel_button:
			setResult(RESULT_CANCELED);
			finish();

			break;

		default:
			break;
		}
	}
}
