package org.testgen.logging;

import java.util.function.Supplier;
import java.util.logging.LogRecord;

import org.testgen.logging.config.Configuration;
import org.testgen.logging.config.Level;

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

	public void error(String message, Throwable exception) {
		log(Level.ERROR, message);
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

	public void debug(String message, Supplier<String> expensiveMessage) {
		log(Level.DEBUG, message, expensiveMessage);
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
		log(Level.WARN, message);
	}

	public void warning(Supplier<String> message) {
		log(Level.WARN, message);
	}

	public void warning(Exception exception) {
		log(Level.WARN, exception);
	}

	public void trace(String message) {
		log(Level.TRACE, message);
	}

	public void trace(Supplier<String> message) {
		log(Level.TRACE, message);
	}

	public void trace(String message, Supplier<String> expensiveMessage) {
		log(Level.TRACE, message, expensiveMessage);
	}

	public void trace(Throwable exception) {
		log(Level.TRACE, exception);
	}

	private boolean isLoggable(Level level) {
		return this.config.getLevel().ordinal() >= level.ordinal();
	}

	public void log(Level level, String message) {
		if (isLoggable(level)) {
			LogRecord record = new LogRecord(java.util.logging.Level.ALL, message);
			record.setParameters(new Object[] { config });

			config.getHandlers().forEach(handler -> handler.publish(record));
		}
	}

	public void log(Level level, Throwable exception) {
		if (isLoggable(level)) {
			LogRecord record = new LogRecord(java.util.logging.Level.ALL, null);
			record.setThrown(exception);
			record.setParameters(new Object[] { config });

			config.getHandlers().forEach(handler -> handler.publish(record));
		}
	}

	public void log(Level level, Supplier<String> message) {
		if (isLoggable(level)) {
			LogRecord record = new LogRecord(java.util.logging.Level.ALL, message.get());
			record.setParameters(new Object[] { config });

			config.getHandlers().forEach(handler -> handler.publish(record));
		}
	}

	public void log(Level level, String message, Supplier<String> expensiveMessage) {
		if (isLoggable(level)) {
			String completeMessage = message + System.lineSeparator() + expensiveMessage.get();

			LogRecord record = new LogRecord(java.util.logging.Level.ALL, completeMessage);
			record.setParameters(new Object[] { config });

			config.getHandlers().forEach(handler -> handler.publish(record));
		}
	}
}
