package net.vvakame.android.helper;

public class HelperUtil {
	private static final String MY_PACKAGE_PREFIX = "net.vvakame";
	private static final String EXCLUDE_CLASS = HelperUtil.class
			.getCanonicalName();

	// 実行中のメソッド名を取得します
	public static String getMethodName() {
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();

		for (StackTraceElement stack : stacks) {
			String stackClass = stack.getClassName();
			if (stackClass.startsWith(MY_PACKAGE_PREFIX)
					&& !EXCLUDE_CLASS.equals(stackClass)) {
				return stack.getMethodName();
			}
		}

		return null;
	}

	// 実行中のクラス名とメソッド名を取得します
	public static String getStackName() {
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();

		for (StackTraceElement stack : stacks) {
			String stackClass = stack.getClassName();
			if (stackClass.startsWith(MY_PACKAGE_PREFIX)
					&& !EXCLUDE_CLASS.equals(stackClass)) {

				StringBuilder stb = new StringBuilder();
				stb.append(stack.getFileName().replace(".java", ""));
				stb.append("#");
				stb.append(stack.getMethodName());
				stb.append("/L");
				stb.append(stack.getLineNumber());

				return stb.toString();
			}
		}

		return null;
	}

	public static String getExceptionLog(Exception e) {
		String ret = getStackName() + ", " + e.getClass().getSimpleName() + "="
				+ e.getMessage();
		return ret;
	}
}
