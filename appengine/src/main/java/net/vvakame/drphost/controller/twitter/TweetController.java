package net.vvakame.drphost.controller.twitter;

import java.util.logging.Logger;

import net.vvakame.drphost.model.TwitterOAuth;
import net.vvakame.drphost.service.TwitterService;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.util.StringUtil;

import twitter4j.Twitter;

import com.google.appengine.api.datastore.Key;

/**
 * TwitterにOAuth認証の請求を行います.
 * 
 * @author vvakame
 */
public class TweetController extends Controller {

	static final Logger logger = Logger.getLogger(TweetController.class
			.getName());

	@Override
	public Navigation run() throws Exception {
		Integer id = asInteger("id");

		if (id == null) {
			throw new IllegalArgumentException();
		}

		String tweet = asString("tweet");
		tweet = StringUtil.isEmpty(tweet) ? "tweet!" : tweet;

		Key key = TwitterService.createKey(id);
		TwitterOAuth oauth = Datastore.get(TwitterOAuth.class, key);
		Twitter twitter = TwitterService.getInstance(oauth);
		twitter.updateStatus(tweet);

		return null;
	}
}
