package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.vvakame.dropphosting.meta.OAuthDataMeta;
import net.vvakame.dropphosting.model.OAuthData;
import net.vvakame.dropphosting.model.TwitterAuthorizedData;

import org.slim3.datastore.Datastore;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.google.appengine.api.datastore.Key;

public class OAuthServlet extends HttpServlet {

	private static final long serialVersionUID = 2332845261180385478L;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String PROP_TWITTER = "twitter";

	private static final String SESSION_TOKEN = "token";
	private static final String SESSION_TOKEN_SECRET = "tokenSecret";

	private static final String PARAM_OAUTH_VERIFIER = "oauth_verifier";

	private static final String PROP_DROP = "dropp";
	private static boolean DEBUG = false;

	private static String callbackUrl;
	private static String consumerKey;
	private static String consumerSecret;

	private static TwitterFactory twiFac;

	public void init() throws ServletException {

		ResourceBundle rb = ResourceBundle.getBundle(PROP_TWITTER, Locale
				.getDefault());
		callbackUrl = rb.getString("callback_url");
		consumerKey = rb.getString("consumer_key");
		consumerSecret = rb.getString("consumer_secret");

		twiFac = new TwitterFactory();

		rb = ResourceBundle.getBundle(PROP_DROP, Locale.getDefault());
		DEBUG = Boolean.parseBoolean(rb.getString("debug_mode"));
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		HttpSession session = req.getSession();

		String oauthVerifier = req.getParameter(PARAM_OAUTH_VERIFIER);
		String token = (String) session.getAttribute(SESSION_TOKEN);
		String tokenSecret = (String) session
				.getAttribute(SESSION_TOKEN_SECRET);

		if (DEBUG) {
			logger.info("DEBUG mode.");
			processDebug(res);

		} else if (oauthVerifier != null) {
			logger.info("process callback.");
			processCallback(res, session, token, tokenSecret, oauthVerifier);

		} else if (token != null && tokenSecret != null) {
			logger.info("process authorized.");
			processAuthorized(res, session, token, tokenSecret);

		} else {
			logger.info("process first process.");
			processFirstTime(res, session);
		}
	}

	private void processDebug(HttpServletResponse res) throws IOException {
		OAuthData data = new OAuthData();
		data.setScreenName("vvakame");
		data.setOauthHashCode(01234567);
		saveOauth(data);
		responseDrpScheme(res, data);
	}

	private void processFirstTime(HttpServletResponse res, HttpSession session)
			throws IOException, ServletException {
		clearSession(session);

		try {
			Twitter twitter = twiFac.getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);

			RequestToken requestToken = twitter
					.getOAuthRequestToken(callbackUrl);
			session.setAttribute(SESSION_TOKEN, requestToken.getToken());
			session.setAttribute(SESSION_TOKEN_SECRET, requestToken
					.getTokenSecret());
			res.sendRedirect(requestToken.getAuthenticationURL());

		} catch (TwitterException e) {
			clearSession(session);
			logger.info(e.getMessage());
			throw new ServletException(e);
		}
	}

	private void processAuthorized(HttpServletResponse res,
			HttpSession session, String token, String tokenSecret)
			throws IOException, ServletException {

		AccessToken accessToken = new AccessToken(token, tokenSecret);
		Twitter twitter = twiFac.getOAuthAuthorizedInstance(consumerKey,
				consumerSecret, accessToken);

		OAuthData data = null;
		try {
			data = new OAuthData();
			data.setScreenName(twitter.getScreenName());
			data.setOauthHashCode(accessToken.hashCode());

			StringBuilder stb = new StringBuilder();
			stb.append("by accessToken, ");
			stb.append("name=").append(data.getScreenName()).append(", ");
			stb.append("hashCode=").append(data.getOauthHashCode());
			logger.info(stb.toString());

		} catch (TwitterException e) {
			clearSession(session);
			logger.info(e.getMessage());
			throw new ServletException(e);
		}

		saveOauth(data);
		responseDrpScheme(res, data);
		saveAnnounceAccount(data, accessToken);
	}

	private void processCallback(HttpServletResponse res, HttpSession session,
			String token, String tokenSecret, String oauthVerifier)
			throws IOException, ServletException {

		OAuthData data = null;
		AccessToken accessToken = null;
		try {
			Twitter twitter = twiFac.getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			RequestToken requestToken = new RequestToken(token, tokenSecret);
			accessToken = twitter.getOAuthAccessToken(requestToken,
					oauthVerifier);

			data = new OAuthData();
			data.setScreenName(twitter.getScreenName());
			data.setOauthHashCode(accessToken.hashCode());

			StringBuilder stb = new StringBuilder();
			stb.append("by accessToken, ");
			stb.append("name=").append(data.getScreenName()).append(", ");
			stb.append("hashCode=").append(data.getOauthHashCode());
			logger.info(stb.toString());

		} catch (TwitterException e) {
			clearSession(session);
			logger.info(e.getMessage());
			throw new ServletException(e);
		}

		saveOauth(data);
		responseDrpScheme(res, data);
		saveAnnounceAccount(data, accessToken);
	}

	private void saveAnnounceAccount(OAuthData data, AccessToken accessToken) {

		if ("DroppShare".equals(data.getScreenName())) {
			TwitterAuthorizedData twiData = new TwitterAuthorizedData();
			twiData.setAccessToken(accessToken);
			twiData.setScreenName(data.getScreenName());
			twiData.createKey();
			Datastore.put(twiData);
		}
	}

	private void saveOauth(OAuthData data) {

		OAuthDataMeta oMeta = OAuthDataMeta.get();
		List<Key> keys = Datastore.query(oMeta).filter(
				oMeta.screenName.equal(data.getScreenName())).asKeyList();
		Datastore.delete(keys);
		Datastore.put(data);
	}

	private void responseDrpScheme(HttpServletResponse response, OAuthData data)
			throws IOException {

		response.sendRedirect("drphost:" + data.getScreenName() + "?hash="
				+ data.getOauthHashCode());
	}

	private void clearSession(HttpSession session) {

		session.removeAttribute(PROP_TWITTER);
		session.removeAttribute(SESSION_TOKEN);
		session.removeAttribute(SESSION_TOKEN_SECRET);
	}
}
