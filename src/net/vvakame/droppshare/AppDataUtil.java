package net.vvakame.droppshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppDataUtil {
	private static final String TAG = AppDataUtil.class.getSimpleName();

	public static String getUriFromAppData(AppData appData)
			throws UnsupportedEncodingException {
		return "http://market.android.com/details?id="
				+ appData.getPackageName();
	}

	public static Bitmap getResizedBitmapDrawable(Bitmap origBitmap) {
		Log.d(TAG, "before convert bytes: "
				+ String.valueOf(origBitmap.getRowBytes()));
		Matrix matrix = new Matrix();
		int newWidth = 48;
		int newHeight = 48;
		float scaleWidth = ((float) newWidth) / origBitmap.getWidth();
		float scaleHeight = ((float) newHeight) / origBitmap.getHeight();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(origBitmap, 0, 0, origBitmap
				.getWidth(), origBitmap.getHeight(), matrix, true);
		Log.d(TAG, "after convert bytes: "
				+ String.valueOf(resizedBitmap.getRowBytes()));

		return resizedBitmap;
	}

	public static AppData readSerializedFile(Context context, File file) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + " "
				+ file.getName());

		AppData appData = null;
		ObjectInputStream in = null;
		try {
			FileInputStream fin = context.openFileInput(file.getName());
			in = new ObjectInputStream(fin);
			appData = (AppData) in.readObject();

			String iconPath = file.getAbsolutePath().substring(0,
					file.getAbsolutePath().length() - ".data".length())
					+ ".png";
			Drawable icon = new BitmapDrawable(iconPath);
			appData.setIcon(icon);

		} catch (ClassNotFoundException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (StreamCorruptedException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (FileNotFoundException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return appData;
	}

	public static void writeSerializedFile(Context context, AppData appData) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + " "
				+ appData.getAppName());

		ObjectOutputStream out = null;
		try {
			String partOfFileName = appData.getAppName() + "_"
					+ appData.getPackageName();

			FileOutputStream fout = context.openFileOutput(partOfFileName
					+ ".data", Context.MODE_WORLD_READABLE);
			out = new ObjectOutputStream(fout);
			out.writeObject(appData);
			out.flush();

			fout = context.openFileOutput(partOfFileName + ".png",
					Context.MODE_WORLD_READABLE);
			BitmapDrawable bitmapDrawable = (BitmapDrawable) appData.getIcon();
			bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100,
					fout);

		} catch (InvalidClassException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (NotSerializableException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
