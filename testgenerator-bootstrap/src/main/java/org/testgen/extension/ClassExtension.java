package org.testgen.extension;

import de.nvg.proxy.AbstractProxy;
import de.nvg.proxy.Proxified;

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
