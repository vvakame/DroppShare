package net.vvakame.drphost.model;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

import twitter4j.http.AccessToken;

@Model
public class TwitterOAuthData {

	@Attribute(primaryKey = true)
	private Key key = null;

	private String screenName = null;
	@Attribute(lob = true)
	private AccessToken accessToken = null;
	// Android側からあげられてくるhashCode
	private Integer oauthHashCode = null;

	public void createKey() {
		key = Datastore.createKey(TwitterOAuthData.class, screenName);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	public void setOauthHashCode(Integer oauthHashCode) {
		this.oauthHashCode = oauthHashCode;
	}

	public Integer getOauthHashCode() {
		return oauthHashCode;
	}

	public static void checkState(TwitterOAuthData twitAuth) {
		if (twitAuth.getScreenName() == null) {
			throw new IllegalStateException("screenName is null");
		}
		if (twitAuth.getAccessToken() == null) {
			throw new IllegalStateException("access token is null");
		}
		if (twitAuth.getOauthHashCode() == null) {
			twitAuth.setOauthHashCode(twitAuth.getAccessToken().hashCode());
		}
		twitAuth.createKey();
	}

}
