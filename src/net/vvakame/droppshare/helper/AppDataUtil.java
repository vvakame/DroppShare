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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.asynctask.DroppHistoryAsynkTask;
import net.vvakame.droppshare.asynctask.DroppInstalledAsynkTask;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDiffData;
import net.vvakame.droppshare.model.InstallLogModel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

public class AppDataUtil implements LogTagIF {

	public static final File EX_STRAGE = new File(Environment
			.getExternalStorageDirectory(), "DroppShare/");
	public static final File CACHE_DIR = new File(EX_STRAGE, "caches/");

	public static final String CACHE_FILE = "appDataList.cache";

	public static final int COMPRESS_QUALITY = 100;

	public static String getHttpUriFromAppData(AppData appData) {
		return "http://market.android.com/details?id="
				+ appData.getPackageName();
	}

	public static String getMarketUriFromAppData(AppData appData) {
		return "market://details?id=" + appData.getPackageName();
	}

	public static Bitmap getResizedBitmapDrawable(Context context,
			Bitmap origBitmap) {
		Matrix matrix = new Matrix();
		int newWidth = getIconSize(context);
		int newHeight = getIconSize(context);

		float scaleWidth = ((float) newWidth) / origBitmap.getWidth();
		float scaleHeight = ((float) newHeight) / origBitmap.getHeight();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(origBitmap, 0, 0, origBitmap
				.getWidth(), origBitmap.getHeight(), matrix, true);

		return resizedBitmap;
	}

	public static boolean isExistCache(String fileName) {
		File cacheFile = new File(CACHE_DIR, fileName);

		return cacheFile.exists();
	}

	public static List<AppData> readSerializedCaches(String fileName)
			throws InvalidClassException, ClassNotFoundException {

		File cacheFile = new File(CACHE_DIR, fileName);
		return readSerializedCaches(cacheFile);
	}

