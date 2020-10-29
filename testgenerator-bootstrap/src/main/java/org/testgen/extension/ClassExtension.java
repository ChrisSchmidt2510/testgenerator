package org.testgen.extension;

import org.testgen.runtime.proxy.AbstractProxy;
import org.testgen.runtime.proxy.Proxified;

public final class ClassExtension {

	private ClassExtension() {
	}

	public static boolean isProxifiedClass(Class<?> clazz) {
		return Proxified.class.isAssignableFrom(clazz);
	}

	public static boolean isProxy(Class<?> clazz) {
		return AbstractProxy.class.isAssignableFrom(clazz);
	}

}
