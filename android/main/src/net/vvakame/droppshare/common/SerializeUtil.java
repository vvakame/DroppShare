package net.vvakame.droppshare.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import net.vvakame.android.helper.FileUtil;
import net.vvakame.android.helper.Log;
import net.vvakame.droppshare.model.AppData;
import net.vvakame.droppshare.model.AppDataUtil;

import org.msgpack.MessagePackable;
import org.msgpack.MessageTypeException;
import org.msgpack.Packer;
import org.msgpack.Unpacker;

import android.content.Context;

public class SerializeUtil {
	public static final File CACHE_DIR = new File(AppDataUtil.EX_STRAGE,
			"caches/");
	public static final File SERIALIZE_DIR = new File(AppDataUtil.EX_STRAGE,
			"list/");

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
	 * @param file
	 *            読み込むキャッシュファイル
	 * @return アプリリスト
	 * @throws InvalidClassException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static List<AppData> readSerializedCaches(File file)
			throws InvalidClassException, ClassNotFoundException {
		Log.d(Log.getStackName() + ", file=" + file.getAbsolutePath());

		List<AppData> appDataList = null;
		try {
			FileInputStream fin = new FileInputStream(file);
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
			Log.e(e);
		} catch (MessageTypeException e) {
			Log.e(e);
		} catch (IOException e) {
			Log.e(e);
		} catch (Exception e) {
			Log.e(e);
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
		Log.d(Log.getStackName() + ", file=" + fileName);

		writeSerializedCache(context, new File(CACHE_DIR, fileName),
				appDataList);
	}

	/**
	 * キャッシュを指定されたファイル名で作成する
	 * 
	 * @param context
	 * @param file
	 *            作成するキャッシュファイル名
	 * @param appDataList
	 *            キャッシュ化したいアプリリスト
	 */
	public static void writeSerializedCache(Context context, File file,
			List<AppData> appDataList) {
		Log.d(Log.getStackName() + ", file=" + file.toString());

		try {
			File tmpFile = new File(file.getParentFile(), file.getName()
					+ ".tmp");

			file.getParentFile().mkdirs();
			FileOutputStream fout = new FileOutputStream(tmpFile);
			Packer packer = new Packer(fout);
			packer.packArray(appDataList.size());
			for (AppData appData : appDataList) {
				packer.pack((MessagePackable) appData);
			}

			fout.flush();

			// 旧キャッシュの削除とすげ替え
			if (file.exists()) {
				file.delete();
			}
			tmpFile.renameTo(file);

		} catch (IOException e) {
			Log.e(e);
		}
	}

	/**
	 * キャッシュファイルを全て削除する
	 */
	public static void deleteOwnCache() {
		Log.d();
		FileUtil.deleteDir(CACHE_DIR);
	}
}
