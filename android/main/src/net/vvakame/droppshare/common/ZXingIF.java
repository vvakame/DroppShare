package net.vvakame.droppshare.common;

/**
 * ZXing回りの定数をまとめたIF
 */
public interface ZXingIF {
	public final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
	public final String ACTION_ENCODE = "com.google.zxing.client.android.ENCODE";

	public final String SCAN_MODE_QR_CODE = "QR_CODE_MODE";

	public final String SCAN_RESULT = "SCAN_RESULT";
	public final String SCAN_RESULT_FORMAT = "SCAN_RESULT_FORMAT";

	public final String ENCODE_TYPE = "ENCODE_TYPE";
	public final String ENCODE_DATA = "ENCODE_DATA";

	public final String ENCODE_TYPE_TEXT = "TEXT_TYPE";

	public final String BUTTON_TEXT = "org.openintents.extra.BUTTON_TEXT";
}