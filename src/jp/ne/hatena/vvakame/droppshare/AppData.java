package jp.ne.hatena.vvakame.droppshare;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppData implements Parcelable {
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

	@Override
	public int describeContents() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
