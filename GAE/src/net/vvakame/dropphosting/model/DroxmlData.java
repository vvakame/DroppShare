package net.vvakame.dropphosting.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class DroxmlData {
	// ServerSide Droxml Version, ClientSideとは一致しないことに注意
	private static final int VERSION = 1;

	private LinkedHashMap<String, AppDataSrv> appMap = null;
	private String screen = null;
	private String version = null;

	// 利便のために設置
	private String screenName = null;

	public DroxmlData() {
		appMap = new LinkedHashMap<String, AppDataSrv>();
	}

	public LinkedHashMap<String, AppDataSrv> getAppMap() {
		return appMap;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void pushApp(AppDataSrv app) {
		appMap.put(app.getIconName(), app);
	}

	public static void checkState(DroxmlData droData) {
		if (droData == null) {
			throw new IllegalArgumentException(
					"upload data parse fail. droData is null!");
		} else if (droData.getScreen() == null) {
			throw new IllegalArgumentException("screen data is not included!");
		} else if (droData.getVersion() == null) {
			throw new IllegalArgumentException(
					"drozip version is not included!");
		} else if (droData.getAppMap() == null) {
			throw new IllegalArgumentException("app list data is not included!");
		} else if (droData.getScreenName() == null) {
			throw new IllegalArgumentException("screen name is not included!");
		}
		Collection<AppDataSrv> appList = droData.getAppMap().values();
		for (AppDataSrv app : appList) {
			AppDataSrv.checkState(app);
		}
	}

	public static DroxmlData getDroxmlData(DatastoreService datastoreService,
			String screenName) {

		Query q = new Query(AppDataSrv.KEY);
		q.addFilter(AppDataSrv.USER, Query.FilterOperator.EQUAL, screenName);
		q.addSort(AppDataSrv.APP_NAME);

		FetchOptions fopt = FetchOptions.Builder.withOffset(0);
		PreparedQuery pq = datastoreService.prepare(q);
		List<Entity> entities = pq.asList(fopt);
		if (entities.size() == 0) {
			throw new IllegalArgumentException(screenName
					+ "'s data is not exists.");
		}

		DroxmlData droData = new DroxmlData();

		boolean firstTime = true;
		for (Entity entity : entities) {
			if (firstTime && entity.hasProperty(AppDataSrv.USER)) {
				droData.setScreenName((String) entity
						.getProperty(AppDataSrv.USER));
				firstTime = false;
			}
			try {
				AppDataSrv app = AppDataSrv.getAppDataSrv(datastoreService,
						entity);
				droData.pushApp(app);
			} catch (EntityNotFoundException e) {
				throw new IllegalArgumentException("entity not found!");
			}
		}

		return droData;
	}

	public Document getDOMDocument() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.newDocument();

		ProcessingInstruction pi = document.createProcessingInstruction(
				"xml-stylesheet", "type=\"text/xsl\" href=\"res/app.xsl\"");
		document.appendChild(pi);

		Element root = document.createElement("DroppShare");

		Element versionNode = document.createElement("version");
		versionNode.appendChild(document
				.createTextNode(String.valueOf(VERSION)));
		root.appendChild(versionNode);

		Element authorNode = document.createElement("author");
		authorNode.appendChild(document.createTextNode(screenName));
		root.appendChild(authorNode);

		Collection<AppDataSrv> appList = appMap.values();
		for (AppDataSrv app : appList) {
			root.appendChild(app.getDOMElement(document));
		}
		document.appendChild(root);

		return document;
	}
}
