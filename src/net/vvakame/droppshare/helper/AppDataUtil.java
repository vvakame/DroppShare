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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

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
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

/**
 * AppData回りの付帯処理
 * 
 * @author vvakame
 */
public class AppDataUtil implements LogTagIF {

	public static final File EX_STRAGE = new File(Environment
			.getExternalStorageDirectory(), "DroppShare/");
	public static final File CACHE_DIR = new File(EX_STRAGE, "caches/");

	private static final Rect sOldBounds = new Rect();
	private static Canvas sCanvas = new Canvas();

	public static int sIconWidth = -1;
	public static int sIconHeight = -1;
	public static final int COMPRESS_QUALITY = 100;

	/**
	 * AppDataからMarketへのhttpによるURIを作成する。
	 * 
	 * @param appData
	 * @return
	 */
	public static String getHttpUriFromAppData(AppData appData) {
		return "http://market.android.com/details?id="
				+ appData.getPackageName();
	}

	/**
	 * AppDataからMarketへのmarketによるURIを作成する。
	 * 
	 * @param appData
	 * @return
	 */
	public static String getMarketUriFromAppData(AppData appData) {
		return "market://details?id=" + appData.getPackageName();
	}

	/**
	 * アイコンを適正サイズにリサイズする。
	 * 
	 * @param context
	 * @param origBitmap
	 *            リサイズ元Bitmap
	 * @return リサイズ後Bitmap
	 */
	public static BitmapDrawable getResizedBitmapDrawable(Context context,
			Drawable icon) {
		if (sIconWidth == -1) {
			sIconWidth = (int) context.getResources().getDimension(
					android.R.dimen.app_icon_size);
			sIconHeight = sIconWidth;
		}

		int width = sIconWidth;
		int height = sIconHeight;

		// 下処理
		if (icon instanceof PaintDrawable) {
			PaintDrawable painter = (PaintDrawable) icon;
			painter.setIntrinsicWidth(width);
			painter.setIntrinsicHeight(height);

		} else if (icon instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				bitmapDrawable.setTargetDensity(context.getResources()
						.getDisplayMetrics());
			}
		}

		int iconWidth = icon.getIntrinsicWidth();
		int iconHeight = icon.getIntrinsicHeight();
		Bitmap thumb = null;
		if (width > 0 && height > 0) {
			if (width < iconWidth || height < iconHeight) {
				// 縮小するパターン

				final float ratio = (float) iconWidth / iconHeight;
				if (iconWidth > iconHeight) {
					height = (int) (width / ratio);
				} else if (iconHeight > iconWidth) {
					width = (int) (height * ratio);
				}

				final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;

				thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(thumb);
				sOldBounds.set(icon.getBounds());
				final int x = (sIconWidth - width) / 2;
				final int y = (sIconHeight - height) / 2;
				icon.setBounds(x, y, x + width, y + height);
				icon.draw(canvas);
				icon.setBounds(sOldBounds);

			} else if (iconWidth < width && iconHeight < height) {
				// 拡大するパターン(真ん中に描画)

				final Bitmap.Config c = Bitmap.Config.ARGB_8888;
				thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(thumb);
				sOldBounds.set(icon.getBounds());
				final int x = (width - iconWidth) / 2;
				final int y = (height - iconHeight) / 2;
				icon.setBounds(x, y, x + iconWidth, y + iconHeight);
				icon.draw(canvas);
				icon.setBounds(sOldBounds);
			} else {
				// 同じとき

				final Bitmap.Config c = Bitmap.Config.ARGB_8888;
				thumb = Bitmap.createBitmap(sIconWidth, sIconHeight, c);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(thumb);
				sOldBounds.set(icon.getBounds());
				icon.setBounds(0, 0, iconWidth, iconHeight);
				icon.draw(canvas);
				icon.setBounds(sOldBounds);
			}
		}

