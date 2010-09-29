package net.vvakame.drphost.model;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.vvakame.drphost.meta.AppDataSrvMeta;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.InverseModelListRef;
import org.slim3.datastore.Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import com.google.appengine.api.datastore.Key;

@Model
public class VariantData {
	// ServerSide Droxml Version, ClientSideとは一致しないことに注意
	private static final int VERSION = 1;

	@Attribute(primaryKey = true)
	private Key key = null;

	@Attribute(persistent = false)
	private InverseModelListRef<AppDataSrv, VariantData> appListRef = new InverseModelListRef<AppDataSrv, VariantData>(
			AppDataSrv.class, AppDataSrvMeta.get().variantRef.getName(), this);

	private String version = null;

	private String screenName = null;
	private String variant = null;

	public void createKey() {
		if (key == null) {
			key = Datastore.createKey(VariantData.class, screenName + "/"
					+ variant);
		}
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public InverseModelListRef<AppDataSrv, VariantData> getAppListRef() {
		return appListRef;
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

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public List<AppDataSrv> getAppList() {
		return appListRef.getModelList();
	}

	public void pushApp(AppDataSrv app) {
		appListRef.getModelList().add(app);
	}

	public void constructState(UploadData upData) {
		screenName = upData.getOauth().getScreenName();
		variant = upData.getVariant();

		List<AppDataSrv> appList = getAppListRef().getModelList();
		for (AppDataSrv app : appList) {
			app.getVariantRef().setModel(this);
			app.constructState(this);
		}
	}

	public static void checkState(VariantData variantData) {
		if (variantData == null) {
			throw new IllegalArgumentException("variant data is null!");
		} else if (variantData.getScreenName() == null) {
			throw new IllegalArgumentException("screenname is not included!");
		} else if (variantData.getVariant() == null) {
			throw new IllegalArgumentException("variant is not included!");
		} else if (variantData.getAppListRef().getModelList() == null) {
			throw new IllegalArgumentException("app list is null!");
		} else if (variantData.getAppListRef().getModelList().size() == 0) {
			throw new IllegalArgumentException("app list is not included!");
		}
		List<AppDataSrv> appList = variantData.getAppListRef().getModelList();
		for (AppDataSrv app : appList) {
			AppDataSrv.checkState(app);
		}
		variantData.createKey();
	}

	public Document getDOMDocument(Locale locale)
			throws ParserConfigurationException {
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

		List<AppDataSrv> appList = getAppListRef().getModelList();
		final Collator collator = Collator.getInstance(locale);
		Collections.sort(appList, new Comparator<AppDataSrv>() {
			@Override
			public int compare(AppDataSrv o1, AppDataSrv o2) {
				return collator.compare(o1.getAppName(), o2.getAppName());
			}
		});
		for (AppDataSrv app : appList) {
			root.appendChild(app.getDOMElement(document));
		}
		document.appendChild(root);

		return document;
	}
}
