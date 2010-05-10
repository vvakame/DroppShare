package net.vvakame.droppshare.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.android.helper.ZipUtil;
import net.vvakame.droppshare.R;
import net.vvakame.droppshare.model.AppData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
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

	private static final String DROPP_SHARE = "DroppShare";
	private static final String VERSION = "version";
	private static final String SCREEN = "screen";
	private static final String APP_DATA = "AppData";
	private static final String APP_NAME = "appName";
	private static final String PACKAGE_NAME = "packageName";
	private static final String DESCRIPTION = "description";
	private static final String VERSION_CODE = "versionCode";
	private static final String VERSION_NAME = "versionName";

	public static final File DATA_DIR = new File(AppDataUtil.EX_STRAGE, "data/");
	public static final String POSTFIX = ".drozip";

	public static final File WORKING_DIR = new File(AppDataUtil.EX_STRAGE,
			"work/");
	private static final File TEMP_DIR = new File(WORKING_DIR, "archive/");
	private static final File ICON_DIR = new File(TEMP_DIR, "icon/");
	private static final File XML_FILE = new File(TEMP_DIR, "app.xml");
	private static final String DROZIP_NAME = "archive" + POSTFIX;

	public static final int COMPRESS_QUALITY = 100;

	public static void writeXmlCache(Context context, String fileName,
			List<AppData> appDataList) {
		Log.d(TAG, HelperUtil.getStackName());

		HelperUtil.deleteDir(WORKING_DIR);
		TEMP_DIR.mkdirs();

		String result = createXml(context, appDataList);
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

		File archFile = new File(WORKING_DIR, DROZIP_NAME);
		try {
			ZipUtil.ZipCompresser zedit = ZipUtil.getCompressor(archFile);
			zedit.push(TEMP_DIR);
			zedit.finish();
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return;
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return;
		}

		archFile.renameTo(new File(DATA_DIR, fileName + POSTFIX));
		HelperUtil.deleteDir(WORKING_DIR);
	}

	public static List<AppData> readXmlCache(Context context, File zipFile) {
		List<AppData> appList = null;

		if (!zipFile.exists()) {
			throw new IllegalArgumentException("Not found " + zipFile.getName());
		}

		HelperUtil.deleteDir(WORKING_DIR);

		File tmpZip = new File(WORKING_DIR, DROZIP_NAME);
		tmpZip.getParentFile().mkdirs();
		try {
			HelperUtil.copyFile(zipFile, tmpZip);
			ZipUtil.unzip(tmpZip, WORKING_DIR);
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return null;
		}

		FileInputStream fin = null;
		try {
			fin = new FileInputStream(XML_FILE);
		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return null;
		}
		appList = readXml(context, fin);

		HelperUtil.deleteDir(WORKING_DIR);

		return appList;
	}

	private static String createXml(Context context, List<AppData> appDataList) {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String result = null;

		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", DROPP_SHARE);
			serializer.attribute("", VERSION, String
					.valueOf(AppData.serialVersionUID));
			serializer.attribute("", SCREEN, context
					.getString(R.string.screen_dpi));

			for (AppData appData : appDataList) {
				serializer.startTag("", APP_DATA);

				serializer.startTag("", APP_NAME);
				serializer.text(appData.getAppName());
				serializer.endTag("", APP_NAME);

				serializer.startTag("", PACKAGE_NAME);
				serializer.text(appData.getPackageName());
				serializer.endTag("", PACKAGE_NAME);

				serializer.startTag("", DESCRIPTION);
				String desc = appData.getDescription();
				serializer.text(desc != null ? desc : "");
				serializer.endTag("", DESCRIPTION);

				serializer.startTag("", VERSION_CODE);
				serializer.text(String.valueOf(appData.getVersionCode()));
				serializer.endTag("", VERSION_CODE);

				serializer.startTag("", VERSION_NAME);
				serializer.text(appData.getVersionName());
				serializer.endTag("", VERSION_NAME);

				serializer.endTag("", APP_DATA);
			}
			serializer.endTag("", DROPP_SHARE);
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

	private static List<AppData> readXml(Context context, InputStream isr) {
		XmlPullParser xmlParser = Xml.newPullParser();
		List<AppData> appList = null;

		@SuppressWarnings("unused")
		long version = -1;
		try {
			xmlParser.setInput(isr, null);

			int eventType = xmlParser.getEventType();
			AppData appData = null;
			try {

				while (eventType != XmlPullParser.END_DOCUMENT) {
					String name = null;
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						appList = new ArrayList<AppData>();
						break;
					case XmlPullParser.START_TAG:
						name = xmlParser.getName();

						if (name.equalsIgnoreCase(APP_NAME)) {
							appData.setAppName(xmlParser.nextText());

						} else if (name.equalsIgnoreCase(PACKAGE_NAME)) {
							appData.setPackageName(xmlParser.nextText());

						} else if (name.equalsIgnoreCase(DESCRIPTION)) {
							String desc = xmlParser.nextText();
							if (!"".equals(desc)) {
								appData.setDescription(desc);
							}

						} else if (name.equalsIgnoreCase(VERSION_CODE)) {
							int versionCode = Integer.parseInt(xmlParser
									.nextText());
							appData.setVersionCode(versionCode);

						} else if (name.equalsIgnoreCase(VERSION_NAME)) {
							appData.setVersionName(xmlParser.nextText());

						} else if (name.equalsIgnoreCase(APP_DATA)) {
							appData = new AppData();

						} else if (name.equalsIgnoreCase(DROPP_SHARE)) {
							version = Long.parseLong(xmlParser
									.getAttributeValue(0));
						}

						break;
					case XmlPullParser.END_TAG:
						name = xmlParser.getName();
						if (name.equalsIgnoreCase(APP_DATA)) {
							File iconFile = new File(ICON_DIR, appData
									.getUniqName()
									+ ".png");
							BitmapDrawable icon = new BitmapDrawable(iconFile
									.getAbsolutePath());
							icon = AppDataUtil.getResizedBitmapDrawable(
									context, icon);
							appData.setIcon(icon);

							appList.add(appData);
							appData = new AppData();
						}
						break;
					}
					eventType = xmlParser.next();
				}
			} catch (NumberFormatException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			} catch (IOException e) {
				Log.d(TAG, HelperUtil.getExceptionLog(e));
				return null;
			}

		} catch (XmlPullParserException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
			return null;
		}

		return appList;
	}

	private static void writeIconImages(Context context, List<AppData> appList) {
		PackageManager pm = context.getPackageManager();

		for (AppData appData : appList) {
			try {
				Drawable icon = pm.getApplicationIcon(appData.getPackageName());
				BitmapDrawable bitmapDrawable = AppDataUtil
						.getResizedBitmapDrawable(context, icon);

				writeBitmap(new File(ICON_DIR, appData.getUniqName() + ".png"),
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