		BitmapDrawable bDrawable = thumb == null ? null : new BitmapDrawable(
				thumb);
		return bDrawable;
	}

	/**
	 * 指定されたキャッシュが存在するか調べる
	 * 
	 * @param fileName
	 *            調べたいキャッシュファイル名
	 * @return 存在する場合はtrue, 存在しない場合はfalseを返す。
	 */
	public static boolean isExistCache(String fileName) {
		File cacheFile = new File(CACHE_DIR, fileName);

		return cacheFile.exists();
	}

	/**
	 * 指定されたキャッシュを読み込み返す
	 * 
	 * @param fileName
	 *            読み込むキャッシュファイル
	 * @return アプリ一覧
	 * @throws InvalidClassException
	 * @throws ClassNotFoundException
	 */
	public static List<AppData> readSerializedCaches(String fileName)
			throws InvalidClassException, ClassNotFoundException {

		File cacheFile = new File(CACHE_DIR, fileName);
		return readSerializedCaches(cacheFile);
	}

	/**
	 * 指定されたキャッシュを読み込み返す
	 * 
	 * @param cacheFile
	 *            読み込むキャッシュファイル
	 * @return アプリリスト
	 * @throws InvalidClassException
	 * @throws ClassNotFoundException
	 */
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

	/**
	 * キャッシュを指定されたファイル名で作成する
	 * 
	 * @param context
	 * @param fileName
	 *            作成するキャッシュファイル名
	 * @param appDataList
	 *            キャッシュ化したいアプリリスト
	 */
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

	public static String writeXmlCache(Context context, String fileName,
			List<AppData> appDataList) {

		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "DroppShare");
			serializer.attribute("", "version", String
					.valueOf(AppData.serialVersionUID));

			for (AppData appData : appDataList) {
				serializer.startTag("", "AppData");

				serializer.startTag("", "appName");
				serializer.text(appData.getAppName());
				serializer.endTag("", "appName");

				serializer.startTag("", "packageName");
				serializer.text(appData.getPackageName());
				serializer.endTag("", "packageName");

				serializer.startTag("", "description");
				serializer.text(appData.getDescription());
				serializer.endTag("", "description");

				serializer.startTag("", "versionName");
				serializer.text(appData.getVersionName());
				serializer.endTag("", "versionName");

				serializer.startTag("", "uniqName");
				serializer.text(appData.getUniqName());
				serializer.endTag("", "uniqName");

				serializer.endTag("", "AppData");
			}
			serializer.endTag("", "DroppShare");
			serializer.endDocument();

			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 指定されたキャッシュを削除する
	 * 
	 * @param fileName
	 *            削除するキャッシュファイル
	 */
	public static void deleteCache(String fileName) {
		Log.d(TAG, HelperUtil.getStackName());

		File cacheFile = new File(CACHE_DIR, fileName);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
	}

	/**
	 * キャッシュファイルを全て削除する
	 */
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

	/**
	 * 指定されたディレクトリにアプリのアイコンを書き出す
	 * 
	 * @param toDir
	 *            出力先ディレクトリ
	 * @param appData
	 *            アイコンを出力するアプリ
	 */
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

	/**
	 * DBから読み込んだデータを通常のアプリのデータに変換する。
	 * 
	 * @param context
	 * @param insLogModel
	 *            DBから読み込んだデータ
	 * @return アプリデータ
	 * @throws NameNotFoundException
	 */
	public static AppData convert(Context context, InstallLogModel insLogModel)
			throws NameNotFoundException {
		AppData appData = convert(context, insLogModel.getPackageName());

		int versionCode = insLogModel.getVersionCode();
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

		appData.setVersionCode(versionCode);
		appData.setVersionName(versionName);
		appData.setAction(action);
		appData.setProcessDate(processDate);

		return appData;
	}

	/**
	 * 指定されたパッケージに関する情報を読み取りアプリのデータに組み立て返す
	 * 
	 * @param context
	 * @param packageName
	 *            パッケージ名
	 * @return アプリデータ
	 * @throws NameNotFoundException
	 */
	public static AppData convert(Context context, String packageName)
			throws NameNotFoundException {

		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = pm.getApplicationInfo(packageName,
				PackageManager.GET_UNINSTALLED_PACKAGES);
		PackageInfo pInfo = pm.getPackageInfo(packageName,
				PackageManager.GET_UNINSTALLED_PACKAGES);

		return convert(context, pm, pInfo, appInfo);
	}

	/**
	 * 指定された情報を元にアプリのデータに組み立て返す
	 * 
	 * @param pm
	 *            PackageManager
	 * @param pInfo
	 *            対象アプリのPackageInfo
	 * @param appInfo
	 *            対象アプリのApplicationInfo
	 * @return アプリデータ
	 * @throws NameNotFoundException
	 */
	public static AppData convert(Context context, PackageManager pm,
			PackageInfo pInfo, ApplicationInfo appInfo)
			throws NameNotFoundException {
		AppData appData = new AppData();

		String packageName = pInfo.packageName;
		CharSequence appName = pm.getApplicationLabel(appInfo);
		CharSequence description = appInfo.loadDescription(pm);
		int versionCode = pInfo.versionCode;
		String versionName = pInfo.versionName;
		Drawable icon = getResizedBitmapDrawable(context, appInfo.loadIcon(pm));

		appData.setPackageName(packageName);
		appData.setDescription(description);
		appData.setAppName(appName);
		appData.setVersionCode(versionCode);
		appData.setVersionName(versionName);
		appData.setIcon(icon);

		return appData;
	}

	/**
	 * 2つのアプリリストを突き合わせアプリの差分リストを作成し、返す
	 * 
	 * @param srcList
	 *            比較元となるアプリリスト
	 * @param destList
	 *            比較先となるアプリリスト
	 * @return 突き合わせた結果のアプリ差分リスト
	 */
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

	/**
	 * アイコン表示用のサイズを設定ファイルより取得し返します。
	 * 
	 * @param context
	 * @return アイコンの表示サイズ
	 */
	public static int getIconSize(Context context) {
		return (int) context.getResources().getDimension(
				android.R.dimen.app_icon_size);
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
