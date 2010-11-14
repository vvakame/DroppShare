package net.vvakame.drphost.controller.twitter;

import java.util.logging.Logger;

import net.vvakame.drphost.service.TwitterService;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import twitter4j.Twitter;
import twitter4j.http.RequestToken;

/**
 * TwitterにOAuth認証の請求を行います.
 * 
 * @author vvakame
 */
public class AuthController extends Controller {

	static final Logger logger = Logger.getLogger(AuthController.class
			.getName());

	@Override
	public Navigation run() throws Exception {
		Twitter twitter = TwitterService.getInstance();

		// セッションに保持
		sessionScope("twitter", twitter);
		// リクエストトークンの作成
		RequestToken requestToken = twitter
				.getOAuthRequestToken(TwitterService.callbackUrl);
		// セッションに保持
		sessionScope("requestToken", requestToken);

		return redirect(requestToken.getAuthorizationURL());
	}
}
