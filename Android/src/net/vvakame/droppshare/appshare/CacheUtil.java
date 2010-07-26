package net.vvakame.droppshare.appshare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.common.AppData;
import net.vvakame.droppshare.common.AppDataUtil;
import net.vvakame.droppshare.common.LogTagIF;

import org.msgpack.MessagePackable;
import org.msgpack.MessageTypeException;
import org.msgpack.Packer;
import org.msgpack.Unpacker;

import android.content.Context;
import android.util.Log;

public class CacheUtil implements LogTagIF {
	public static final File CACHE_DIR = new File(AppDataUtil.EX_STRAGE,
			"caches/");

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
		try {
			FileInputStream fin = new FileInputStream(cacheFile);
			Unpacker unpacker = new Unpacker(fin);
			final int len = unpacker.unpackArray();
			appDataList = new ArrayList<AppData>(len);
			for (Object obj : unpacker) {
				AppData appData = new AppData();
				List<Object> lst = (List<Object>) obj;
				appData.messageUnpack(lst);
				appDataList.add(appData);
			}

		} catch (FileNotFoundException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (MessageTypeException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		} catch (Exception e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
		}

		return appDataList != null ? appDataList : new ArrayList<AppData>();
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

		try {
			File tmpCache = new File(CACHE_DIR, fileName + ".tmp");
			File cache = new File(CACHE_DIR, fileName);

			CACHE_DIR.mkdirs();
			FileOutputStream fout = new FileOutputStream(tmpCache);
			Packer packer = new Packer(fout);
			packer.packArray(appDataList.size());
			for (AppData appData : appDataList) {
				packer.pack((MessagePackable) appData);
			}

			fout.flush();

			// 旧キャッシュの削除とすげ替え
			if (cache.exists()) {
				cache.delete();
			}
			tmpCache.renameTo(cache);

		} catch (IOException e) {
			Log.d(TAG, HelperUtil.getExceptionLog(e));
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

		cacheFile = new File(CACHE_DIR, DrozipInstalledAsyncTask.CACHE_FILE);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}

		cacheFile = new File(CACHE_DIR, DrozipHistoryAsyncTask.CACHE_FILE);
		if (cacheFile.exists()) {
			cacheFile.delete();
		}
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
