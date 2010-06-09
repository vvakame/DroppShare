package net.vvakame.dropphosting.model;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

import twitter4j.http.AccessToken;

@Model
public class TwitterAuthorizedData {

	@Attribute(primaryKey = true)
	private Key key = null;

	private String screenName = null;
	@Attribute(lob = true)
	private AccessToken accessToken = null;

	public void createKey() {
		key = Datastore.createKey(TwitterAuthorizedData.class, screenName);
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
}
