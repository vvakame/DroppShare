package net.vvakame.droppshare.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * DroppSelectorActivityのヘルパ
 * 
 * @author vvakame
 */
public class SelectorHelper {
	public static final String DEFAULT = "generatedWatching.txt";
	public static final String MANUAL = "manuallyWatching.txt";

	/**
	 * 監視対象設定ファイルのインストール
	 * 
	 * @param context
	 */
	public static void installWatchingFile(Context context) {
		AssetManager am = context.getAssets();

		try {
			InputStream is = am.open(DEFAULT);
			File def = new File(AppDataUtil.EX_STRAGE, DEFAULT);
			def.getParentFile().mkdirs();
			def.createNewFile();
			FileOutputStream fout = new FileOutputStream(def);
			byte[] byteArray = new byte[1024];
			int len = 0;
			while (-1 != (len = is.read(byteArray))) {
				fout.write(byteArray, 0, len);
			}

			File man = new File(AppDataUtil.EX_STRAGE, MANUAL);
			man.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 設定ファイルの読み込み
	 * 
	 * @return 読み込み先ファイルパスのリスト
	 */
	public static List<String> readWatchingFile() {
		ArrayList<String> fileList = new ArrayList<String>();
		FileInputStream fin = null;

		File def = new File(AppDataUtil.EX_STRAGE, DEFAULT);
		try {
			fin = new FileInputStream(def);
			fileList.addAll(parseFile(fin));
		} catch (FileNotFoundException e) {
		} finally {
			try {
				if (fin != null) {
					fin.close();
					fin = null;
				}
			} catch (IOException e) {
			}
		}

		File man = new File(AppDataUtil.EX_STRAGE, MANUAL);
		try {
			fin = new FileInputStream(man);
			fileList.addAll(parseFile(fin));
		} catch (FileNotFoundException e) {
		} finally {
			try {
				if (fin != null) {
					fin.close();
					fin = null;
				}
			} catch (IOException e) {
			}
		}

		return fileList;
	}

	/**
	 * 渡されたファイルの内容を読んで返す
	 * 
	 * @return 読み込み先ファイルパスのリスト
	 */
	private static List<String> parseFile(FileInputStream fin) {
		InputStreamReader isr = new InputStreamReader(fin);
		BufferedReader br = new BufferedReader(isr);

		List<String> list = new ArrayList<String>();

		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (!"".equals(line) && line.charAt(0) != '#') {
					list.add(line);
				}
			}
		} catch (IOException e) {
		}

		return list;
	}
}
