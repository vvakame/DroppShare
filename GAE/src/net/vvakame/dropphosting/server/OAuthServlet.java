package net.vvakame.dropphosting.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.vvakame.dropphosting.model.OAuthData;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class OAuthServlet extends HttpServlet {

	private static final long serialVersionUID = 2332845261180385478L;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String SESSION_TWITTER = "twitter";
	private static final String SESSION_REQUEST_TOKEN = "requestToken";
	private static final String SESSION_ACCESS_TOKEN = "accessToken";

	private static final String PARAM_OAUTH_VERIFIER = "oauth_verifier";

	private static String callbackUrl;
	private static String consumerKey;
	private static String consumerSecret;

	private static TwitterFactory twiFac;

	public void init() throws ServletException {

		ResourceBundle rb = ResourceBundle.getBundle(SESSION_TWITTER, Locale
				.getDefault());
		callbackUrl = rb.getString("callback_url");
		consumerKey = rb.getString("consumer_key");
		consumerSecret = rb.getString("consumer_secret");

		twiFac = new TwitterFactory();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		String oauth_verifier = request.getParameter(PARAM_OAUTH_VERIFIER);

		AccessToken accessToken = (AccessToken) session
				.getAttribute(SESSION_ACCESS_TOKEN);

		String userAgent = request.getHeader("User-Agent");
		logger.info(userAgent);

		if (accessToken != null) {
			// アクセストークン取得後の処理

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
				raiseTwitterException(response, e);
				return;
			}

			outputXml(response, data);

			return;

		} else if (oauth_verifier == null) {
			// 初回アクセス時の処理

			clearSession(session);

			Twitter twitter = twiFac.getInstance();
			try {
				twitter.setOAuthConsumer(consumerKey, consumerSecret);

				RequestToken requestToken = twitter
						.getOAuthRequestToken(callbackUrl);
				session.setAttribute(SESSION_REQUEST_TOKEN, requestToken);
				session.setAttribute(SESSION_TWITTER, twitter);
				response.sendRedirect(requestToken.getAuthenticationURL());

			} catch (TwitterException e) {
				clearSession(session);
				raiseTwitterException(response, e);
				return;
			}

			return;

		} else {
			// 承認後Callback

			OAuthData data = null;
			try {
				Twitter twitter = (Twitter) session
						.getAttribute(SESSION_TWITTER);
				if (twitter == null) {
					clearSession(session);
					throw new ServletException("Twitter instance was null!!!");
				}
				RequestToken requestToken = (RequestToken) session
						.getAttribute(SESSION_REQUEST_TOKEN);
				if (requestToken == null) {
					clearSession(session);
					throw new ServletException(
							"RequestToken instance was null!!!");
				}

				accessToken = twitter.getOAuthAccessToken(requestToken,
						oauth_verifier);
				session.setAttribute(SESSION_ACCESS_TOKEN, accessToken);

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
				raiseTwitterException(response, e);
				return;
			}

			outputXml(response, data);

			return;
		}
	}

	private void outputXml(HttpServletResponse response, OAuthData data)
			throws TransformerFactoryConfigurationError, IOException,
			ServletException {
		try {
			DOMImplementation dom = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().getDOMImplementation();

			Document document = dom.createDocument("", "twitter", null);
			Element root = document.getDocumentElement();

			Element screenName = document.createElement("screenName");
			screenName.appendChild(document
					.createTextNode(data.getScreenName()));

			Element oauthHashcode = document.createElement("oauthHashcode");
			oauthHashcode.appendChild(document.createTextNode(String
					.valueOf(data.getOauthHashCode())));

			root.appendChild(screenName);
			root.appendChild(oauthHashcode);

			DOMSource src = new DOMSource(document);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();

			response.setContentType("application/xml");
			StreamResult result = new StreamResult(response.getOutputStream());
			transformer.transform(src, result);

		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		} catch (TransformerConfigurationException e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new ServletException(e);
		}
	}

	private void clearSession(HttpSession session) {

		session.removeAttribute(SESSION_TWITTER);
		session.removeAttribute(SESSION_ACCESS_TOKEN);
		session.removeAttribute(SESSION_REQUEST_TOKEN);
	}

	private void raiseTwitterException(HttpServletResponse response,
			TwitterException e) throws IOException {

		logger.warning(e.getMessage());

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.setContentType("text/html");
		PrintWriter wr = response.getWriter();
		wr.print("<html>");
		wr.print("<body>");
		wr.println("Raise Twitter exception");
		wr.println(e.getMessage());
		wr.print("</body>");
		wr.print("</html>");
		wr.flush();
		// 閉じちゃう
		wr.close();
	}
}
