package net.vvakame.droppshare.activity;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.helper.HelperUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class PreferencesActivity extends PreferenceActivity {
	private static final String TAG = PreferencesActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);

		CheckBoxPreference checkbox = (CheckBoxPreference) findPreference("create_cache_automatically");
		checkbox
				.setOnPreferenceChangeListener(new CreateCachePreferenceChange());
	}

	public static String getUriType(Context con) {
		String key = "uti_types";
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, "http");
	}

	public static boolean isHttp(Context con) {
		return "http".equalsIgnoreCase(getUriType(con));
	}

	public static boolean isMarket(Context con) {
		return "market".equalsIgnoreCase(getUriType(con));
	}

	public static boolean isUriShorten(Context con) {
		String key = "uri_shorten";
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				key, true);
	}

	public static boolean isAllowAutoCache(Context con) {
		String key = "create_cache_automatically";
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				key, true);
	}

	public static String getMessageTemplate(Context con) {
		String key = "message_template";
		String messageTemplate = con.getString(R.string.message_added);
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, messageTemplate);
	}

	private class CreateCachePreferenceChange implements
			OnPreferenceChangeListener {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", newValue="
					+ newValue);
			Intent intent = new Intent(PreferencesActivity.this,
					DroppKickReceiverService.class);
			intent.putExtra(DroppKickReceiverService.REGIST_FLG,
					((Boolean) newValue).booleanValue());
			startService(intent);

			return true;
		}
	}
}
