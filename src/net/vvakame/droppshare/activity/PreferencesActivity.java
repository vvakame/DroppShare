package net.vvakame.droppshare.activity;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
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
}
