package net.vvakame.droppshare.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpPostMultipartWrapper {

	private static final String LINE_END = "\r\n";
	private static final String TWO_HYPHENS = "--";
	private static final String BOUNDARY = "-drozipQawsedrftgyhujikolp";
	private static final String DATA_END = TWO_HYPHENS + LINE_END;

	private URL mUrl = null;
	private HttpURLConnection mCon = null;
	private DataOutputStream mDos = null;

	public HttpPostMultipartWrapper(String url) throws MalformedURLException {
		this(new URL(url));
	}

	public HttpPostMultipartWrapper(URL url) {
		if (url == null) {
			throw new IllegalArgumentException();
		}
		mUrl = url;
	}

	private void connect() throws IOException {
		if (mCon == null) {
			mCon = (HttpURLConnection) mUrl.openConnection();

			mCon.setDoInput(true);
			mCon.setDoOutput(true);
			mCon.setUseCaches(false);

			mCon.setRequestMethod("POST");
			mCon.setRequestProperty("Connection", "Keep-Alive");

			/*
			 * mCon.setRequestProperty("Content-Type",
			 * "multipart/form-data;boundary=" + BOUNDARY);
			 */

			mCon.setRequestProperty("Content-Type", "multipart/mixed;boundary="
					+ BOUNDARY);

			mDos = new DataOutputStream(mCon.getOutputStream());

			elementEnd();
		}
	}

	public void close() throws IOException {
		mDos.writeBytes(DATA_END);
		mDos.flush();
		mDos.close();
	}

	public String readResponse() throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(mCon
				.getInputStream()));

		StringBuilder stb = new StringBuilder();
		String line = null;
		while ((line = rd.readLine()) != null) {
			stb.append(line);
		}
		rd.close();

		return stb.toString();
	}

	public InputStream getResponseStream() throws IOException {
		return mCon.getInputStream();
	}

	private void elementEnd() throws IOException {
		mDos.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
	}

	public void pushString(String name, String value) throws IOException {
		connect();

		mDos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""
				+ LINE_END);
		mDos.writeBytes(LINE_END);
		mDos.writeBytes(value);
		mDos.writeBytes(LINE_END);

		elementEnd();

		mDos.flush();
	}

	public void pushString(String name, int value) throws IOException {
		pushString(name, String.valueOf(value));
	}

	public void pushFile(String name, File file) throws FileNotFoundException,
			IOException {
		pushFile(name, file.getName(), file);
	}

	public void pushFile(String name, String fileName, File file)
			throws FileNotFoundException, IOException {
		connect();

		int bytesRead;
		int bytesAvailable;
		int bufferSize;

		int maxBufferSize = 1 * 1024 * 1024;

		FileInputStream fileInputStream = new FileInputStream(file);

		mDos
				.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
						+ fileName + "\"" + LINE_END);
		mDos.writeBytes("Content-Type: application/octet-stream" + LINE_END);
		mDos.writeBytes(LINE_END);

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		byte[] buffer = new byte[bufferSize];

		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		while (bytesRead > 0) {
			mDos.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		mDos.writeBytes(LINE_END);

		elementEnd();

		fileInputStream.close();
		mDos.flush();
	}
}
