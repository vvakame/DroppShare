package net.vvakame.droppshare.helper;

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
import java.util.Date;
import java.util.List;

import net.vvakame.droppshare.R;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.InstallLogModel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppDataUtil {
	private static final String TAG = AppDataUtil.class.getSimpleName();

	public static final String CACHE_FILE = "appDataList.cache";

	public static final int ICON_WIDTH = 48;
	public static final int ICON_HEIGHT = 48;
	public static final int COMPRESS_QUALITY = 100;

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

	public static Bitmap getResizedBitmapDrawable(Bitmap origBitmap) {
		Log.d(TAG, "before convert bytes: "
				+ String.valueOf(origBitmap.getRowBytes()));
		Matrix matrix = new Matrix();
		int newWidth = ICON_WIDTH;
		int newHeight = ICON_HEIGHT;
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
	public static List<AppData> readSerializedCaches(Context context)
			throws InvalidClassException, ClassNotFoundException {
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
		} catch (InvalidClassException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			throw e;
		} catch (ClassCastException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (ClassNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			throw e;
		} catch (StreamCorruptedException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.d(TAG, HelperUtil.getExceptionLog(e));
				}
			}
		}

		return appDataList;
	}

	private static void readIconCache(Context context, AppData appData) {
		try {
			File cacheDir = getCacheDir(context);
			cacheDir.mkdirs();
			File cacheIcon = new File(cacheDir, appData.getUniqName() + ".png");
			FileInputStream fin = new FileInputStream(cacheIcon);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(fin);

			appData.setIcon(bitmapDrawable);
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		}
	}

	public static void writeSerializedCache(Context context,
			List<AppData> appDataList) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		ObjectOutputStream out = null;
		try {
			File tmpCache = getTmpCacheFile(context);
			Log
					.d(TAG, TAG + ":" + HelperUtil.getMethodName() + ", "
							+ tmpCache);
			tmpCache.getParentFile().mkdirs();

			for (AppData appData : appDataList) {
				writeIconCache(context, appData);
			}

			FileOutputStream fout = new FileOutputStream(tmpCache);
			out = new ObjectOutputStream(fout);
			out.writeObject(appDataList);
			out.flush();

			// 旧キャッシュの削除とすげ替え
			File cacheDir = getCacheDir(context);
			deleteCache(context);
			File tmpDir = getTmpDir(context);
			tmpDir.renameTo(cacheDir);

		} catch (InvalidClassException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (NotSerializableException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Log.d(TAG, HelperUtil.getExceptionLog(e));
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
			// 一個上のメソッドでDirectoryは作ってるから確実にある想定
			File tmpIcon = new File(tmpDir, appData.getUniqName() + ".png");
			FileOutputStream fout = new FileOutputStream(tmpIcon);

			bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG,
					COMPRESS_QUALITY, fout);
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		}
	}

	public static void deleteCache(Context context) {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		File cacheDir = getCacheDir(context);
		if (!cacheDir.exists()) {
			return;
		}
		File[] cacheFiles = cacheDir.listFiles();
		if (cacheFiles != null) {
			for (File oldCache : cacheFiles) {
				oldCache.delete();
			}
		}
		cacheDir.delete();
	}

	public static AppData convert(Context context, InstallLogModel insLogModel)
			throws NameNotFoundException {
		AppData appData = new AppData();

		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = pm.getApplicationInfo(insLogModel
				.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);

		String packageName = insLogModel.getPackageName();
		CharSequence appName = pm.getApplicationLabel(appInfo);
		CharSequence description = appInfo.loadDescription(pm);
		String versionName = insLogModel.getVersionName();
		Drawable icon = appInfo.loadIcon(pm);
		String action = insLogModel.getActionType();
		Date processDate = insLogModel.getProcessDate();

		// アクションの加工を行う
		if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
			action = context.getString(R.string.added);
		} else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
			action = context.getString(R.string.replaced);
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
			action = context.getString(R.string.removed);
		}

		appData.setPackageName(packageName);
		appData.setDescription(description);
		appData.setAppName(appName);
		appData.setVersionName(versionName);
		appData.setIcon(icon);
		appData.setAction(action);
		appData.setProcessDate(processDate);

		return appData;
	}

	public static AppData convert(Context context, String packageName)
			throws NameNotFoundException {
		AppData appData = new AppData();

		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = pm.getApplicationInfo(packageName,
				PackageManager.GET_UNINSTALLED_PACKAGES);

		// String packageName = packageName;
		CharSequence appName = pm.getApplicationLabel(appInfo);
		CharSequence description = appInfo.loadDescription(pm);
		String versionName = pm.getPackageInfo(packageName,
				PackageManager.GET_UNINSTALLED_PACKAGES).versionName;
		Drawable icon = appInfo.loadIcon(pm);

		appData.setPackageName(packageName);
		appData.setDescription(description);
		appData.setAppName(appName);
		appData.setVersionName(versionName);
		appData.setIcon(icon);

		return appData;
	}
}
