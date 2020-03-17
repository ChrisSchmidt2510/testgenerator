package de.nvg.testgenerator.logging.config;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import de.nvg.testgenerator.logging.config.appender.Appender;
import de.nvg.testgenerator.logging.config.appender.ConsoleAppender;
import de.nvg.testgenerator.logging.config.appender.FileAppender;

public final class LoggerRepository {

	private static final LoggerRepository INSTANCE = new LoggerRepository();

	private static final String DELIMETER_POINT = ".";

	private List<Configuration> repository = new ArrayList<>();

	{
		String defaultLoggerDirectory = System.getProperty("user.home") + File.separator + "testgenerator"
				+ File.separator;

		int maxLogSize = 5_000_000;

		// Default-Config
		Appender consoleAppender = new ConsoleAppender();

		Appender agentAppender = new FileAppender("Agent", maxLogSize, defaultLoggerDirectory, consoleAppender);
		Appender testgeneratorAppender = new FileAppender("Testgeneration", maxLogSize, defaultLoggerDirectory,
				consoleAppender);
		Appender valueTrackerAppender = new FileAppender("ValueTracker", maxLogSize, defaultLoggerDirectory,
				consoleAppender);

		Configuration agentConfiguration = new Configuration("de.nvg.agent", Level.INFO, agentAppender);
		Configuration manipulationConfig = new Configuration("de.nvg.agent.classdata.modification", Level.INFO,
				agentAppender);
		Configuration analysisConfig = new Configuration("de.nvg.agent.classdata.analysis", Level.INFO, agentAppender);
		Configuration transformerConfig = new Configuration("de.nvg.agent.transformer", Level.INFO, agentAppender);
		Configuration instructionFilterConfiguration = new Configuration("de.nvg.agent.classdata.instructions",
				Level.DEBUG, agentAppender);
		Configuration testgeneratorConfiguration = new Configuration("de.nvg.testgenerator", Level.INFO,
				testgeneratorAppender);
		Configuration valueTrackerConfiguration = new Configuration("de.nvg.valuetracker", Level.INFO,
				valueTrackerAppender);

		repository.add(agentConfiguration);
		repository.add(manipulationConfig);
		repository.add(analysisConfig);
		repository.add(transformerConfig);
		repository.add(instructionFilterConfiguration);
		repository.add(testgeneratorConfiguration);
		repository.add(valueTrackerConfiguration);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> repository.forEach((Configuration config) -> {
			try {
				config.getAppender().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		})));
	}

	private LoggerRepository() {
	}

	public static LoggerRepository getInstance() {
		return INSTANCE;
	}

	public Configuration getConfiguration(Class<?> clazz) {
		return repository.stream()
				.map(config -> new SimpleImmutableEntry<>(config,
						countEqualTokens(config.getPackageName(), clazz.getPackage().getName())))
				.max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
				.orElseThrow(() -> new NoSuchElementException("No Logger-Configuration matched")).getKey();

	}

	private static Integer countEqualTokens(String packageName, String classPackage) {
		StringTokenizer packageTokenizer = new StringTokenizer(packageName, DELIMETER_POINT);
		StringTokenizer classTokenizer = new StringTokenizer(classPackage, DELIMETER_POINT);

		if (packageTokenizer.countTokens() > classTokenizer.countTokens()) {
			return 0;
		}

		int tokens = 0;

		while (packageTokenizer.hasMoreTokens()) {
			if (!packageTokenizer.nextToken().equals(classTokenizer.nextToken())) {
				return 0;
			}
			tokens++;
		}
		return tokens;
	}

}
