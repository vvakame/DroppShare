package net.vvakame.android.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	public static ZipCompresser getCompressor(File zipfile)
			throws FileNotFoundException {
		return new ZipCompresser(zipfile);
	}

	public static void unzip(File file) throws ZipException, IOException {
		new ZipDecompresser().unzip(file);
	}

	public static void unzip(File file, File toDir) throws ZipException,
			IOException {
		new ZipDecompresser().unzip(file, toDir);
	}

	public static class ZipDecompresser {
		public void unzip(File file) throws ZipException, IOException {

			String fileName = file.getName();
			int exindex = fileName.lastIndexOf(".");
			String dirName = fileName.substring(0, exindex);

			File toDir = new File(file.getParent(), dirName + "/");

			unzip(file, toDir);
		}

		public void unzip(File file, File toDir) throws ZipException,
				IOException {

			toDir.mkdirs();
			if (!toDir.exists()) {
				throw new IllegalStateException();
			}

			ZipFile zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			int len;
			byte[] read = new byte[1024];

			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();

				File outFile = new File(toDir, ze.getName());
				if (ze.isDirectory()) {
					outFile.mkdirs();
				} else {
					BufferedInputStream bis = null;
					BufferedOutputStream bos = null;
					try {
						InputStream is = zipFile.getInputStream(ze);
						bis = new BufferedInputStream(is);

						bos = new BufferedOutputStream(new FileOutputStream(
								outFile));

						while ((len = bis.read(read)) != -1) {
							bos.write(read, 0, len);
						}
					} catch (FileNotFoundException e) {
						throw e;
					} catch (IOException e) {
						throw e;
					} finally {
						try {
							if (bis != null)
								bis.close();
						} catch (IOException e) {
						}
						try {
							if (bos != null)
								bos.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
	}

	public static class ZipCompresser {

		private File mZipFilePath;
		private File mProcessFile;
		private ZipOutputStream mZout;

		ZipCompresser(File zipFilePath) throws FileNotFoundException {
			if (zipFilePath == null) {
				throw new IllegalArgumentException();
			}
			mZipFilePath = zipFilePath;
		}

		public void push(File file) throws IOException {
			if (file == null) {
				throw new IllegalArgumentException();
			} else if (!file.exists()) {
				throw new FileNotFoundException("can't find "
						+ file.getAbsolutePath());
			}

			mProcessFile = file;

			if (mZout == null) {
				mZipFilePath.getParentFile().mkdirs();
				FileOutputStream fout = new FileOutputStream(mZipFilePath);
				mZout = new ZipOutputStream(fout);
			}

			if (file.isDirectory()) {
				pushDir(file);
			} else {
				pushFile(file);
			}
		}

		public void finish() throws IOException {
			if (mZout == null) {
				throw new IllegalStateException("You need call push method.");
			}

			mZout.flush();
			mZout.close();
			mZout = null;
		}

		private void pushDir(File dir) throws IOException {

			ZipEntry entry = new ZipEntry(getZipEntryName(dir.getPath()) + "/");
			entry.setSize(0);
			mZout.putNextEntry(entry);

			List<File> rootFiles = Arrays.asList(dir.listFiles());

			for (File file : rootFiles) {
				if (file.isDirectory()) {
					pushDir(file);
				} else {
					pushFile(file);
				}
			}
		}

		private void pushFile(File file) throws FileNotFoundException,
				IOException {

			byte[] buf = new byte[1024];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(file));

			ZipEntry entry = new ZipEntry(getZipEntryName(file.getPath()));
			mZout.putNextEntry(entry);

			int size;
			while ((size = in.read(buf, 0, buf.length)) != -1) {
				mZout.write(buf, 0, size);
			}

			mZout.closeEntry();
			in.close();
		}

		private String getZipEntryName(String filePath) {
			String parantPath = mProcessFile.getParent();
			parantPath = removeLastSeparator(parantPath);
			return filePath.substring(parantPath.length() + 1);
		}

		private String removeLastSeparator(String path) {
			if (!path.endsWith(File.pathSeparator)) {
				return path;
			}
			return path.substring(0, path.length() - 1);
		}
	}
}
