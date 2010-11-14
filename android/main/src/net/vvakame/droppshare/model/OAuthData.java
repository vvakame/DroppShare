package net.vvakame.droppshare.model;

public class OAuthData {

	private String screenName = null;
	private Long oauthHashCode = null;

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public Long getOauthHashCode() {
		return oauthHashCode;
	}

	public void setOauthHashCode(long oauthHashCode) {
		this.oauthHashCode = oauthHashCode;
	}
}
