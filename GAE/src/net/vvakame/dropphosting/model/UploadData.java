package net.vvakame.dropphosting.model;

public class UploadData {
	private OAuthData oauth = null;
	private byte[] zipData = null;

	public OAuthData getOauth() {
		return oauth;
	}

	public void setOauth(OAuthData oauth) {
		this.oauth = oauth;
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
		} else {
			OAuthData.checkState(upData.getOauth());
		}
	}
}
