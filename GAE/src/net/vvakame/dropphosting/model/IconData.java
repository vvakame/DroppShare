package net.vvakame.dropphosting.model;

import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.Image;

@Model
public class IconData {

	@Attribute(primaryKey = true)
	private Key key = null;

	private String packageName = null;
	private String fileName = null;
	@Attribute(lob = true)
	private Image icon = null;
	private Long width = null;
	private Long height = null;
	private String register = null;
	private Date createAt = null;

	public IconData() {
	}

	public void createKey() {
		key = Datastore.createKey(IconData.class, packageName);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public Image getIcon() {
		return icon;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	public Long getWidth() {
		return width;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public Long getHeight() {
		return height;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public String getRegister() {
		return register;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public static void checkState(IconData iconData) {
		if (iconData == null) {
			throw new IllegalStateException("iconData is null");
		}
		if (iconData.getPackageName() == null) {
			throw new IllegalStateException("icon packageName is null");
		} else if (iconData.getIcon() == null) {
			throw new IllegalStateException("icon image is null");
		} else if (iconData.getRegister() == null) {
			throw new IllegalStateException("icon register is null");
		} else if (iconData.getCreateAt() == null) {
			throw new IllegalStateException("icon create at is null");
		}
		iconData.setHeight((long) iconData.getIcon().getHeight());
		iconData.setWidth((long) iconData.getIcon().getWidth());

		iconData.createKey();
	}
}
