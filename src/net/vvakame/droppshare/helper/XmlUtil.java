package net.vvakame.droppshare.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.android.helper.ZipUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.model.AppData;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;

public class XmlUtil implements LogTagIF {

	public static final File DATA_DIR = new File(AppDataUtil.EX_STRAGE, "data/");

	public static final File WORKING_DIR = new File(AppDataUtil.EX_STRAGE,
			"archive/");
	public static final File ICON_DIR = new File(WORKING_DIR, "icon/");

	public static final File XML_FILE = new File(WORKING_DIR, "app.xml");

	public static final File DROZIP_FILE = new File(DATA_DIR, "archive.drozip");

	public static final int COMPRESS_QUALITY = 100;

	public static void writeXmlCache(Context context, String fileName,
			List<AppData> appDataList) {
		Log.d(TAG, HelperUtil.getStackName());

		HelperUtil.deleteDir(WORKING_DIR);
		WORKING_DIR.mkdirs();

		String result = createXml(appDataList);
		if (result == null) {
			return;
		}

		try {
			FileWriter fw = new FileWriter(XML_FILE);
			fw.write(result);
			fw.flush();
			fw.close();

		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return;
		}

		ICON_DIR.mkdirs();
		writeIconImages(context, appDataList);

		try {
			ZipUtil.ZipCompresser zedit = ZipUtil.getCompressor(DROZIP_FILE);
			zedit.push(WORKING_DIR);
			zedit.finish();
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return;
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return;
		}
	}

	private static String createXml(List<AppData> appDataList) {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String result = null;

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
				String desc = appData.getDescription();
				serializer.text(desc != null ? desc : "");
				serializer.endTag("", "description");

				serializer.startTag("", "versionCode");
				serializer.text(String.valueOf(appData.getVersionCode()));
				serializer.endTag("", "versionCode");

				serializer.startTag("", "versionName");
				serializer.text(appData.getVersionName());
				serializer.endTag("", "versionName");

				serializer.endTag("", "AppData");
			}
			serializer.endTag("", "DroppShare");
			serializer.endDocument();

			result = writer.toString();
		} catch (IllegalArgumentException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (IllegalStateException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		}

		return result;
	}

	private static void writeIconImages(Context context, List<AppData> appList) {
		PackageManager pm = context.getPackageManager();
		int iconSize = context.getResources().getInteger(R.attr.xml_icon_size);

		for (AppData appData : appList) {
			try {
				Drawable icon = pm.getApplicationIcon(appData.getPackageName());
				BitmapDrawable bitmapDrawable = AppDataUtil
						.getResizedBitmapDrawable(context, icon, iconSize,
								iconSize);

				writeBitmap(new File(ICON_DIR, appData.getPackageName() + "_v"
						+ String.valueOf(appData.getVersionCode()) + ".png"),
						bitmapDrawable.getBitmap());
			} catch (NameNotFoundException e) {
				// 握りつぶす
			}
		}
	}

	private static void writeBitmap(File fileName, Bitmap bitmap) {
		Log.d(TAG, HelperUtil.getStackName());

		try {
			FileOutputStream fout = new FileOutputStream(fileName);

			bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, fout);
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		}
	}
}
