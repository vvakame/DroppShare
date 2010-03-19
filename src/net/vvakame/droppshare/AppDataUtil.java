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
		// TODO Vendingが落ちなくなったら"http://market.android.com/details?id="に戻すこと
		return "http://market.android.com/search?q=pname:"
				+ appData.getPackageName();
	}

	public static String getMarketUriFromAppData(AppData appData) {
		// TODO Vendingが落ちなくなったら"market://details?id="に戻すこと
		return "market://search?q=pname:" + appData.getPackageName();
	}

	private static File getTmpDir(Context context) {
		File dir = new File(context.getFilesDir(), "tmp/");
		return dir;
	}

	private static File getCacheDir(Context context) {
		File dir = new File(context.getFilesDir(), "cache/");
		return dir;
	}

	private static File getTmpCacheFile(Context context) {
		File file = new File(context.getFilesDir(), "tmp/" + CACHE_FILE);
		return file;
	}

	private static File getCacheFile(Context context) {
		File file = new File(context.getFilesDir(), "cache/" + CACHE_FILE);
		return file;
	}

	private static void makeDir(File file) {
		if (file == null) {
			return;
		} else if (file.exists()) {
			return;
		} else if (file.isDirectory()) {
			file.mkdirs();
		} else {
			file.getParentFile().mkdirs();
		}
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

	public static boolean isExistCache(Context context) {
		File cacheFile = getCacheFile(context);

		return cacheFile.exists();
	}

	@SuppressWarnings("unchecked")
	public static List<AppData> readSerializedCaches(Context context) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		List<AppData> appDataList = null;
		ObjectInputStream in = null;
		try {
			File cacheFile = getCacheFile(context);
			FileInputStream fin = new FileInputStream(cacheFile);
			in = new ObjectInputStream(fin);
			appDataList = (List<AppData>) in.readObject();

			for (AppData appData : appDataList) {
				readIconCache(context, appData);
			}
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

	private static void readIconCache(Context context, AppData appData) {
		try {
			File cacheDir = getCacheDir(context);
			makeDir(cacheDir);
			File cacheIcon = new File(cacheDir, appData.getUniqName() + ".png");
			FileInputStream fin = new FileInputStream(cacheIcon);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(fin);

			appData.setIcon(bitmapDrawable);
		} catch (FileNotFoundException e) {
		}
	}

	public static void writeSerializedCache(Context context,
			List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		ObjectOutputStream out = null;
		try {
			for (AppData appData : appDataList) {
				writeIconCache(context, appData);
			}

			File tmpCache = getTmpCacheFile(context);
			makeDir(tmpCache);

			FileOutputStream fout = new FileOutputStream(tmpCache);
			out = new ObjectOutputStream(fout);
			out.writeObject(appDataList);
			out.flush();

			// 旧キャッシュの削除とすげ替え
			File cacheDir = getCacheDir(context);
			cacheDir.delete();
			File tmpDir = getTmpDir(context);
			tmpDir.renameTo(cacheDir);

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

	private static void writeIconCache(Context context, AppData appData) {
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
			File tmpDir = getTmpDir(context);
			makeDir(tmpDir);
			File tmpIcon = new File(tmpDir, appData.getUniqName() + ".png");
			FileOutputStream fout = new FileOutputStream(tmpIcon);

			bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100,
					fout);
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getMethodName() + ":" + e.getMessage());
		}
	}
}
