package net.vvakame.dropphosting.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.vvakame.dropphosting.meta.AppDataSrvMeta;
import net.vvakame.dropphosting.meta.IconDataMeta;
import net.vvakame.dropphosting.meta.OAuthDataMeta;
import net.vvakame.dropphosting.meta.VariantDataMeta;
import net.vvakame.dropphosting.model.AppDataSrv;
import net.vvakame.dropphosting.model.IconData;
import net.vvakame.dropphosting.model.OAuthData;
import net.vvakame.dropphosting.model.UploadData;
import net.vvakame.dropphosting.model.VariantData;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Builder;

public class DrozipUploadServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	private static final String PROP_DROP = "dropp";
	@SuppressWarnings("unused")
	private static boolean DEBUG = false;

	private static final int MAX_UPLOAD_SIZE = 2000000;

	private static final Logger log = Logger
			.getLogger(DrozipUploadServlet.class.getName());

	public void init() throws ServletException {
		ResourceBundle rb = ResourceBundle.getBundle(PROP_DROP, Locale
				.getDefault());
		DEBUG = Boolean.parseBoolean(rb.getString("debug_mode"));
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// Uploadデータを受け取る
		UploadData upData = getUploadFiles(req);
		OAuthData upOauth = upData.getOauth();
		OAuthDataMeta oMeta = OAuthDataMeta.get();
		OAuthData dbOauth = Datastore.query(oMeta).filter(
				oMeta.screenName.equal(upOauth.getScreenName())).asSingle();
		if (dbOauth == null) {
			throw new IllegalStateException(
					"OAuth on Twitter still has not been authenticated!");
		}
		if (!dbOauth.equals(upOauth)) {
			throw new IllegalStateException(
					"Please try to authenticate once more...");
		}
		UploadData.chechState(upData);

		// 受け取ったzipデータの展開とかする
		VariantData variantData = unzip(upData);
		variantData.constructState(upData);
		VariantData.checkState(variantData);

		save(upData.getOauth(), variantData);

		res.setStatus(HttpServletResponse.SC_CREATED);
		String url = req.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/") + 1);

		res
				.setHeader("Location", url + "view?u="
						+ variantData.getScreenName());
	}

	private void save(OAuthData oauth, VariantData variantData)
			throws ServletException {
		log.info("start save droData");
		List<AppDataSrv> appList = variantData.getAppList();

		// まだ整合性は気にしない
		for (AppDataSrv app : appList) {
			IconDataMeta iMeta = IconDataMeta.get();
			IconData iconData = Datastore.query(iMeta).filter(
					iMeta.packageName.equal(app.getPackageName())).asSingle();

			IconData appIcon = app.getIconData();

			// 現在持ってるのよりでかいサイズのが来たらDB差し替えたい
			if (iconData == null || appIcon.getHeight() > iconData.getHeight()
					&& appIcon.getHeight() <= 72 && appIcon.getWidth() <= 72) {

				if (iconData != null) {
					Datastore.delete(iconData.getKey());
				}
				Datastore.put(appIcon);
			} else {
				app.setIconData(iconData);
			}
		}

		// トランザクション処理は多分いらない…はず
		AppDataSrvMeta aMeta = AppDataSrvMeta.get();
		List<Key> keys = Datastore.query(aMeta).filter(
				aMeta.screenName.equal(variantData.getScreenName()),
				aMeta.variant.equal(variantData.getVariant())).asKeyList();
		Datastore.delete(keys);

		VariantDataMeta vMeta = VariantDataMeta.get();
		keys = Datastore.query(vMeta).filter(
				vMeta.screenName.equal(variantData.getScreenName()),
				vMeta.variant.equal(variantData.getVariant())).asKeyList();
		Datastore.delete(keys);

		Datastore.put(variantData);
		Datastore.put(variantData.getAppList());

		Queue queue = QueueFactory.getQueue("tweet");
		String u = variantData.getScreenName();
		String v = variantData.getVariant();
		queue.add(Builder.url("/tweet").param("u", u).param("v", v));

		log.info("Yay! data save succeed...");
	}

	private VariantData unzip(UploadData upData) throws IOException {
		ByteArrayInputStream bain = new ByteArrayInputStream(upData
				.getZipData());
		ZipInputStream zin = new ZipInputStream(bain);

		VariantData xmlData = null;
		Map<String, Image> iconMap = new HashMap<String, Image>();

		ZipEntry zen = null;
		while ((zen = zin.getNextEntry()) != null) {
			if (zen.isDirectory()) {
				continue;
			}
			String name = zen.getName();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int data = 0;
			while ((data = zin.read()) != -1) {
				baos.write(data);
			}
			if (name.endsWith(".xml")) {
				String xml = new String(baos.toByteArray(), "UTF-8");
				try {
					xmlData = parseDroxml(xml);
				} catch (XMLStreamException e) {
					// TODO 暫定処置
					e.printStackTrace();
				}
			} else if (name.endsWith(".png")) {
				Image icon = ImagesServiceFactory.makeImage(baos.toByteArray());
				String fileName = name.substring(name.lastIndexOf('/') + 1);
				iconMap.put(fileName, icon);
			}
		}

		if (xmlData == null) {
			throw new IllegalStateException(
					"upload file is not include xml data!");
		}
		for (AppDataSrv app : xmlData.getAppList()) {
			Image icon = iconMap.get(app.getIconName());
			IconData iconData = new IconData();
			iconData.setIcon(icon);
			app.setIconData(iconData);
			app.setScreenName(upData.getOauth().getScreenName());
		}

		return xmlData;
	}

	private VariantData parseDroxml(String xml) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory
				.createXMLStreamReader(new StringReader(xml));

		VariantData result = null;
		AppDataSrv app = null;

		while (reader.hasNext()) {
			String name = null;
			switch (reader.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
				result = new VariantData();

				break;

			case XMLStreamConstants.START_ELEMENT:
				name = reader.getLocalName();
				if ("DroppShare".equals(name)) {
					for (int i = 0; i < reader.getAttributeCount(); i++) {
						String attrName = reader.getAttributeLocalName(i);
						String attrValue = reader.getAttributeValue(i);
						if ("version".equals(attrName)) {
							result.setVersion(attrValue);
						} else if ("screen".equals(attrName)) {
							// result.setScreen(attrValue);
						}
					}
				} else if ("AppData".equals(name)) {
					app = new AppDataSrv();
				} else if ("appName".equals(name)) {
					app.setAppName(reader.getElementText());
				} else if ("packageName".equals(name)) {
					app.setPackageName(reader.getElementText());
				} else if ("description".equals(name)) {
					app.setDescription(reader.getElementText());
				} else if ("versionCode".equals(name)) {
					app.setVersionCode(Integer
							.parseInt(reader.getElementText()));
				} else if ("versionName".equals(name)) {
					app.setVersionName(reader.getElementText());
				}
				break;

			case XMLStreamConstants.END_ELEMENT:
				name = reader.getLocalName();
				if ("AppData".equals(name)) {
					result.pushApp(app);
				}
				break;

			case XMLStreamConstants.END_DOCUMENT:

				break;

			default:
				break;
			}
			reader.next();
		}

		return result;
	}

	private UploadData getUploadFiles(HttpServletRequest req)
			throws ServletException {
		ServletFileUpload upload = new ServletFileUpload();
		// 2MB
		upload.setSizeMax(MAX_UPLOAD_SIZE);

		UploadData upData = new UploadData();
		upData.setOauth(new OAuthData());
		try {
			FileItemIterator iter = upload.getItemIterator(req);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				InputStream is = item.openStream();

				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = null;
					log.info("Got a form field: " + item.getFieldName());
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					StringBuilder stb = new StringBuilder();
					while ((line = br.readLine()) != null) {
						stb.append(line);
					}
					value = stb.toString();

					if ("screen_name".equals(name)) {
						upData.getOauth().setScreenName(value);
					} else if ("oauth_hashcode".equals(name)) {
						upData.getOauth().setOauthHashCode(
								Integer.parseInt(value));
					} else if ("variant".equals(name)) {
						upData.setVariant(value);
					}

				} else {
					log.info("Got an uploaded file: " + item.getFieldName()
							+ ", name = " + item.getName());

					int len;
					byte[] buffer = new byte[1024];
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while ((len = is.read(buffer, 0, buffer.length)) != -1) {
						bout.write(buffer, 0, len);
					}

					upData.setZipData(bout.toByteArray());
				}
			}
		} catch (FileUploadException e) {
			throw new ServletException(e);
		} catch (IOException e) {
			throw new ServletException(e);
		}

		return upData;
	}
}
