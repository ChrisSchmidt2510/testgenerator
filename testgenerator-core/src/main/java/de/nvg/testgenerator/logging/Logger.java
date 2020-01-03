package de.nvg.testgenerator.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import de.nvg.testgenerator.logging.config.Level;
import de.nvg.testgenerator.logging.config.appender.Appender;

public class Logger {
	private final Level level;
	private final Appender appender;

	Logger(Level level, Appender appender) {
		this.level = level;
		this.appender = appender;
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
			appender.write(exception);
		}
	}

	public void log(Level level, Supplier<String> message) {
		if (isLevelActive(level)) {
			printMessage(level, message.get());
		}
	}

	public boolean isLevelActive(Level level) {
		return this.level.ordinal() >= level.ordinal();
	}

	private void printMessage(Level messageLevel, String message) {
		appender.write((startLogMessage(messageLevel) + message + "\n"));
	}

	private String startLogMessage(Level messageLevel) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " [" + messageLevel
				+ "] ";
	}

}
