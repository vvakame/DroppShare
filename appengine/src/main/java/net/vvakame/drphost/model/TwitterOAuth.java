package net.vvakame.drphost.model;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * TwitterのOAuthTokenを保持しておくためのクラス. <br>
 * Key = TwitterID
 * 
 * @author vvakame
 */
@Model
public class TwitterOAuth {

	@Attribute(primaryKey = true)
	private Key key;

	@Attribute(unindexed = true)
	private Long hash;

	@Attribute(unindexed = true)
	private String token;

	@Attribute(unindexed = true)
	private String tokenSecret;

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return the hash
	 */
	public Long getHash() {
		return hash;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(Long hash) {
		this.hash = hash;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the tokenSecret
	 */
	public String getTokenSecret() {
		return tokenSecret;
	}

	/**
	 * @param tokenSecret
	 *            the tokenSecret to set
	 */
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

}
