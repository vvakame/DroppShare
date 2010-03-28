package net.vvakame.util.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Twitterとお話をします。ごにょごにょ。
 * 
 * @author vvakame
 */
public class TwitterAgent {

	/** Twitterから受け取るデータのうち、User部分の固まりの始まりのタグ */
	public static final String TAG_USERS = "users";
	/** Twitterから受け取るデータのうち、次のデータのカーソル */
	public static final String TAG_NEXT_CURSOR = "next_cursor";
	/** Twitterから受け取るデータのうち、エラーメッセージ */
	public static final String TAG_ERROR = "error";

	/** ユーザデータのうち、nameのタグ */
	public static final String NAME = "name";
	/** ユーザデータのうち、screen_nameのタグ */
	public static final String SCREEN_NAME = "screen_name";
	/** ユーザデータのうち、フォロりの人数のタグ */
	public static final String FRIENDS_COUNT = "friends_count";

	/** カーソルにこの値を指定すると、データの先頭を指す */
	public static final long INITIAL_CURSOL = -1;
	/** カーソルにこの値を指定すると、データの最後尾に到達したことを指す */
	public static final long END_CURSOL = 0;

	/** Twitterから受け取るデータの1回の通信あたりの分量 */
	private static final int GET_COUNT = 50;

	/**
	 * ユーザと次のカーソルを一緒に返すためのTuple的なクラス
	 * 
	 * @author vvakame
	 */
	public class TwitterResponse {
		private List<UserModel> userList = null;
		private long nextCursor = -1;

		public List<UserModel> getUserList() {
			return userList;
		}

		public void setUserList(List<UserModel> userList) {
			this.userList = userList;
		}

		public long getNextCursor() {
			return nextCursor;
		}

		public void setNextCursor(long nextCursor) {
			this.nextCursor = nextCursor;
		}
	}

	/**
	 * 通常のコンストラクタ
	 */
	public TwitterAgent() {
	}

	/**
	 * 指定されたユーザのフォロり一覧を取得する
	 * 
	 * @param screenName
	 *            フォロり一覧を取得したいユーザ
	 * @return フォロり一覧と次のデータへのカーソル
	 * @throws IOException
	 * @throws JSONException
	 */
	public TwitterResponse getFriendsStatus(String screenName)
			throws IOException, JSONException {
		return getFriendsStatus(screenName, INITIAL_CURSOL);
	}

	/**
	 * 指定されたユーザのフォロり一覧を取得する
	 * 
	 * @param screenName
	 *            フォロり一覧を取得したいユーザ
	 * @return フォロり一覧と次のデータへのカーソル
	 * @throws IOException
	 * @throws JSONException
	 */
	public TwitterResponse getFriendsStatus(String screenName, long cursor)
			throws IOException, JSONException {
		URL url = null;
		try {
			if (screenName == null || "".equals(screenName)) {
				throw new IllegalArgumentException();
			}
			StringBuilder stb = new StringBuilder();
			stb.append("http://api.twitter.com/1/statuses/friends/");
			stb.append(screenName);
			stb.append(".json?cursor=");
			stb.append(cursor);
			stb.append("&count=");
			stb.append(GET_COUNT);

			url = new URL(stb.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		URLConnection connect = url.openConnection();
		InputStream is = connect.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		StringBuilder stb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			stb.append(line);
		}

		JSONObject json = new JSONObject(stb.toString());

		JSONArray jsons = json.getJSONArray(TAG_USERS);

		List<UserModel> friendsList = new ArrayList<UserModel>();
		for (int i = 0; i < jsons.length(); i++) {
			JSONObject userJson = jsons.getJSONObject(i);

			UserModel user = constructUser(userJson);
			friendsList.add(user);
		}

		TwitterResponse res = new TwitterResponse();
		res.setNextCursor(json.getLong(TAG_NEXT_CURSOR));
		res.setUserList(friendsList);

		return res;
	}

	/**
	 * 指定されたユーザについての情報を取得します
	 * 
	 * @param screenName
	 *            情報を取得したいユーザ
	 * @return ユーザデータ
	 * @throws IOException
	 * @throws JSONException
	 */
	public UserModel getShowUser(String screenName) throws IOException,
			JSONException {
		URL url = null;
		try {
			if (screenName == null || "".equals(screenName)) {
				throw new IllegalArgumentException();
			}
			url = new URL(
					"http://api.twitter.com/1/users/show.json?screen_name="
							+ screenName);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		URLConnection connect = url.openConnection();
		InputStream is = connect.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(isr);

		StringBuilder stb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			stb.append(line);
		}

		JSONObject userJson = new JSONObject(stb.toString());

		UserModel user = constructUser(userJson);

		return user;
	}

	private UserModel constructUser(JSONObject userJson) throws JSONException {
		UserModel user = new UserModel();

		user.setScreenName(userJson.getString(SCREEN_NAME));
		user.setName(userJson.getString(NAME));
		user.setFriendsCount(userJson.getLong(FRIENDS_COUNT));

		return user;
	}
}
