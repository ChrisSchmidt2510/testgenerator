package org.testgen.runtime.generation.api.naming;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;
import org.testgen.runtime.generation.api.naming.impl.DefaultNamingService;

public class NamingServiceProvider {

	private static NamingService<?> INSTANCE;

	@SuppressWarnings("unchecked")
	public static <T> NamingService<T> getNamingService() {
		if (INSTANCE == null) {
			INSTANCE = init();
		}

		return (NamingService<T>) INSTANCE;
	}

	@SuppressWarnings("unchecked")
	private static <T> NamingService<T> init() {
		String customNamingServiceClass = TestgeneratorConfig.getCustomNamingServiceClass();

		if (customNamingServiceClass != null) {
			Class<?> clazz = ReflectionUtil.forName(customNamingServiceClass);

			ReflectionUtil.checkForInterface(clazz, NamingService.class);

			return (NamingService<T>) ReflectionUtil.newInstance(clazz);
		}

		return new DefaultNamingService<T>();
	}

}
