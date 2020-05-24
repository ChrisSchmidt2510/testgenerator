package org.testgen.core.logging;

import org.testgen.core.logging.config.Configuration;
import org.testgen.core.logging.config.LoggerRepository;

public final class LogManager {

	private LogManager() {
	}

	public static Logger getLogger(Class<?> clazz) {
		Configuration configuration = LoggerRepository.getInstance().getConfiguration(clazz);

		return new Logger(configuration);
	}

}
