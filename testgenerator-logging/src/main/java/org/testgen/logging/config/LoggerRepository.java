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

	private LoggerRepository() {
		loadConfiguration();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> repository.//
				forEach(config -> config.getHandlers()//
						.forEach(hdl -> hdl.close()))));
	}

	public static LoggerRepository getInstance() {
		return INSTANCE;
	}

	public Configuration getConfiguration(Class<?> clazz) {
		Optional<SimpleImmutableEntry<Configuration, Integer>> configOptional = repository.stream()
				.map(config -> new SimpleImmutableEntry<>(config,
						countEqualTokens(config.getPackageName(), clazz.getPackage().getName())))
				.max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

		if (configOptional.isPresent())
			return configOptional.get().getKey();

		else if (root != null)
			return root;

		else
			throw new NoSuchElementException(
					String.format("No matching Logger configuration found for class $s", clazz));

	}

	public Configuration getRoot() {
		return root;
	}

	private void loadConfiguration() {
		List<Configuration> configurations = XmlConfigurationLoader.//
				parseXMLConfiguration(config -> root = config);

		repository.clear();
		repository.addAll(configurations);

	}

	static Integer countEqualTokens(String packageName, String classPackage) {
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
