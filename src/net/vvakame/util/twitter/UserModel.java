package net.vvakame.util.twitter;

import java.io.Serializable;

/**
 * ユーザのデータを表す
 * 
 * @author vvakame
 */
public class UserModel implements Serializable {
	private static final long serialVersionUID = 2L;

	public static final String TABLE_NAME = "twitter";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SCREEN_NAME = "screen_name";
	public static final String COLUMN_FRIENDS_COUNT = "friends_count";

	public static final String COLUMN_FAVORITE = "favorite";

	public static final String FAVORITE_ON = "on";
	public static final String FAVORITE_OFF = null;

	private Long rowId = null;
	private String name = null;
	private String screenName = null;
	private Long friendsCount = null;

	private String favorite = null;

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public Long getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(Long friendsCount) {
		this.friendsCount = friendsCount;
	}

	public String getFavorite() {
		return favorite;
	}

	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}

	public void toggleFavorite() {
		if (favorite == FAVORITE_OFF) {
			favorite = FAVORITE_ON;
		} else {
			favorite = FAVORITE_OFF;
		}
	}

	public void updateFrom(UserModel model) {
		if (!this.screenName.equals(model.getScreenName())) {
			throw new IllegalArgumentException();
		}
		this.rowId = null;
		this.name = model.getName();
	}

	@Override
	public String toString() {
		StringBuilder stb = new StringBuilder();

		stb.append("rowId:").append(rowId).append(", ");
		stb.append("screenName:").append(screenName).append(", ");
		stb.append("name:").append(name).append(", ");
		if (favorite != null) {
			stb.append("favorite:").append(favorite);
		} else {
			stb.append("favorite:").append("off");
		}
		stb.append(".");

		return stb.toString();
	}
}
