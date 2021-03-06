package net.vvakame.dropphosting.taskqueue;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vvakame.dropphosting.meta.TwitterAuthorizedDataMeta;
import net.vvakame.dropphosting.meta.VariantDataMeta;
import net.vvakame.dropphosting.model.TwitterAuthorizedData;
import net.vvakame.dropphosting.model.VariantData;
import net.vvakame.dropphosting.server.DataDownloadServlet;

import org.slim3.datastore.Datastore;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TweetQueueServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String PROP_TWITTER = "twitter";

	private static String consumerKey;
	private static String consumerSecret;

	private static TwitterFactory twiFac;

	private static final Logger log = Logger
			.getLogger(DataDownloadServlet.class.getName());

	public void init() throws ServletException {
		ResourceBundle rb = ResourceBundle.getBundle(PROP_TWITTER, Locale
				.getDefault());
		consumerKey = rb.getString("consumer_key");
		consumerSecret = rb.getString("consumer_secret");

		twiFac = new TwitterFactory();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String u = req.getParameter("u");
		String v = req.getParameter("v");

		log.warning("taskqueue tweet upload! u=" + u + ", v=" + v);

		TwitterAuthorizedDataMeta tMeta = TwitterAuthorizedDataMeta.get();
		TwitterAuthorizedData twiData = Datastore.query(tMeta).filter(
				tMeta.screenName.equal("DroppShare")).asSingle();

		if (twiData != null) {
			VariantDataMeta vMeta = VariantDataMeta.get();
			VariantData variantData = Datastore.query(vMeta).filter(
					vMeta.screenName.equal(u), vMeta.variant.equal(v))
					.asSingle();

			Twitter twitter = twiFac.getOAuthAuthorizedInstance(consumerKey,
					consumerSecret, twiData.getAccessToken());
			try {
				twitter.updateStatus(".@" + variantData.getScreenName()
						+ "'s app list " + "http://drphost.appspot.com/view?u="
						+ variantData.getScreenName() + " "
						+ new Date().toString());
			} catch (TwitterException e) {
				throw new ServletException(e);
			}
		} else {
			log.warning("Oops! @DroppShare is not authorized!!");
		}
	}
}
