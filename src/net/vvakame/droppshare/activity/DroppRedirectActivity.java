package net.vvakame.droppshare.activity;

import java.util.regex.Pattern;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class DroppRedirectActivity extends Activity implements LogTagIF {

	private static String marketProtcol = "market://";
	private static final Pattern MARKET_PATTERN = Pattern
			.compile("market://[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+[a-zA-Z]");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.redirect_to_market);

		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		String receiveStr = data.getString(Intent.EXTRA_TEXT);

		TextView receiveText = (TextView) findViewById(R.id.receive_text);
		receiveText.setText(receiveStr);
		Linkify.addLinks(receiveText, MARKET_PATTERN, marketProtcol);
	}
}
