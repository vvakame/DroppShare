package net.vvakame.drphost.service;

import java.util.Locale;
import java.util.ResourceBundle;

import net.vvakame.drphost.meta.TwitterOAuthMeta;
import net.vvakame.drphost.model.TwitterOAuth;

import org.slim3.datastore.Datastore;
import org.slim3.util.AppEngineUtil;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;

import com.google.appengine.api.datastore.Key;
import com.google.apphosting.api.ApiProxy;

/**
 * Twitter回りのWapperクラス.
 * 
 * @author vvakame
 */
public class TwitterService {

	public static final String callbackUrl;

	private static TwitterFactory twitFactory;
	private static Configuration config;

	static {
		ResourceBundle rb = ResourceBundle.getBundle("twitter",
				Locale.getDefault());

		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		confbuilder.setOAuthConsumerKey(rb.getString("consumer_key"));
		confbuilder.setOAuthConsumerSecret(rb.getString("consumer_secret"));
		config = confbuilder.build();

		if (AppEngineUtil.isProduction()) {
			String applicationName = ApiProxy.getCurrentEnvironment()
					.getAppId();
			String versionId = ApiProxy.getCurrentEnvironment().getVersionId();
			callbackUrl = "http://" + versionId + "latest." + applicationName
					+ ".appspot.com/twitter/callback";
		} else {
			callbackUrl = "http://localhost:8888/twitter/callback";
		}

		twitFactory = new TwitterFactory(config);
	}

	private TwitterService() {
	}

	/**
	 * {@link TwitterOAuth}のKeyを取得します
	 * 
	 * @param twitterId
	 * @return
	 */
	public static Key createKey(int twitterId) {
		return Datastore.createKey(TwitterOAuthMeta.get(), twitterId);
	}

	/**
	 * 未承認の {@link Twitter} インスタンスを返します.
	 * 
	 * @return 未承認のインスタンス
	 */
	public static Twitter getInstance() {
		return twitFactory.getInstance();
	}

	/**
	 * 承認済の {@link Twitter} インスタンスを返します.<br>
	 * 普段は {@link TwitterService#getInstance(TwitterOAuth)} を使ったほうがよいでしょう.
	 * 
	 * @param accessToken
	 *            Twitter4Jの {@link AccessToken}
	 * @return 承認済のインスタンス
	 */
	public static Twitter getInstance(AccessToken accessToken) {
		if (accessToken == null) {
			new IllegalArgumentException("accessToken is null.");
		}
		return twitFactory.getOAuthAuthorizedInstance(accessToken);
	}

	/**
	 * 承認済の {@link Twitter} インスタンスを返します.
	 * 
	 * @param oauth
	 * @return 承認済のインスタンス
	 */
	public static Twitter getInstance(TwitterOAuth oauth) {
		if (oauth == null || oauth.getToken() == null
				|| oauth.getTokenSecret() == null) {
			throw new IllegalArgumentException(
					"oauth or oauth token or oauth token secret is null.");
		}
		return getInstance(new AccessToken(oauth.getToken(),
				oauth.getTokenSecret()));
	}
}
