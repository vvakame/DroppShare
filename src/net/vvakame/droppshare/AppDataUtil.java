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
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class AppDataUtil {
	private static final String TAG = AppDataUtil.class.getSimpleName();

	public static final String CACHE_FILE = "appDataList.cache";

	public static String getHttpUriFromAppData(AppData appData) {
		return "http://market.android.com/details?id="
				+ appData.getPackageName();
	}

	public static String getMarketUriFromAppData(AppData appData) {
		return "market://details?id=" + appData.getPackageName();
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

	public static void writeIconCache(Context context, AppData appData) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		if (appData == null || appData.getIcon() == null) {
			return;
		} else if (!(appData.getIcon() instanceof BitmapDrawable)) {
			Log.d(TAG, TAG + ":" + HelperUtil.getMethodName() + " "
					+ appData.getIcon().getClass().getSimpleName());
			return;
		}

		BitmapDrawable bitmapDrawable = (BitmapDrawable) appData.getIcon();
		try {
			bitmapDrawable.getBitmap().compress(
					Bitmap.CompressFormat.PNG,
					100,
					context.openFileOutput(appData.getUniqName() + ".png",
							Context.MODE_WORLD_READABLE));
		} catch (FileNotFoundException e) {
		}
	}

	public static void readIconCache(Context context, AppData appData) {
		try {
			FileInputStream fin = context.openFileInput(appData.getUniqName()
					+ ".png");
			BitmapDrawable bitmapDrawable = new BitmapDrawable(fin);

			appData.setIcon(bitmapDrawable);
		} catch (FileNotFoundException e) {
		}
	}

	public static boolean isExistCache(Context context) {
		String cachePath = context.getFilesDir().getAbsolutePath() + "/"
				+ CACHE_FILE;
		File cacheFile = new File(cachePath);

		return cacheFile.exists();
	}

	@SuppressWarnings("unchecked")
	public static List<AppData> readSerializedCaches(Context context) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		List<AppData> appDataList = null;
		ObjectInputStream in = null;
		try {
			FileInputStream fin = context.openFileInput(CACHE_FILE);
			in = new ObjectInputStream(fin);
			appDataList = (List<AppData>) in.readObject();
		} catch (ClassCastException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.d(TAG, e.getClass().getSimpleName() + " " + e.getMessage());
		} catch (StreamCorruptedException e) {
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

		return appDataList;
	}

	public static void writeSerializedCache(Context context,
			List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		ObjectOutputStream out = null;
		try {
			FileOutputStream fout = context.openFileOutput(CACHE_FILE,
					Context.MODE_WORLD_READABLE);
			out = new ObjectOutputStream(fout);
			out.writeObject(appDataList);
			out.flush();
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
