package org.testgen.logging.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Handler;

public class Configuration {
	private final String packageName;
	private final Level level;

	private List<Handler> handlers = new ArrayList<>();

	public Configuration(String packageName, Level level) {
		this.packageName = packageName;
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	public String getPackageName() {
		return packageName;
	}

	public void addHandler(Handler handler) {
		handlers.add(handler);
	}

	public List<Handler> getHandlers() {
		return handlers;
	}
	
	public void forEachHandler(Consumer<Handler> action) {
		handlers.forEach(action);
	}

}
