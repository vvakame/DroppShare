package net.vvakame.droppshare.hosting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class OAuthHelper {
	public static void saveOAuth(Context context, OAuthData oauth) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		editor.putString("screen_name", oauth.getScreenName());
		editor.putLong("oauth_hashcode", oauth.getOauthHashCode());
		editor.commit();
	}

	public static OAuthData restoreOAuth(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		OAuthData oauth = new OAuthData();
		oauth.setScreenName(pref.getString("screen_name", null));
		oauth.setOauthHashCode(pref.getLong("oauth_hashcode", 0));

		return oauth.getScreenName() != null && oauth.getOauthHashCode() != 0 ? oauth
				: null;
	}

	public static void removeOAuth(Context context) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context)
				.edit();
		editor.remove("screen_name");
		editor.remove("oauth_hashcode");
		editor.commit();
	}
}
