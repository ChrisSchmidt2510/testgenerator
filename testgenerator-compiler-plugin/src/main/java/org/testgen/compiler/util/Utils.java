package org.testgen.compiler.util;

public final class Utils {

	private Utils() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object val) {
		return (T) val;
	}
}
