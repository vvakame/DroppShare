package net.vvakame.droppshare.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.msgpack.MessagePackable;
import org.msgpack.MessageTypeException;
import org.msgpack.MessageUnpackable;
import org.msgpack.Packer;
import org.msgpack.Unpacker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * アプリデータ保持用データモデル
 * 
 * @author vvakame
 */
public class AppData implements MessagePackable, MessageUnpackable {

	public static final int PACK_VERSION = 7;

	public static final int COMPRESS_QUALITY = 100;

	private String appName = null;
	private String packageName = null;
	private String description = null;
	private int versionCode = -1;
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
		return packageName + "_v" + String.valueOf(versionCode);
	}

	private String toString(CharSequence charSeq) {
		return charSeq != null ? charSeq.toString() : null;
	}

	@Override
	public String toString() {
		return getClass().getName() + "@" + packageName;
	}

	public byte[] toByteArray() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Packer packer = new Packer(bout);
		try {
			messagePack(packer);
		} catch (IOException e) {
		}
		return bout.toByteArray();
	}

	public static AppData fromByteArray(byte[] bArray) {
		ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
		Unpacker unpacker = new Unpacker(bin);
		AppData appData = new AppData();
		try {
			appData.messageUnpack(unpacker);
		} catch (MessageTypeException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return appData;
	}

	@Override
	public void messagePack(Packer packer) throws IOException {
		packer.packArray(9);
		packer.pack(PACK_VERSION);

		packer.pack(appName);
		packer.pack(packageName);
		packer.pack(description);
		packer.pack(versionCode);
		packer.pack(versionName);
		packer.pack(action);
		packer.pack(processDate != null ? processDate.getTime() : null);
		if (icon instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bout);
			byte[] bArray = bout.toByteArray();
			packer.pack(bArray);
		} else {
			packer.pack((Object) null);
		}
	}

	@Override
	public void messageUnpack(Unpacker unpacker) throws IOException,
			MessageTypeException {
		final int len = unpacker.unpackArray();
		int version = unpacker.unpackInt();
		switch (version) {
		// 1〜6 はSerializable時代に消費
		case 7:
			if (len != 9) {
				throw new IllegalArgumentException(
						"mismatch version <-> array length!");
			}
			appName = unpacker.unpackString();
			packageName = unpacker.unpackString();
			description = unpacker.tryUnpackNull() ? null : unpacker
					.unpackString();
			versionCode = unpacker.unpackInt();
			versionName = unpacker.tryUnpackNull() ? null : unpacker
					.unpackString();
			action = unpacker.tryUnpackNull() ? null : unpacker.unpackString();
			Long date = unpacker.tryUnpackNull() ? null : unpacker.unpackLong();
			processDate = date != null ? new Date(date) : null;
			byte[] bArray = unpacker.unpackByteArray();
			if (bArray != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0,
						bArray.length);
				icon = new BitmapDrawable(bitmap);
			}
			break;

		default:
			throw new IllegalStateException("unknown app data version!!");
		}
	}

	public void messageUnpack(List<Object> list) throws IOException,
			MessageTypeException {
		// 注意 ここで渡ってくるListはArrays.toList(ary)産の可能性有り。immutable。

		final int len = list.size();
		int version = (Byte) list.get(0);
		switch (version) {
		// 1〜6 はSerializable時代に消費
		case 7:
			if (len != 9) {
				throw new IllegalArgumentException(
						"mismatch version <-> array length!");
			}

			appName = convObj2String(list.get(1));
			packageName = convObj2String(list.get(2));
			description = convObj2String(list.get(3));
			Object obj = list.get(4);
			if (obj instanceof Byte) {
				versionCode = (Byte) obj;
			} else if (obj instanceof Integer) {
				versionCode = (Integer) obj;
			} else if (obj instanceof Short) {
				versionCode = (Short) obj;
			} else if (obj instanceof Long) {
				versionCode = (int) (long) (Long) obj;
			} else {
				throw new IllegalStateException();
			}
			versionName = convObj2String(list.get(5));
			action = convObj2String(list.get(6));
			Long date = (Long) list.get(7);
			processDate = date != null ? new Date(date) : null;

			byte[] bArray = (byte[]) list.get(8);
			if (bArray != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0,
						bArray.length);
				icon = new BitmapDrawable(bitmap);
			}
			break;

		default:
			throw new IllegalStateException("unknown app data version!!");
		}
	}

	private String convObj2String(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof byte[]) {
			return new String((byte[]) obj);
		} else if (obj instanceof String) {
			return (String) obj;
		} else {
			throw new IllegalStateException(
					"obj is not String byte array or String");
		}
	}
}
