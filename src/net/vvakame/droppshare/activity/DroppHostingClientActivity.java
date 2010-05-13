package net.vvakame.droppshare.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.vvakame.android.helper.HelperUtil;
import net.vvakame.droppshare.helper.LogTagIF;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class DroppHostingClientActivity extends Activity implements LogTagIF {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, HelperUtil.getStackName());

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		File drozip = new File(fileUri.getPath());

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "drozip";

		int bytesRead, bytesAvailable, bufferSize;

		int maxBufferSize = 1 * 1024 * 1024;

		String responseFromServer = "";

		HttpURLConnection conn = null;

		try {
			FileInputStream fileInputStream = new FileInputStream(drozip);

			URL url = new URL("http://192.168.0.6:8888/upload");

			conn = (HttpURLConnection) url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ drozip.getName() + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				Log.i(TAG, "Message: " + line);
				responseFromServer += line;
			}
			rd.close();

		} catch (IOException e) {
			Log.e(TAG, HelperUtil.getExceptionLog(e));
		}
	}
}