	@SuppressWarnings("unchecked")
	public static List<AppData> readSerializedCaches(File cacheFile)
			throws InvalidClassException, ClassNotFoundException {
		Log.d(TAG, HelperUtil.getStackName() + ", file="
				+ cacheFile.getAbsolutePath());

		List<AppData> appDataList = null;
		ObjectInputStream in = null;
		try {
			FileInputStream fin = new FileInputStream(cacheFile);
			in = new ObjectInputStream(fin);
			appDataList = (List<AppData>) in.readObject();

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

	public static void writeSerializedCache(Context context, String fileName,
			List<AppData> appDataList) {
		Log.d(TAG, HelperUtil.getStackName() + ", file=" + fileName);

		// v0.5→v0.6 のキャッシュ構成変更でゴミを残さないためのコード。暫く残す。
		deleteOldCache(context);

		ObjectOutputStream out = null;
		try {
			File tmpCache = new File(CACHE_DIR, fileName + ".tmp");
			File cache = new File(CACHE_DIR, fileName);

			CACHE_DIR.mkdirs();
			FileOutputStream fout = new FileOutputStream(tmpCache);
			out = new ObjectOutputStream(fout);
			out.writeObject(appDataList);
			out.flush();

			// 旧キャッシュの削除とすげ替え
			if (cache.exists()) {
				cache.delete();
			}
			tmpCache.renameTo(cache);

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

	public static void deleteCache(String fileName) {
		Log.d(TAG, HelperUtil.getStackName());

		File cacheFile = new File(CACHE_DIR, fileName);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}

	public static void deleteOwnCache() {
		Log.d(TAG, HelperUtil.getStackName());

		File cacheFile = null;

		cacheFile = new File(CACHE_DIR, DroppInstalledAsynkTask.CACHE_FILE);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}

		cacheFile = new File(CACHE_DIR, DroppHistoryAsynkTask.CACHE_FILE);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}

	@SuppressWarnings("unused")
	private static void writeIconImage(File toDir, AppData appData) {
		Log.d(TAG, HelperUtil.getStackName());

		if (appData == null || appData.getIcon() == null) {
			return;
		} else if (!(appData.getIcon() instanceof BitmapDrawable)) {
			Log.d(TAG, HelperUtil.getStackName() + " "
					+ appData.getIcon().getClass().getSimpleName());
			return;
		}

		BitmapDrawable bitmapDrawable = (BitmapDrawable) appData.getIcon();
		try {
			File iconFile = new File(toDir, appData.getUniqName() + ".png");
			FileOutputStream fout = new FileOutputStream(iconFile);

			bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG,
					COMPRESS_QUALITY, fout);
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		}
	}

	public static AppData convert(Context context, InstallLogModel insLogModel)
			throws NameNotFoundException {
		AppData appData = convert(context, insLogModel.getPackageName());

		String versionName = insLogModel.getVersionName();
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

		appData.setVersionName(versionName);
		appData.setAction(action);
		appData.setProcessDate(processDate);

		return appData;
	}

	public static AppData convert(Context context, String packageName)
			throws NameNotFoundException {

		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = pm.getApplicationInfo(packageName,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		PackageInfo pInfo = pm.getPackageInfo(packageName,
				PackageManager.GET_UNINSTALLED_PACKAGES);

		return convert(pm, pInfo, appInfo);
	}

	public static AppData convert(PackageManager pm, PackageInfo pInfo,
			ApplicationInfo appInfo) throws NameNotFoundException {
		AppData appData = new AppData();

		String packageName = pInfo.packageName;
		CharSequence appName = pm.getApplicationLabel(appInfo);
		CharSequence description = appInfo.loadDescription(pm);
		String versionName = pInfo.versionName;
		Drawable icon = appInfo.loadIcon(pm);

		appData.setPackageName(packageName);
		appData.setDescription(description);
		appData.setAppName(appName);
		appData.setVersionName(versionName);
		appData.setIcon(icon);

		return appData;
	}

	public static List<AppDiffData> zipAppData(List<AppData> srcList,
			List<AppData> destList) {
		srcList = srcList != null ? srcList : new ArrayList<AppData>();
		destList = destList != null ? destList : new ArrayList<AppData>();

		// リストのソート(突き合わせ用ソート)
		Comparator<AppData> pkgCompare = new Comparator<AppData>() {
			@Override
			public int compare(AppData obj1, AppData obj2) {
				return obj1.getPackageName().compareTo(obj2.getPackageName());
			}
		};

		// リストのソート(表示用ソート)
		Comparator<AppDiffData> nameCompare = new Comparator<AppDiffData>() {
			@Override
			public int compare(AppDiffData obj1, AppDiffData obj2) {
				AppData o1 = obj1.getMasterAppData();
				AppData o2 = obj2.getMasterAppData();

				return o1.getAppName().compareToIgnoreCase(o2.getAppName());
			}
		};

		Collections.sort(srcList, pkgCompare);
		Collections.sort(destList, pkgCompare);

		List<AppDiffData> diffList = new ArrayList<AppDiffData>();

		// 特殊なパターンに対処
		if (srcList.size() == 0) {
			for (int i = 0; i < destList.size(); i++) {
				AppDiffData diff = new AppDiffData(null, destList.get(i));
				diffList.add(diff);
			}
			Collections.sort(diffList, nameCompare);
			return diffList;
		} else if (destList.size() == 0) {
			for (int i = 0; i < srcList.size(); i++) {
				AppDiffData diff = new AppDiffData(srcList.get(i), null);
				diffList.add(diff);
			}
			Collections.sort(diffList, nameCompare);
			return diffList;
		}

		int i = 0;
		int j = 0;
		while (i < srcList.size() || j < destList.size()) {
			AppData src = null;
			if (srcList.size() != 0 && i < srcList.size()) {
				src = srcList.get(i);
			} else {
				src = null;
			}

			AppData dest = null;
			if (destList.size() != 0 && j < destList.size()) {
				dest = destList.get(j);
			} else {
				dest = null;
			}

			int compare = 0;
			if (src == null) {
				compare = 1;
			} else if (dest == null) {
				compare = -1;
			} else {
				compare = src.getPackageName().compareTo(dest.getPackageName());
			}
			AppDiffData diff = null;

			if (compare == 0) {
				diff = new AppDiffData(src, dest);
				i++;
				j++;
			} else if (compare < 0) {
				diff = new AppDiffData(src, null);
				i++;
			} else {
				diff = new AppDiffData(null, dest);
				j++;
			}

			diffList.add(diff);
		}

		Collections.sort(diffList, nameCompare);
		return diffList;
	}

	public static int getIconSize(Context context) {
		return context.getResources().getInteger(R.attr.icon_size_px);
	}

	// v0.5→v0.6でキャッシュの構成を変更したのでお掃除コードを仕込む 暫く残す
	private static void deleteOldCache(Context context) {
		Log.d(TAG, HelperUtil.getStackName());

		File cacheDir = new File(context.getFilesDir(), "cache/");
		if (cacheDir.exists()) {
			File[] cacheFiles = cacheDir.listFiles();
			if (cacheFiles != null) {
				for (File oldCache : cacheFiles) {
					oldCache.delete();
				}
			}
			cacheDir.delete();
		}
		File tmpDir = new File(context.getFilesDir(), "tmp/");
		if (tmpDir.exists()) {
			File[] cacheFiles = tmpDir.listFiles();
			if (cacheFiles != null) {
				for (File oldCache : cacheFiles) {
					oldCache.delete();
				}
			}
			tmpDir.delete();
		}
	}
}
