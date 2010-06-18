package net.vvakame.droppshare.activity;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class TwitterOAuthDialog extends Activity implements LogTagIF,
		OnClickListener, TextWatcher {

	public static final String PASSWORD = "password";
	public static final String SCREEN_NAME = "screenName";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		setTitle(R.string.oauth_title);
		setContentView(R.layout.twitter_dialog);

		String screenName = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("screen_name", "");

		EditText editName = (EditText) findViewById(R.id.name);
		editName.setText(screenName);

		EditText nameEdit = (EditText) findViewById(R.id.name);
		EditText passEdit = (EditText) findViewById(R.id.password);
		nameEdit.addTextChangedListener(this);
		passEdit.addTextChangedListener(this);

		findViewById(R.id.ok_button).setOnClickListener(this);
		findViewById(R.id.cancel_button).setOnClickListener(this);

		toggleOkEnable();

		if (!"".equals(nameEdit.getText())) {
			passEdit.requestFocus();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_button:
			EditText name = (EditText) findViewById(R.id.name);
			EditText pass = (EditText) findViewById(R.id.password);

			// TODO 入力値チェックするべき

			EditText editName = (EditText) findViewById(R.id.name);
			String screenName = editName.getText().toString();
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
					.edit();
			editor.putString("screen_name", screenName);
			editor.commit();

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

	@Override
	public void afterTextChanged(Editable s) {
		toggleOkEnable();
	}

	private void toggleOkEnable() {
		EditText nameEdit = (EditText) findViewById(R.id.name);
		EditText passEdit = (EditText) findViewById(R.id.password);

		String name = nameEdit.getText().toString();
		String pass = passEdit.getText().toString();

		if (!"".equals(name) && !"".equals(pass)) {
			findViewById(R.id.ok_button).setEnabled(true);
		} else {
			findViewById(R.id.ok_button).setEnabled(false);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
