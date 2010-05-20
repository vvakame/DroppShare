package net.vvakame.droppshare.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.LogTagIF;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

public class DroppHostingClientActivity extends Activity implements LogTagIF {
	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		Map<String, String> tokens = null;
		try {
			HttpGet con = new HttpGet("http://drphost.appspot.com/twitter");
			HttpResponse res = new DefaultHttpClient().execute(con);

			tokens = parseTwitterLogin(res.getEntity().getContent());

		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}

		String redirectFor = null;
		try {
			HttpPost con = new HttpPost(
					"https://twitter.com/oauth/authenticate");

			con.getParams().setParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			tokens.put("session[username_or_email]", "hogehoge");
			tokens.put("session[password]", "fugafuga");
			for (String key : tokens.keySet()) {
				String value = tokens.get(key);
				nvps.add(new BasicNameValuePair(key, value));
			}
			con.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse res = new DefaultHttpClient().execute(con);

			// ここで得られるHTMLはStrictなXHTMLではなく、XMLとしてパースできない
			// TODO XMLとしての処理ではなく正規表現ひっこぬきに変更する
			// http://drphost.appspot.com/twitter?oauth_token=xxx&oauth_verifier=yyy
			redirectFor = parseTwitterRedirect(res.getEntity().getContent());

		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}

		if (redirectFor == null) {
			throw new IllegalStateException("You can't get redirect url.");
		}

		try {
			HttpGet con = new HttpGet(redirectFor);
			HttpResponse res = new DefaultHttpClient().execute(con);

			tokens = parseDrphostPage(res.getEntity().getContent());

		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}
	}

	private Map<String, String> parseTwitterLogin(InputStream isr) {
		XmlPullParser xmlParser = Xml.newPullParser();
		Map<String, String> map = null;

		try {
			xmlParser.setInput(isr, null);

			int eventType = xmlParser.getEventType();
			try {

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
								String attrValue = xmlParser
										.getAttributeValue(i);

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
			} catch (NumberFormatException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			} catch (IOException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			}

		} catch (XmlPullParserException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return null;
		}

		return map;
	}

	private String parseTwitterRedirect(InputStream isr) {
		XmlPullParser xmlParser = Xml.newPullParser();
		Map<String, String> map = null;

		try {
			xmlParser.setInput(isr, null);

			int eventType = xmlParser.getEventType();
			try {

				while (eventType != XmlPullParser.END_DOCUMENT) {
					String name = null;
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						map = new HashMap<String, String>();
						break;
					case XmlPullParser.START_TAG:
						name = xmlParser.getName();

						if (name.equalsIgnoreCase("a")) {
							String key = null;
							String value = null;
							int count = xmlParser.getAttributeCount();
							for (int i = 0; i < count; i++) {
								String attrName = xmlParser.getAttributeName(i);
								String attrValue = xmlParser
										.getAttributeValue(i);

								if ("href".equals(attrName)) {
									return attrValue;
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
			} catch (NumberFormatException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			} catch (IOException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			}

		} catch (XmlPullParserException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return null;
		}

		return null;
	}

	private Map<String, String> parseDrphostPage(InputStream isr) {
		XmlPullParser xmlParser = Xml.newPullParser();
		Map<String, String> map = null;

		try {
			xmlParser.setInput(isr, null);

			int eventType = xmlParser.getEventType();
			try {

				while (eventType != XmlPullParser.END_DOCUMENT) {
					String name = null;
					String value = null;
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						map = new HashMap<String, String>();
						break;
					case XmlPullParser.START_TAG:
						name = xmlParser.getName();
						value = xmlParser.getText();

						map.put(name, value);

						break;
					case XmlPullParser.END_TAG:
						break;
					}
					eventType = xmlParser.next();
				}
			} catch (NumberFormatException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			} catch (IOException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			}

		} catch (XmlPullParserException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
			return null;
		}

		return map;
	}

	public void pool(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		File drozip = new File(fileUri.getPath());

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "drozip";

		int bytesRead, bytesAvailable, bufferSize;

		int maxBufferSize = 1 * 1024 * 1024;

		String responseFromServer = "";

		HttpURLConnection conn = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(drozip);

			URL url = new URL("http://192.168.0.6:8888/upload");

			conn = (HttpURLConnection) url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ drozip.getName() + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				Log.i(TAG, "Message: " + line);
				responseFromServer += line;
			}
			rd.close();

		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}
	}

	@SuppressWarnings("unused")
	private void debugReading(HttpResponse res) throws IOException {
		InputStream is = res.getEntity().getContent();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		StringBuilder stb = new StringBuilder();
		while ((line = br.readLine()) != null) {
			stb.append(line);
		}
		stb.toString();
	}
}
