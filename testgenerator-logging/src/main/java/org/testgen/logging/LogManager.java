package org.testgen.logging;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

public final class LogManager {

	static {
		ConfigurationSource config = ConfigurationSource.fromResource("testgenerator-log4j2.xml", //
				Thread.currentThread().getContextClassLoader());
		Configurator.initialize(null, config);
	}

	private LogManager() {

	}

	public static Logger getLogger(Class<?> clazz) {
		org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger(clazz);

		return new Logger(logger);
	}

}
