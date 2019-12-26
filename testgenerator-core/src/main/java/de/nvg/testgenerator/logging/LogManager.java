package de.nvg.testgenerator.logging;

import de.nvg.testgenerator.logging.config.Configuration;
import de.nvg.testgenerator.logging.config.LoggerRepository;

public class LogManager {

	public static Logger getLogger(Class<?> clazz) {
		Configuration configuration = LoggerRepository.getInstance().getConfiguration(clazz);

		return new Logger(configuration);
	}

}
