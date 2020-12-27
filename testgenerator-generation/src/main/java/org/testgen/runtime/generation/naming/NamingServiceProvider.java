package org.testgen.runtime.generation.naming;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;
import org.testgen.runtime.generation.naming.impl.DefaultNamingService;

public class NamingServiceProvider {

	private static NamingService INSTANCE;

	public static NamingService getNamingService() {
		if (INSTANCE == null) {
			INSTANCE = init();
		}

		return INSTANCE;
	}

	private static NamingService init() {
		String customNamingServiceClass = TestgeneratorConfig.getCustomNamingServiceClass();

		if (customNamingServiceClass != null) {
			Class<?> clazz = ReflectionUtil.forName(customNamingServiceClass);

			ReflectionUtil.checkForInterface(clazz, NamingService.class);

			return (NamingService) ReflectionUtil.newInstance(clazz);
		}

		return new DefaultNamingService();
	}

}
