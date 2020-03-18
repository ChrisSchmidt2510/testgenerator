package de.nvg.testgenerator.logging;

import de.nvg.testgenerator.logging.config.Configuration;
import de.nvg.testgenerator.logging.config.LoggerRepository;

public final class LogManager {

	private LogManager() {
	}

	public static Logger getLogger(Class<?> clazz) {
		Configuration configuration = LoggerRepository.getInstance().getConfiguration(clazz);

		return new Logger(configuration);
	}

}
