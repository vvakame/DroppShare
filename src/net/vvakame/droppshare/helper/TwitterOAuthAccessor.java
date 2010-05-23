package net.vvakame.droppshare.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vvakame.droppshare.model.OAuthData;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class TwitterOAuthAccessor {

	public static OAuthData getAuthorizedData(String screenName, String password)
			throws ClientProtocolException, IOException, IllegalStateException,
			XmlPullParserException {
		// API Level 8 AndroidHttpClient に変更できるものならしたほうがよさそう
		HttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpRequestBase con = null;
		HttpResponse res = null;

		// 最初にトークンとかサーバ側に作ってもらう RedirectでTwitterへ
		con = new HttpGet("http://drphost.appspot.com/twitter");
		res = httpclient.execute(con, localContext);
		if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new IllegalStateException(res.getStatusLine()
					.getReasonPhrase());
		}
		Map<String, String> tokens = TwitterOAuthAccessor.parseTwitterLogin(res
				.getEntity().getContent());

		// サーバ側にユーザ名とパスワード投げる
		HttpPost post = new HttpPost("https://twitter.com/oauth/authenticate");
		post.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,
				false);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		tokens.put("session[username_or_email]", screenName);
		tokens.put("session[password]", password);
		for (String key : tokens.keySet()) {
			String value = tokens.get(key);
			nvps.add(new BasicNameValuePair(key, value));
		}
		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		res = httpclient.execute(post, localContext);
		if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new IllegalStateException(res.getStatusLine()
					.getReasonPhrase());
		}
		String redirectFor = TwitterOAuthAccessor.parseTwitterRedirect(res
				.getEntity().getContent());
		if (redirectFor == null) {
			throw new IllegalStateException("You can't get redirect url.");
		}

		// metaタグでのRedirectは処理されないのでこっちでリダイレクトしてやる
		con = new HttpGet(redirectFor);
		res = httpclient.execute(con, localContext);
		if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new IllegalStateException(res.getStatusLine()
					.getReasonPhrase());
		}

		Map<String, String> oauthMap = TwitterOAuthAccessor
				.parseDrphostPage(res.getEntity().getContent());

		OAuthData oauth = new OAuthData();
		oauth.setScreenName(oauthMap.get("screenName"));
		oauth.setOauthHashCode(Integer.parseInt(oauthMap.get("oauthHashcode")));

		return oauth;
	}

	public static Map<String, String> parseTwitterLogin(InputStream isr)
			throws XmlPullParserException, IOException {
		XmlPullParser xmlParser = Xml.newPullParser();
		Map<String, String> map = null;

		xmlParser.setInput(isr, null);

		int eventType = xmlParser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				map = new HashMap<String, String>();
				break;
			case XmlPullParser.START_TAG:
				name = xmlParser.getName();

				if (name.equalsIgnoreCase("input")) {
					String key = null;
					String value = null;
					int count = xmlParser.getAttributeCount();
					for (int i = 0; i < count; i++) {
						String attrName = xmlParser.getAttributeName(i);
						String attrValue = xmlParser.getAttributeValue(i);

						if ("authenticity_token".equals(attrValue)) {
							key = attrValue;
						} else if ("oauth_token".equals(attrValue)) {
							key = attrValue;
						} else if ("value".equals(attrName)) {
							value = attrValue;
						}
					}
					if (key != null) {
						map.put(key, value);
					}
				}

				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = xmlParser.next();
		}

		return map;
	}

	public static String parseTwitterRedirect(InputStream is)
			throws IOException {

		String html = getStringFromInputStream(is);

		final String PATTERN = "http://drphost.appspot.com/[_a-zA-Z0-9/?&=]+";
		Pattern httpPattern = Pattern.compile(PATTERN);
		Matcher matcher = httpPattern.matcher(html);

		while (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	public static Map<String, String> parseDrphostPage(InputStream is)
			throws XmlPullParserException, IOException {
		XmlPullParser xmlParser = Xml.newPullParser();
		Map<String, String> map = null;

		xmlParser.setInput(is, null);

		int eventType = xmlParser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			String value = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				map = new HashMap<String, String>();
				break;
			case XmlPullParser.START_TAG:
				name = xmlParser.getName();
				if (name.equalsIgnoreCase("screenName")) {
					value = xmlParser.nextText();
					map.put(name, value);
				} else if (name.equalsIgnoreCase("oauthHashcode")) {
					value = xmlParser.nextText();
					map.put(name, value);
				}

				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = xmlParser.next();
		}

		return map;
	}

	private static String getStringFromInputStream(InputStream is)
			throws IOException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		StringBuilder stb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			stb.append(line);
		}
		return stb.toString();
	}
}
