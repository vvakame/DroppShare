package net.vvakame.dropphosting.model;

public class UploadData {
	private TwitterOAuthData oauth = null;
	private String variant = null;
	private byte[] zipData = null;

	public TwitterOAuthData getOauth() {
		return oauth;
	}

	public void setOauth(TwitterOAuthData oauth) {
		this.oauth = oauth;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getVariant() {
		return variant;
	}

	public byte[] getZipData() {
		return zipData;
	}

	public void setZipData(byte[] zipData) {
		this.zipData = zipData;
	}

	public static void chechState(UploadData upData) {
		if (upData == null) {
			throw new IllegalArgumentException("upload data is not included!");
		} else if (upData.getOauth() == null) {
			throw new IllegalArgumentException("oauth data is not included!");
		} else if (upData.getZipData() == null) {
			throw new IllegalArgumentException("drozip data is not included!");
		} else if (upData.getVariant() == null) {
			upData.setVariant("default");
		} else {
			TwitterOAuthData.checkState(upData.getOauth());
		}
	}
}
