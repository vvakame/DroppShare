package net.vvakame.dropphosting.model;

public class OAuthData {

	private String screenName = null;
	private Integer oauthHashCode = null;

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
	}
}
