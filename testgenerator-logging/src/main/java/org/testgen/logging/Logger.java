package org.testgen.logging;

import java.util.function.Supplier;
import java.util.logging.Level;

public class Logger {
	private final org.apache.logging.log4j.Logger logger;

	Logger(org.apache.logging.log4j.Logger logger) {
		this.logger = logger;
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

	public void trace(String message, Supplier<String> expensiveMessage) {
		log(Level.TRACE, message, expensiveMessage);
	}

	public void trace(Throwable exception) {
		log(Level.TRACE, exception);
	}

	public void log(Level level, String message) {
//		if (Level.INFO == level) {
//			logger.info(message);
//		} else if (Level.DEBUG == level) {
//			logger.debug(message);
//		} else if (Level.TRACE == level) {
//			logger.trace(message);
//		} else if (Level.WARNING == level) {
//			logger.warn(message);
//		} else if (Level.ERROR == level) {
//			logger.error(message);
//		}
	}

	public void log(Level level, Throwable exception) {
//		if (Level.INFO == level) {
//			logger.info(exception);
//		} else if (Level.DEBUG == level) {
//			logger.debug(exception);
//		} else if (Level.TRACE == level) {
//			logger.trace(exception);
//		} else if (Level.WARNING == level) {
//			logger.warn(exception);
//		} else if (Level.ERROR == level) {
//			logger.error(exception);
//		}
	}

	public void log(Level level, Supplier<?> message) {
//		if (Level.INFO == level) {
//			logger.info(message);
//		} else if (Level.DEBUG == level) {
//			logger.debug(message);
//		} else if (Level.TRACE == level) {
//			logger.trace(message);
//		} else if (Level.WARNING == level) {
//			logger.warn(message);
//		} else if (Level.ERROR == level) {
//			logger.error(message);
//		}
	}

	public void log(Level level, String message, Supplier<String> expensiveMessage) {
//		if (Level.INFO == level && logger.isInfoEnabled()) {
//			logger.info(message);
//			logger.info(expensiveMessage.get());
//		} else if (Level.DEBUG == level && logger.isDebugEnabled()) {
//			logger.debug(message);
//			logger.debug(expensiveMessage.get());
//		} else if (Level.TRACE == level && logger.isTraceEnabled()) {
//			logger.trace(message);
//			logger.trace(expensiveMessage.get());
//		} else if (Level.WARNING == level && logger.isWarnEnabled()) {
//			logger.warn(message);
//			logger.warn(expensiveMessage.get());
//		} else if (Level.ERROR == level && logger.isErrorEnabled()) {
//			logger.error(message);
//			logger.error(expensiveMessage.get());
//		}
	}
