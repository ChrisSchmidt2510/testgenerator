package org.testgen.testgenerator.ui.plugin.helper;

public final class Utils {

	private Utils() {
	}

	public static boolean checkStringFilled(String str) {
		return str != null && !str.trim().isEmpty();
	}

}
