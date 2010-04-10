package net.vvakame.droppshare;

import net.vvakame.droppshare.helper.HelperUtil;
import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;
import android.util.Log;

public class AllTests extends TestSuite {
	private static final String TAG = AllTests.class.getSimpleName();

	public static Test suite() {
		Log.d(TAG, TAG + ":" + HelperUtil.getMethodName());

		TestSuite testSuite = new TestSuiteBuilder(AllTests.class)
				.includeAllPackagesUnderHere().build();

		return testSuite;
	}
}
