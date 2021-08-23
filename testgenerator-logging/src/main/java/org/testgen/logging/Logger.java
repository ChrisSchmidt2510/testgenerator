package org.testgen.logging;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.LogRecord;

import org.testgen.logging.config.Configuration;
import org.testgen.logging.config.Level;

public class Logger{
	private final Configuration config;
	private final String name;

	Logger(Configuration config, Class<?> loggerClass) {
		this.config = config;
		this.name = loggerClass.getSimpleName();
	}

	Logger(Configuration config, String name) {
		this.config = config;
		this.name = name;
	}

	public void error(String message) {
		log(Level.ERROR, message);
	}

	public void error(String message, Throwable exception) {
		log(Level.ERROR, message, exception);
	}
	
	public void error(String message, Supplier<String> expensiveMessage) {
		log(Level.ERROR, message, expensiveMessage);
	}

	public void warn(String message) {
		log(Level.WARN, message);	
	}

	public void warn(String message, Throwable exception) {
		log(Level.WARN, message, exception);
	}

	public void warn(String message, Supplier<String> expensiveMessage) {
		log(Level.WARN, message, expensiveMessage);
	}

	public void info(String message) {
		log(Level.INFO, message);
	}

	public void info(String message, Throwable exception) {
		log(Level.INFO, message, exception);
	}

	public void info(String message, Supplier<String> expensiveMessage) {
		log(Level.INFO, message, expensiveMessage);
	}
	
	public void debug(String message) {
		log(Level.DEBUG, message);
	}

	public void debug(String message, Throwable exception) {
		log(Level.DEBUG, message, exception);
	}
	
	public void debug(String message, Supplier<String> expensiveMessage) {
		log(Level.DEBUG, message, expensiveMessage);
	}

	public void trace(String message) {
		log(Level.TRACE, message);
	}
	
	public void trace(String message, Throwable exception) {
		log(Level.TRACE, message, exception);
	}

	public void trace(String message, Supplier<String> expensiveMessage) {
		log(Level.TRACE, message, expensiveMessage);
	}

	private boolean isLoggable(Level level) {
		return this.config.getLevel().ordinal() >= level.ordinal();
	}

	private void log(Level level, String message) {
		if (isLoggable(level)) {
			LogRecord record = new LogRecord(java.util.logging.Level.ALL, message);
			record.setParameters(new Object[] { level, name });

			config.forEachHandler(handler -> handler.publish(record));
		}
	}

	private void log(Level level, String message, Supplier<String> expensiveMessage) {
		Objects.requireNonNull(expensiveMessage, "expeniveMessage is required");
		
		if (isLoggable(level)) {
			String completeMessage = message == null ||message.isEmpty() ? expensiveMessage.get() : message + System.lineSeparator() + expensiveMessage.get();

			LogRecord record = new LogRecord(java.util.logging.Level.ALL, completeMessage);
			record.setParameters(new Object[] { level, name });

			config.forEachHandler(handler -> handler.publish(record));
		}
	}

	private void log(Level level, String message, Throwable exception) {
		Objects.requireNonNull(exception, "exception is required");
		
		if (isLoggable(level)) {
			LogRecord record = new LogRecord(java.util.logging.Level.ALL, message == null || message.isEmpty() ? "": message);
			record.setParameters(new Object[] { level, name });
			record.setThrown(exception);
			config.forEachHandler(handler -> handler.publish(record));
		}
	}
}
