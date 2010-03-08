package jp.ne.hatena.vvakame.droppshare;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppData implements Parcelable {
	private String appName = null;
	private String packageName = null;
	private Drawable icon = null;

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

	public AppData() {
	}

	public AppData(Parcel in) {
		appName = in.readString();
		packageName = in.readString();
		// icon = (Drawable) in.readValue(AppData.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(appName);
		out.writeString(packageName);
		// out.writeValue(icon);
	}

	public static final Parcelable.Creator<AppData> CREATOR = new Creator<AppData>() {
		@Override
		public AppData createFromParcel(Parcel source) {
			return new AppData(source);
		}

		@Override
		public AppData[] newArray(int size) {
			return new AppData[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
}
