package net.vvakame.dropphosting.model;

import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Image;

/**
 * アプリデータ保持用データモデル(サーバ用)
 * 
 * @author vvakame
 */
@Model(kind = "AppData")
public class AppDataSrv {

	// TODO いらないキーを捨てる
	public static final String KEY = "drodata";
	public static final String USER = "user";
	public static final String APP_NAME = "appName";

	private static final String PACKAGE_NAME = "packageName";
	private static final String DESCRIPTION = "description";
	private static final String VERSION_CODE = "versionCode";
	private static final String VERSION_NAME = "versionName";

	@Attribute(primaryKey = true)
	private Key key = null;

	private ModelRef<VariantData> variantRef = new ModelRef<VariantData>(
			VariantData.class);
	private ModelRef<IconData> iconRef = new ModelRef<IconData>(IconData.class);

	private String screenName = null;
	private String variant = null;

	private String appName = null;
	private String packageName = null;
	private String description = null;
	private int versionCode = -1;
	private String versionName = null;

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public ModelRef<VariantData> getVariantRef() {
		return variantRef;
	}

	public ModelRef<IconData> getIconRef() {
		return iconRef;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getVariant() {
		return variant;
	}

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

	public VariantData getVariantData() {
		return variantRef.getModel();
	}

	public void setVariantData(VariantData variant) {
		variantRef.setModel(variant);
	}

	public IconData getIconData() {
		return iconRef.getModel();
	}

	public void setIconData(IconData icon) {
		iconRef.setModel(icon);
	}

	public String getIconName() {
		return packageName + "_v" + String.valueOf(versionCode) + ".png";
	}

	public void constructState(VariantData variantData) {
		if ("".equals(description)) {
			description = null;
		}
		variant = variantData.getVariant();

		IconData iconData = iconRef.getModel();

		iconData.setPackageName(packageName);
		iconData.setFileName(getIconName());
		iconData.setRegister(variantData.getScreenName());
		iconData.setCreateAt(new Date());
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
		} else if (app.getIconData() == null
				|| app.getIconData().getIcon() == null) {
			throw new IllegalStateException(prefix + "image is not included!");
		} else if (Image.Format.PNG != app.getIconData().getIcon().getFormat()) {
			throw new IllegalStateException(prefix + "Image is not png format");
		} else if (app.getScreenName() == null) {
			throw new IllegalStateException(prefix
					+ "screen name is not included!");
		} else if (app.getVariantData() == null) {
			throw new IllegalStateException(prefix
					+ "Parent variant is not included!");
		}
		IconData.checkState(app.getIconData());
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
