package org.testgen.logging;

import org.testgen.logging.config.Configuration;
import org.testgen.logging.config.LoggerRepository;

public final class LogManager {

	private LogManager() {
	}

	public static Logger getLogger(Class<?> clazz) {
		Configuration configuration = LoggerRepository.getInstance().getConfiguration(clazz);

		return new Logger(configuration, clazz);
	}

	public static Logger getRoot() {
		Configuration root = LoggerRepository.getInstance().getRoot();

		return new Logger(root, "root");
	}
}