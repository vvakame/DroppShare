package net.vvakame.droppshare;

public class HelperUtil {
	private static final int CALLEE_STACK = 3;

	public static String getMethodName() {
		return getMethodName(CALLEE_STACK + 1);
	}

	private static String getMethodName(int depth) {
		// [0]=dalvikVM [1]=getStackTrace [2]=currentMethod [3]=calleeMethod
		String methodName = Thread.currentThread().getStackTrace()[depth]
				.getMethodName();

		return methodName;
	}

	public static String getExceptionLog(Exception e) {
		String ret = getMethodName(CALLEE_STACK + 1) + ", "
				+ e.getClass().getSimpleName() + "=" + e.getMessage();
		return ret;
	}
}
