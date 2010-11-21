package net.vvakame.drphost.controller.twitter;

import java.util.logging.Logger;

import net.vvakame.drphost.model.TwitterOAuth;
import net.vvakame.drphost.service.TwitterService;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import twitter4j.Twitter;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

/**
 * Twitterからのコールバックを受け取り、Androidアプリへリダイレクトします.
 * Androidアプリ側にてIntentFilterを設定し、drphost://に反応させます.
 * 
 * @author vvakame
 */
public class CallbackController extends Controller {

	static final Logger logger = Logger.getLogger(CallbackController.class
			.getName());

	@Override
	public Navigation run() throws Exception {

		// セッションから取得
		Twitter twitter = sessionScope("twitter");
		RequestToken requestToken = sessionScope("requestToken");

		if (twitter == null || requestToken == null) {
			return redirect("/twitter/auth");
		}

		// AccessTokenの生成＆確認
		String verifier = asString("oauth_verifier");
		AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
				verifier);

		// セッションの掃除
		request.getSession().removeAttribute("twitter"); // セッションから削除
		request.getSession().removeAttribute("requestToken"); // セッションから削除

		// 保存
		TwitterOAuth oauth = new TwitterOAuth();
		oauth.setKey(TwitterService.createKey(twitter.getId()));
		oauth.setHash((long) accessToken.hashCode());
		oauth.setToken(accessToken.getToken());
		oauth.setTokenSecret(accessToken.getTokenSecret());
		Datastore.put(oauth);

		return redirect("drphost://id=" + oauth.getKey().getId() + "&hash="
				+ oauth.getHash());
	}
}
