package net.vvakame.droppshare.activity;

import java.util.regex.Pattern;

import net.vvakame.android.helper.Log;
import net.vvakame.droppshare.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Window;
import android.widget.TextView;

/**
 * market://に対応していないアプリから文字列を受け取りMarketへのリンクを貼るActivity
 * 
 * @author vvakame
 */
public class MarketRedirectActivity extends Activity {

	private static String marketProtcol = "market://";

	/** marketスキーマと認められる文字列に合致する正規表現 */
	private static final Pattern MARKET_PATTERN = Pattern
			.compile("market://(search|details)?[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d();

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
