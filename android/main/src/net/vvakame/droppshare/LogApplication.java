package net.vvakame.droppshare;

import net.vvakame.android.helper.Log;
import android.app.Application;
import android.content.pm.ApplicationInfo;

/**
 * アプリ起動時に {@link Log} の初期設定を行うための独自 {@link Application}.
 * 
 * @author vvakame
 */
public class LogApplication extends Application {

	/**
	 * {@inheritDoc}<br>
	 * 独自処理として、 {@link Log} の初期設定を行う.
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		boolean debuggable = false;
		ApplicationInfo ai = getApplicationInfo();
		if ((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
			debuggable = true;
		}
		Log.init("DroppShare", debuggable);
	}
}