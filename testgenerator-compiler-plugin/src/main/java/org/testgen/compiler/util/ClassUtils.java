package org.testgen.compiler.util;

public final class ClassUtils {

	private ClassUtils() {
	}

	public static String removePackageFromClass(String qualifiedClassName) {
		return qualifiedClassName.substring(qualifiedClassName.lastIndexOf('.') + 1);
	}
}
