package de.nvg.testgenerator.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class Logger {
	private static final Logger INSTANCE = new Logger();

	private Level level;

	private String className;
	private String methodName;
	private String methodDescriptor;

	private Logger() {
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setMethodDescriptor(String methodDescriptor) {
		this.methodDescriptor = methodDescriptor;
	}

	public void resetMethod() {
		this.methodName = null;
		this.methodDescriptor = null;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public static Logger getInstance() {
		return INSTANCE;
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
			printMessage(message);
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
			printMessage(message.get());
		}
	}

	public boolean isLevelActive(Level level) {
		return this.level.ordinal() >= level.ordinal();
	}

	private void printMessage(String message) {
		if (className != null && methodName != null) {

			System.out.print(startLogMessage() + "Class: " + className + //
					" Method: " + methodName + methodDescriptor + " " + message + "\n");
		} else {
			System.out.print(startLogMessage() + message + "\n");
		}
	}

	private String startLogMessage() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " [" + level + "] ";
	}

}
