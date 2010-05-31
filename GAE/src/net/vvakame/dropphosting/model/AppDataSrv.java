package net.vvakame.dropphosting.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.Image;

/**
 * アプリデータ保持用データモデル(サーバ用)
 * 
 * @author vvakame
 */
public class AppDataSrv {

	public static final String KEY = "drodata";
	public static final String USER = "user";
	public static final String APP_NAME = "appName";

	private static final String PACKAGE_NAME = "packageName";
	private static final String DESCRIPTION = "description";
	private static final String VERSION_CODE = "versionCode";
	private static final String VERSION_NAME = "versionName";

	private String appName = null;
	private String packageName = null;
	private String description = null;
	private int versionCode = -1;
	private String versionName = null;
	private Image icon = null;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public static void checkState(AppDataSrv app) {
		if (app == null) {
			throw new IllegalStateException("app is null");
		}

		String prefix = "[" + app.getAppName() + "] ";
		if (app.getAppName() == null) {
			throw new IllegalStateException(prefix + "appName is null");
		} else if (app.getPackageName() == null) {
			throw new IllegalStateException(prefix + "packageName is null");
		} else if (app.getVersionCode() == -1) {
			throw new IllegalStateException(prefix + "versionCode is -1");
		} else if (app.getIcon() == null
				|| Image.Format.PNG != app.getIcon().getFormat()) {
			throw new IllegalStateException(prefix + "Image is not png format");
		}
	}

	public String getIconName() {
		return packageName + "_v" + String.valueOf(versionCode) + ".png";
	}

	public String getIconNameWithScreenHint(DroxmlData droData) {
		int magic = -1;
		String screen = droData.getScreen();
		if ("hdpi".equals(screen)) {
			magic = 1;
		} else if ("mdpi".equals(screen)) {
			magic = 2;
		} else if ("ldpi".equals(screen)) {
			magic = 3;
		} else {
			throw new IllegalStateException("screen is invalid value!");
		}

		return getIconName() + "/" + String.valueOf(magic);
	}

	public Entity getEntity(DroxmlData droData) {
		Entity entity = new Entity(KEY);
		entity.setProperty(USER, droData.getScreenName());
		entity.setProperty(APP_NAME, appName);
		entity.setProperty(PACKAGE_NAME, packageName);
		if (description != null) {
			entity.setProperty(DESCRIPTION, description);
		}
		entity.setProperty(VERSION_CODE, versionCode);
		if (versionName != null) {
			entity.setProperty(VERSION_NAME, versionName);
		}

		return entity;
	}

	public static AppDataSrv getAppDataSrv(DatastoreService datastoreService,
			Entity entity) throws EntityNotFoundException {
		AppDataSrv app = new AppDataSrv();
		if (entity.hasProperty(APP_NAME)) {
			app.setAppName((String) entity.getProperty(APP_NAME));
		}
		if (entity.hasProperty(PACKAGE_NAME)) {
			app.setPackageName((String) entity.getProperty(PACKAGE_NAME));
		}
		if (entity.hasProperty(DESCRIPTION)) {
			app.setDescription((String) entity.getProperty(DESCRIPTION));
		}
		if (entity.hasProperty(VERSION_CODE)) {
			Long versionCode = (Long) entity.getProperty(VERSION_CODE);
			app.setVersionCode(versionCode.intValue());
		}
		if (entity.hasProperty(VERSION_NAME)) {
			app.setVersionName((String) entity.getProperty(VERSION_NAME));
		}

		return app;
	}

	public Element getDOMElement(Document document) {
		Element appDataNode = document.createElement("AppData");

		Element appNameNode = document.createElement(APP_NAME);
		appNameNode.appendChild(document.createTextNode(appName));
		appDataNode.appendChild(appNameNode);

		Element packageNameNode = document.createElement(PACKAGE_NAME);
		packageNameNode.appendChild(document.createTextNode(packageName));
		appDataNode.appendChild(packageNameNode);

		if (description != null && !"".equals(description)) {
			Element descriptionNode = document.createElement(DESCRIPTION);
			descriptionNode.appendChild(document.createTextNode(description));
			appDataNode.appendChild(descriptionNode);
		}

		Element versionCodeNode = document.createElement(VERSION_CODE);
		versionCodeNode.appendChild(document.createTextNode(String
				.valueOf(versionCode)));
		appDataNode.appendChild(versionCodeNode);

		if (versionName != null) {
			Element versionNameNode = document.createElement(VERSION_NAME);
			versionNameNode.appendChild(document.createTextNode(versionName));
			appDataNode.appendChild(versionNameNode);
		}

		return appDataNode;
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + packageName + "_v"
				+ String.valueOf(versionCode);
	}
}
