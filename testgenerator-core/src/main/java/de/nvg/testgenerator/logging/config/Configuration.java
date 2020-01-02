package de.nvg.testgenerator.logging.config;

import de.nvg.testgenerator.logging.config.appender.Appender;

public class Configuration {
	private final String packageName;
	private final Level level;
	private final Appender appender;

	public Configuration(String packageName, Level level, Appender appender) {
		this.packageName = packageName;
		this.level = level;
		this.appender = appender;
	}

	public Level getLevel() {
		return level;
	}

	public Appender getAppender() {
		return appender;
	}

	public String getPackageName() {
		return packageName;
	}

}
