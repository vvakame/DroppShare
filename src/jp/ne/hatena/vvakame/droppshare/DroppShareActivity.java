package jp.ne.hatena.vvakame.droppshare;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

public class DroppShareActivity extends Activity {
	private PackageManager mPm = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPm = getPackageManager();
		List<ApplicationInfo> appInfoList = mPm
				.getInstalledApplications(PackageManager.GET_ACTIVITIES);

		Collections.sort(appInfoList, new Comparator<ApplicationInfo>() {
			@Override
			public int compare(ApplicationInfo obj1, ApplicationInfo obj2) {
				String str1 = mPm.getApplicationLabel(obj1).toString();
				String str2 = mPm.getApplicationLabel(obj2).toString();
				return str1.compareTo(str2);
			}
		});

		// ListViewé¸ÇËÇÃèàóù
		AppDataAdapter appDataAdapter = new AppDataAdapter(this,
				R.layout.application_view, appInfoList);
		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setAdapter(appDataAdapter);
	}
}