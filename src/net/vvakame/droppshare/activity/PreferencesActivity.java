package net.vvakame.droppshare.activity;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

public class PreferencesActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
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

	public static String getMessageTemplate(Context con) {
		String key = "message_template";
		String messageTemplate = con.getString(R.string.message_added);
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, messageTemplate);
	}
}
