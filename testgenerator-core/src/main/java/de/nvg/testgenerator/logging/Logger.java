package de.nvg.testgenerator.logging;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import de.nvg.testgenerator.logging.config.Configuration;
import de.nvg.testgenerator.logging.config.Level;

public class Logger {

	private final Configuration config;

	Logger(Configuration config) {
		this.config = config;
	}

	public void error(String message) {
		log(Level.ERROR, message);
	}

	public void error(Throwable exception) {
		log(Level.ERROR, exception);
	}

	public void error(Supplier<String> message) {
		log(Level.ERROR, message);
	}

	public void debug(String message) {
		log(Level.DEBUG, message);
	}

	public void debug(Supplier<String> message) {
		log(Level.DEBUG, message);
	}

	public void debug(Throwable exception) {
		log(Level.DEBUG, exception);
	}

	public void info(String message) {
		log(Level.INFO, message);
	}

	public void info(Supplier<String> message) {
		log(Level.INFO, message);
	}

	public void info(Throwable exception) {
		log(Level.INFO, exception);
	}

	public void warning(String message) {
		log(Level.WARNING, message);
	}

	public void warning(Supplier<String> message) {
		log(Level.WARNING, message);
	}

	public void warning(Exception exception) {
		log(Level.WARNING, exception);
	}

	public void trace(String message) {
		log(Level.TRACE, message);
	}

	public void trace(Supplier<String> message) {
		log(Level.TRACE, message);
	}

	public void trace(Throwable exception) {
		log(Level.TRACE, exception);
	}

	public void log(Level level, String message) {
		if (isLevelActive(level)) {
			printMessage(level, message);
		}
	}

	public void log(Level level, Throwable exception) {
		if (isLevelActive(level)) {
			exception.printStackTrace();

			throw new RuntimeException(exception);
		}
	}

	public void log(Level level, Supplier<String> message) {
		if (isLevelActive(level)) {
			printMessage(level, message.get());
		}
	}

	public boolean isLevelActive(Level level) {
		return this.config.getLevel().ordinal() >= level.ordinal();
	}

	private void printMessage(Level messageLevel, String message) {
		config.getOutputStream().forEach(stream -> {
			try {
				stream.write((startLogMessage(messageLevel) + message + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private String startLogMessage(Level messageLevel) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " [" + messageLevel
				+ "] ";
	}

}
