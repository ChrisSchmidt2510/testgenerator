package org.testgen.logging.config;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;

public final class LoggerRepository {

	private static final LoggerRepository INSTANCE = new LoggerRepository();

	private static final String DELIMETER_POINT = ".";

	private List<Configuration> repository = new ArrayList<>();

	private Configuration root;

	{
//		String defaultLoggerDirectory = System.getProperty("user.home") + File.separator + "testgenerator"
//				+ File.separator;
//
//		int maxLogSize = 5_000_000;
//
//		// Default-Config
//		Appender consoleAppender = new ConsoleAppender();
//
//		Appender agentAppender = new FileAppender("Agent", maxLogSize, defaultLoggerDirectory, consoleAppender);
//		Appender testgeneratorAppender = new FileAppender("Testgeneration", maxLogSize, defaultLoggerDirectory,
//				consoleAppender);
//		Appender valueTrackerAppender = new FileAppender("ValueTracker", maxLogSize, defaultLoggerDirectory,
//				consoleAppender);

//		Configuration agentConfiguration = new Configuration("org.testgen.agent", Level.ERROR, agentAppender);
//		Configuration manipulationConfig = new Configuration("org.testgen.agent.classdata.modification", Level.ERROR,
//				agentAppender);
//		Configuration analysisConfig = new Configuration("org.testgen.agent.classdata.analysis", Level.INFO,
//				agentAppender);
//		Configuration transformerConfig = new Configuration("org.testgen.agent.transformer", Level.INFO, agentAppender);
//		Configuration instructionFilterConfiguration = new Configuration("de.nvg.agent.classdata.instructions",
//				Level.INFO, agentAppender);
//		Configuration testgeneratorConfiguration = new Configuration("org.testgen.runtime.generation", Level.DEBUG,
//				testgeneratorAppender);
//		Configuration valueTrackerConfiguration = new Configuration("org.testgen.runtime.valuetracker", Level.INFO,
//				valueTrackerAppender);

//		repository.add(agentConfiguration);
//		repository.add(manipulationConfig);
//		repository.add(analysisConfig);
//		repository.add(transformerConfig);
//		repository.add(instructionFilterConfiguration);
//		repository.add(testgeneratorConfiguration);
//		repository.add(valueTrackerConfiguration);

//		Runtime.getRuntime().addShutdownHook(new Thread(() -> repository.forEach((Configuration config) -> {
//			try {
//				config.getAppender().close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		})));
	}

	private LoggerRepository() {
		loadConfiguration();
	}

	public static LoggerRepository getInstance() {
		return INSTANCE;
	}

	public Configuration getConfiguration(Class<?> clazz) {
		Optional<SimpleImmutableEntry<Configuration, Integer>> configOptional = repository.stream()
				.map(config -> new SimpleImmutableEntry<>(config,
						countEqualTokens(config.getPackageName(), clazz.getPackage().getName())))
				.max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

		if (configOptional.isPresent()) {
			return configOptional.get().getKey();
		} else if (root != null) {
			return root;
		} else {
			throw new NoSuchElementException(
					String.format("No matching Logger configuration found for class $s", clazz));
		}

	}

	public Configuration getRoot() {
		return root;
	}

	private void loadConfiguration() {
		XmlConfigurationMapper mapper = new XmlConfigurationMapper();
		List<Configuration> configurations = mapper.parseXMLConfiguration(config -> root = config);

		repository.clear();
		repository.addAll(configurations);

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
