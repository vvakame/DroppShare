package jp.ne.hatena.vvakame.droppshare;

import android.graphics.drawable.Drawable;

public class AppData {
	private CharSequence appName = null;
	private Drawable icon = null;

	public CharSequence getAppName() {
		return appName;
	}

	public void setAppName(CharSequence appName) {
		this.appName = appName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
