package net.vvakame.droppshare;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class AppData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String appName = null;
	private String packageName = null;
	private String description = null;
	private String versionName = null;
	private transient Drawable icon = null;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setAppName(CharSequence appName) {
		this.appName = toString(appName);
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

	public void setDescription(CharSequence description) {
		this.description = toString(description);
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getUniqName() {
		return this.appName + "_" + this.packageName;
	}

	private String toString(CharSequence charSeq) {
		return charSeq != null ? charSeq.toString() : null;
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + packageName;
	}
}
