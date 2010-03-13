package net.vvakame.droppshare;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class AppData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String appName = null;
	private String packageName = null;
	private transient Drawable icon = null;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
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
}
