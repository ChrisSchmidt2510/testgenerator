package de.nvg.testgenerator.logging.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class LoggerRepository {
	private static final LoggerRepository INSTANCE = new LoggerRepository();

	private static final String defaultLogDirectorie = System.getProperty("user.home") + "\\testgenerator";

	private Map<String, Configuration> repository = new HashMap<>();

	private FileOutputStream fios;

	private Configuration defaultConfig;

	{
		File outputDirectorie = createDirectorie(defaultLogDirectorie);

		outputDirectorie = new File(outputDirectorie.getPath() + "\\Testgenerator.log");

		try {
			fios = new FileOutputStream(outputDirectorie, true);

			defaultConfig = new Configuration(Level.INFO, System.out, fios);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> repository.forEach((key, value) -> {
			for (OutputStream outputStream : value.getOutputStream()) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		})));

	}

	private LoggerRepository() {
	}

	public static LoggerRepository getInstance() {
		return INSTANCE;
	}

	public Configuration getConfiguration(Class<?> clazz) {
		clazz.getPackage().getName();

		return repository.getOrDefault(clazz, defaultConfig);
	}

	private File createDirectorie(String directory) {
		File outputDirectorie = new File(directory);

		if (!outputDirectorie.exists()) {
			outputDirectorie.mkdir();
		}

		return outputDirectorie;
	}

}
