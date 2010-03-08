package jp.ne.hatena.vvakame.droppshare;

public class HelperUtil {
	public static String getMethodName() {
		// [0]=dalvikVM [0]=getStackTrace [1]=currentMethod [2]=calleeMethod
		String methodName = Thread.currentThread().getStackTrace()[3]
				.getMethodName();

		return methodName;
	}
}
