package org.testgen.logging.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import org.testgen.logging.formatter.PackageFormatter;
import org.testgen.logging.handler.ConsoleHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LoggerConfigParser extends DefaultHandler {

	private static final String TAG_ROLLING_FILE_APPENDER = "RollingFileAppender";
	private static final String TAG_CONSOLE_APPENDER = "ConsoleAppender";
	private static final String APPENDER_ATTRIBUTE_FILENAME = "fileName";
	private static final String APPENDER_ATTRIBUTE_NAME = "name";

	private static final String TAG_SIZE_BASED_TRIGGER_POLICY = "SizeBasedTriggeringPolicy";
	private static final String POLICY_ATTRIBUTE_SIZE = "size";
	private static final String MEMORY_SIZE_KB = "KB";
	private static final String MEMORY_SIZE_MB = "MB";
	private static final String MEMORY_SIZE_GB = "GB";

	private static final String TAG_LOGGER = "Logger";
	private static final String LOGGER_ATTRIBUTE_NAME = "name";
	private static final String LOGGER_ATTRIBUTE_LEVEL = "level";

	private static final String LEVEL_TRACE = "TRACE";
	private static final String LEVEL_DEBUG = "DEBUG";
	private static final String LEVEL_INFO = "INFO";
	private static final String LEVEL_WARN = "WARN";
	private static final String LEVEL_ERROR = "ERROR";

	private static final String TAG_APPENDER_REF = "AppenderRef";
	private static final String APPENDER_REF_ATTRIBUTE_REF = "ref";

	private static final String TAG_ROOT = "Root";

	private final Consumer<Configuration> rootLoggerAdder;

	private Configuration currentConfig;

	private TempFileHandler currentHandler;

	private Map<String, Handler> handlers = new HashMap<>();

	private List<Configuration> finishedConfigs = new ArrayList<>();

	public LoggerConfigParser(Consumer<Configuration> rootLoggerAdder) {
		this.rootLoggerAdder = rootLoggerAdder;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (TAG_ROLLING_FILE_APPENDER.equals(qName)) {
			currentHandler = new TempFileHandler(attributes.getValue(APPENDER_ATTRIBUTE_NAME),
					attributes.getValue(APPENDER_ATTRIBUTE_FILENAME));
		} else if (TAG_SIZE_BASED_TRIGGER_POLICY.equals(qName)) {
			String size = attributes.getValue(POLICY_ATTRIBUTE_SIZE);

			String memorySize = size.substring(size.length() - 2).toUpperCase();
			Integer base = Integer.valueOf(size.substring(0, size.length() - 2).trim());

			currentHandler.size = getLogFileSize(memorySize, base);
		} else if (TAG_CONSOLE_APPENDER.equals(qName)) {
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new PackageFormatter());
			consoleHandler.setLevel(java.util.logging.Level.ALL);

			handlers.put(attributes.getValue(APPENDER_ATTRIBUTE_NAME), consoleHandler);
		} else if (TAG_LOGGER.equals(qName)) {
			String unmappedLevel = attributes.getValue(LOGGER_ATTRIBUTE_LEVEL);
			Level level = mapLevel(unmappedLevel);

			currentConfig = new Configuration(attributes.getValue(LOGGER_ATTRIBUTE_NAME), level);
		} else if (TAG_APPENDER_REF.equals(qName)) {
			String ref = attributes.getValue(APPENDER_REF_ATTRIBUTE_REF);
			Handler handler = handlers.get(ref);

			currentConfig.addHandler(handler);
		} else if (TAG_ROOT.equals(qName)) {
			String unmappedLevel = attributes.getValue(LOGGER_ATTRIBUTE_LEVEL);
			Level level = mapLevel(unmappedLevel);

			currentConfig = new Configuration("root", level);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (TAG_ROLLING_FILE_APPENDER.equals(qName)) {
			try {
				FileHandler handler = new FileHandler(currentHandler.filename, currentHandler.size, 100, true);
				handler.setLevel(java.util.logging.Level.ALL);
				handler.setFormatter(new PackageFormatter());

				handlers.put(currentHandler.name, handler);
			} catch (SecurityException | IOException e) {
				e.printStackTrace(System.err);
			}

			currentHandler = null;
		} else if (TAG_LOGGER.equals(qName)) {
			finishedConfigs.add(currentConfig);

			currentConfig = null;
		} else if (TAG_ROOT.equals(qName)) {
			rootLoggerAdder.accept(currentConfig);

			currentConfig = null;
		}
	}

	public List<Configuration> getConfigurations() {
		return finishedConfigs;
	}

	private int getLogFileSize(String memorySize, Integer baseSize) {
		switch (memorySize) {
		case MEMORY_SIZE_KB:
			return baseSize * 1024;
		case MEMORY_SIZE_MB:
			return baseSize * 1024 * 1024;
		case MEMORY_SIZE_GB:
			return baseSize * 1024 * 1024 * 1024;
		default:
			throw new IllegalArgumentException(String.format("Unsupported Memorytype: \"%s\"", memorySize));
		}
	}

	private Level mapLevel(String unmappedLevel) {
		if (LEVEL_TRACE.equals(unmappedLevel)) {
			return Level.TRACE;
		} else if (LEVEL_DEBUG.equals(unmappedLevel)) {
			return Level.DEBUG;
		} else if (LEVEL_WARN.equals(unmappedLevel)) {
			return Level.WARN;
		} else if (LEVEL_INFO.equals(unmappedLevel)) {
			return Level.INFO;
		} else if (LEVEL_ERROR.equals(unmappedLevel)) {
			return Level.ERROR;
		}

		throw new IllegalArgumentException(String.format("invalid level: $s", unmappedLevel));
	}

	private class TempFileHandler {
		private final String name;
		private final String filename;
		private int size;

		TempFileHandler(String name, String fileName) {
			this.name = name;
			this.filename = fileName;
		}
	}

}
