package jp.ne.hatena.vvakame.droppshare;

public class HelperUtil {
	private static final int CALLEE_STACK = 3;

	public static String getMethodName() {
		// [0]=dalvikVM [1]=getStackTrace [2]=currentMethod [3]=calleeMethod
		String methodName = Thread.currentThread().getStackTrace()[CALLEE_STACK]
				.getMethodName();

		return methodName;
	}
}
