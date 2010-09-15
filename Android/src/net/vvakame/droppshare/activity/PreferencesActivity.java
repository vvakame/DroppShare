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
	private static final String MESSAGE_TEMPLATE = "message_template";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		patchV08(this);

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

	public static String getUriType(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(URI_TYPES, "http");
	}

	public static boolean isHttp(Context context) {
		return "http".equalsIgnoreCase(getUriType(context));
	}

	public static boolean isMarket(Context context) {
		return "market".equalsIgnoreCase(getUriType(context));
	}

	public static String getUriShortenAgent(Context context) {
		patchV08(context);
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(URI_SHORTEN, "goo.gl");
	}

	public static String getMessageTemplate(Context context) {
		String messageTemplate = context.getString(R.string.message_added);
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(MESSAGE_TEMPLATE, messageTemplate);
	}

	public static String getDrozipName(Context context) {
		String key = "drozip_name";
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(key, "archive");
	}

	public static boolean setDrozipName(Context con, String name) {
		String key = "drozip_name";
		Editor editor = PreferenceManager.getDefaultSharedPreferences(con)
				.edit();
		editor.putString(key, name);
		return editor.commit();
	}

	private static void patchV08(Context context) {
		// v0.7→v0.8でCheckboxPreferenceからListPreferenceに変更したのの対応
		Object tmp = PreferenceManager.getDefaultSharedPreferences(context)
				.getAll().get(URI_SHORTEN);
		if (tmp instanceof Boolean) {
			boolean shorten = ((Boolean) tmp).booleanValue();

			Editor editor = PreferenceManager.getDefaultSharedPreferences(
					context).edit();
			editor.remove(URI_SHORTEN);
			editor.putString(URI_SHORTEN, shorten ? "goo.gl" : "none");
			editor.commit();
		}
	}
}
