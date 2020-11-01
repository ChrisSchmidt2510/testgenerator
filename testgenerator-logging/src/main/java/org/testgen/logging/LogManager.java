package org.testgen.logging;

import javax.security.auth.login.Configuration;

import org.testgen.logging.config.LoggerRepository;

public final class LogManager {

	private LogManager() {
	}

	public static Logger getLogger(Class<?> clazz) {
		Configuration configuration = LoggerRepository.getInstance().getConfiguration(clazz);

		return new Logger(configuration);
	}
}