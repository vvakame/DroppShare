package net.vvakame.dropphosting.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import net.vvakame.dropphosting.model.AppDataSrv;
import net.vvakame.dropphosting.model.DroxmlData;
import net.vvakame.dropphosting.model.IconData;
import net.vvakame.dropphosting.model.OAuthData;
import net.vvakame.dropphosting.model.UploadData;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

public class DrozipUploadServlet extends HttpServlet {
	private static final long serialVersionUID = -7504558110741757404L;

	private static final int MAX_UPLOAD_SIZE = 2000000;

	private static final Logger log = Logger
			.getLogger(DrozipUploadServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		DatastoreService datastoreService = DatastoreServiceFactory
				.getDatastoreService();

		// Uploadデータを受け取る
		UploadData upData = getUploadFiles(req);
		UploadData.chechState(upData);

		// 受け取ったzipデータの展開とかする
		DroxmlData droData = unzip(upData);
		droData.setScreenName(upData.getOauth().getScreenName());
		DroxmlData.checkState(droData);

		try {
			save(datastoreService, upData.getOauth(), droData);
		} catch (EntityNotFoundException e) {
			throw new ServletException(e);
		}

		res.setStatus(HttpServletResponse.SC_CREATED);
		String url = req.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/") + 1);

		res.setHeader("Location", url + "view?u=" + droData.getScreenName());
	}

	private void save(DatastoreService datastoreService, OAuthData oauth,
			DroxmlData droData) throws ServletException,
			EntityNotFoundException {
		log.info("start save droData");
		Collection<AppDataSrv> appList = droData.getAppMap().values();

		// まだ整合性は気にしない
		Date createAt = new Date();
		for (AppDataSrv app : appList) {
			String iconNameWithHint = app.getIconNameWithScreenHint(droData);
			Query q = new Query("icon");
			q.addFilter("fileName", Query.FilterOperator.EQUAL,
					iconNameWithHint);
			PreparedQuery pq = datastoreService.prepare(q);
			if (pq.countEntities() == 0) {
				// 未登録のアプリケーション
				IconData iconData = new IconData(droData, app);
				Entity entity = iconData.getEntity(createAt);
				datastoreService.put(entity);
			}
		}

		// トランザクション処理は多分いらない…はず
		// TODO 古い重複データがあったらDELETEする処理を入れる

		List<Entity> eitityList = new ArrayList<Entity>();
		for (AppDataSrv appData : appList) {
			Entity entity = appData.getEntity(droData);
			entity.setProperty("createAt", new Date());
			eitityList.add(entity);
		}
		datastoreService.put(eitityList);

		log.info("Yay! data save succeed...");
	}

	private DroxmlData unzip(UploadData upData) throws IOException {
		ByteArrayInputStream bain = new ByteArrayInputStream(upData
				.getZipData());
		ZipInputStream zin = new ZipInputStream(bain);

		DroxmlData xmlData = null;
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
		for (String key : xmlData.getAppMap().keySet()) {
			Image icon = iconMap.get(key);
			AppDataSrv app = xmlData.getAppMap().get(key);
			app.setIcon(icon);
		}

		return xmlData;
	}

	private DroxmlData parseDroxml(String xml) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory
				.createXMLStreamReader(new StringReader(xml));

		DroxmlData result = null;
		AppDataSrv app = null;

		while (reader.hasNext()) {
			String name = null;
			switch (reader.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
				result = new DroxmlData();

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
							result.setScreen(attrValue);
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
