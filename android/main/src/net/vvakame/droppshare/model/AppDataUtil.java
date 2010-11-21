package net.vvakame.droppshare.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.vvakame.droppshare.R;
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

/**
 * AppData回りの付帯処理
 * 
 * @author vvakame
 */
public class AppDataUtil {

	public static final File EX_STRAGE = new File(
			Environment.getExternalStorageDirectory(), "DroppShare/");

	private static final Rect sOldBounds = new Rect();
	private static Canvas sCanvas = new Canvas();

	private static int sIconWidth = -1;
	private static int sIconHeight = -1;

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
	 * @param icon
	 *            リサイズ元アイコン
	 * @return リサイズ後アイコン
	 */
	public static BitmapDrawable getResizedBitmapDrawable(Context context,
			Drawable icon) {
		if (sIconWidth == -1) {
			sIconWidth = getIconSize(context);
			sIconHeight = sIconWidth;
		}

		// 下処理
		if (icon instanceof PaintDrawable) {
			PaintDrawable painter = (PaintDrawable) icon;
			painter.setIntrinsicWidth(sIconWidth);
			painter.setIntrinsicHeight(sIconHeight);

		} else if (icon instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				bitmapDrawable.setTargetDensity(context.getResources()
						.getDisplayMetrics());
			}
		}

		int width = sIconWidth;
		int height = sIconHeight;

		int iconWidth = icon.getIntrinsicWidth();
		int iconHeight = icon.getIntrinsicHeight();
		Bitmap thumb = null;
		if (sIconWidth > 0 && sIconHeight > 0) {
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
				// 拡大するパターン

				final Bitmap.Config c = Bitmap.Config.ARGB_8888;
				thumb = Bitmap.createBitmap(width, height, c);
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
				thumb = Bitmap.createBitmap(width, height, c);
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
}
