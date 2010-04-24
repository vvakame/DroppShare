package net.vvakame.droppshare.helper;

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

public class SelectorHelper {
	public static final String DEFAULT = "generatedWatching.txt";
	public static final String MANUAL = "manuallyWatching.txt";

	public static void installWatchingFile(Context context) {
		AssetManager am = context.getAssets();

		try {
			InputStream is = am.open(DEFAULT);
			File def = new File(AppDataUtil.EX_STRAGE, DEFAULT);
			def.getParentFile().mkdirs();
			if (def.createNewFile()) {
				FileOutputStream fout = new FileOutputStream(def);
				byte[] byteArray = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(byteArray))) {
					fout.write(byteArray, 0, len);
				}
			}

			File man = new File(AppDataUtil.EX_STRAGE, MANUAL);
			if (!man.exists()) {
				man.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
				fin.close();
				fin = null;
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
				fin.close();
				fin = null;
			} catch (IOException e) {
			}
		}

		return fileList;
	}

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
