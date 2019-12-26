package de.nvg.testgenerator.logging.config;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configuration {
	private final Level level;
	private final List<OutputStream> outputStream;

	public Configuration(Level level, OutputStream outputStream) {
		this.level = level;
		this.outputStream = Collections.singletonList(outputStream);
	}

	public Configuration(Level level, OutputStream... outputStream) {
		this.level = level;
		this.outputStream = Arrays.asList(outputStream);
	}

	public Level getLevel() {
		return level;
	}

	public List<OutputStream> getOutputStream() {
		return outputStream;
	}

}
