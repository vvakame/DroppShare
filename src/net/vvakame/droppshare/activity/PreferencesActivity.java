package net.vvakame.droppshare.activity;

import net.vvakame.droppshare.R;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * 設定用Activity
 * 
 * @author vvakame
 */
public class PreferencesActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {

	private static final String URI_TYPES = "uri_types";
	private static final String URI_SHORTEN = "uri_shorten";
	private static final String CREATE_CACHE_AUTOMATICALLY = "create_cache_automatically";
	private static final String MESSAGE_TEMPLATE = "message_template";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// v0.7→v0.8でCheckboxPreferenceからListPreferenceに変更したのの対応
		Object tmp = PreferenceManager.getDefaultSharedPreferences(this)
				.getAll().get(URI_SHORTEN);
		if (tmp instanceof Boolean) {
			boolean shorten = ((Boolean) tmp).booleanValue();

			Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
					.edit();
			editor.remove(URI_SHORTEN);
			editor.putString(URI_SHORTEN, shorten ? "goo.gl" : "none");
			editor.commit();
		}

		addPreferencesFromResource(R.xml.pref);

		findPreference(URI_TYPES).setOnPreferenceChangeListener(this);
		findPreference(URI_SHORTEN).setEnabled("http".equals(getUriType(this)));
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (URI_TYPES.equals(preference.getKey())) {
			findPreference(URI_SHORTEN).setEnabled("http".equals(newValue));
		}

		return true;
	}

	public static String getUriType(Context con) {
		String key = URI_TYPES;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, "http");
	}

	public static boolean isHttp(Context con) {
		return "http".equalsIgnoreCase(getUriType(con));
	}

	public static boolean isMarket(Context con) {
		return "market".equalsIgnoreCase(getUriType(con));
	}

	public static String getUriShortenAgent(Context con) {
		String key = URI_SHORTEN;
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, "goo.gl");
	}

	public static boolean isAllowAutoCache(Context con) {
		String key = CREATE_CACHE_AUTOMATICALLY;
		return PreferenceManager.getDefaultSharedPreferences(con).getBoolean(
				key, true);
	}

	public static String getMessageTemplate(Context con) {
		String key = MESSAGE_TEMPLATE;
		String messageTemplate = con.getString(R.string.message_added);
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, messageTemplate);
	}

	public static String getDrozipName(Context con) {
		String key = "drozip_name";
		return PreferenceManager.getDefaultSharedPreferences(con).getString(
				key, "archive");
	}

	public static boolean setDrozipName(Context con, String name) {
		String key = "drozip_name";
		Editor editor = PreferenceManager.getDefaultSharedPreferences(con)
				.edit();
		editor.putString(key, name);
		return editor.commit();
	}
}
