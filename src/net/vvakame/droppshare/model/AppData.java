package net.vvakame.droppshare.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * アプリデータ保持用データモデル
 * 
 * @author vvakame
 */
public class AppData implements Serializable {
	private static final long serialVersionUID = 4L;
	public static final int COMPRESS_QUALITY = 100;

	private String appName = null;
	private String packageName = null;
	private String description = null;
	private String versionName = null;
	private transient Drawable icon = null;
	private String action = null;
	private Date processDate = null;

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

	private void writeObject(ObjectOutputStream out) throws IOException {
		// transient 以外
		out.defaultWriteObject();

		// transient
		SerializableBitmapWrapper sBitmap = null;
		if (icon instanceof BitmapDrawable) {
			sBitmap = new SerializableBitmapWrapper(((BitmapDrawable) icon)
					.getBitmap());
		}
		out.writeObject(sBitmap);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		// transient 以外
		in.defaultReadObject();

		// transient
		SerializableBitmapWrapper sBitmap = (SerializableBitmapWrapper) in
				.readObject();
		if (sBitmap != null) {
			icon = new BitmapDrawable(sBitmap.getBitmap());
		}
	}

	private String toString(CharSequence charSeq) {
		return charSeq != null ? charSeq.toString() : null;
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + packageName;
	}

	/**
	 * BitmapDrawableをSerializeするための変換用ラッパ
	 * 
	 * @author vvakame
	 */
	private class SerializableBitmapWrapper implements Serializable {
		private static final long serialVersionUID = AppData.serialVersionUID;

		private byte[] mBitmapArray = null;
		private transient Bitmap mBitmap = null;

		public SerializableBitmapWrapper(Bitmap bitmap) {
			mBitmap = bitmap;

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bout);
			mBitmapArray = bout.toByteArray();
		}

		public Bitmap getBitmap() {
			if (mBitmapArray == null) {
				return null;
			}
			Bitmap bitmap = BitmapFactory.decodeByteArray(mBitmapArray, 0,
					mBitmapArray.length);
			return bitmap;
		}
	}
}
