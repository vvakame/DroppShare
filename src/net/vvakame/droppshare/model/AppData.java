package net.vvakame.droppshare.model;

import java.io.Serializable;
import java.util.Date;

import android.graphics.drawable.Drawable;

public class AppData implements Serializable {
	private static final long serialVersionUID = 3L;

	private String appName = null;
	private String packageName = null;
	private String description = null;
	private String versionName = null;
	private transient Drawable icon = null;
	private transient String action = null;
	private transient Date processDate = null;

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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
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
