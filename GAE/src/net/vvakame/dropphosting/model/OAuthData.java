package net.vvakame.dropphosting.model;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

@Model
public class OAuthData {

	@Attribute(primaryKey = true)
	private Key key = null;

	private String screenName = null;
	private Integer oauthHashCode = null;

	public void createKey() {
		key = Datastore.createKey(OAuthData.class, screenName);
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

	public Integer getOauthHashCode() {
		return oauthHashCode;
	}

	public void setOauthHashCode(Integer oauthHashCode) {
		this.oauthHashCode = oauthHashCode;
	}

	public static void checkState(OAuthData oauth) {
		if (oauth == null) {
			throw new IllegalArgumentException("oauth data is not included!");
		} else if (oauth.getScreenName() == null) {
			throw new IllegalArgumentException("screenname is not included!");
		} else if (oauth.getOauthHashCode() == null) {
			throw new IllegalArgumentException("OAuthHash is not included!");
		}

		oauth.createKey();
	}

	@Override
	public boolean equals(Object obj) {
		OAuthData oauth = null;
		if (obj == null) {
			return false;
		} else if (!(obj instanceof OAuthData)) {
			return false;
		}

		oauth = (OAuthData) obj;
		if (screenName == null && oauth.getScreenName() == null
				&& oauthHashCode == null && oauth.getOauthHashCode() == null) {
			return true;
		} else if (screenName != null && oauthHashCode != null) {
			return screenName.equals(oauth.getScreenName())
					&& oauthHashCode.equals(oauth.getOauthHashCode());
		} else if (screenName != null) {
			return screenName.equals(oauth.getScreenName())
					&& oauth.getOauthHashCode() == null;
		} else if (oauthHashCode != null) {
			return oauthHashCode.equals(oauth.getOauthHashCode())
					&& oauth.getScreenName() == null;
		}

		return false;
	}
}
