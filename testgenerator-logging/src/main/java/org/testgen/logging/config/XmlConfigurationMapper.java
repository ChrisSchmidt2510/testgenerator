package org.testgen.logging.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.config.xml.AppenderRefType;
import org.testgen.logging.config.xml.ConsoleAppender;
import org.testgen.logging.config.xml.LevelType;
import org.testgen.logging.config.xml.LoggerType;
import org.testgen.logging.config.xml.RollingFileAppender;
import org.testgen.logging.config.xml.RootLoggerType;
import org.testgen.logging.config.xml.SizeBasedTriggeringPolicyType;
import org.testgen.logging.formatter.PackageFormatter;
import org.testgen.logging.handler.ConsoleHandler;
import org.xml.sax.SAXException;

public class XmlConfigurationMapper {
	private static final String SCHEMA_PATH = "META-INF/testgenerator-logging.xsd";
	private static final String DEFAULT_LOGGING_XML = "META-INF/testgenerator-default-logging.xml";

	private static final String MEMORY_SIZE_KB = "KB";
	private static final String MEMORY_SIZE_MB = "MB";
	private static final String MEMORY_SIZE_GB = "GB";

	public List<Configuration> parseXMLConfiguration(Consumer<Configuration> rootLoggerAdder) {
		String loggerConfig = TestgeneratorConfig.getCustomLoggerConfig();

		if (loggerConfig == null) {
			loggerConfig = DEFAULT_LOGGING_XML;
		}

		try {
			JAXBContext context = JAXBContext.newInstance(org.testgen.logging.config.xml.Configuration.class);

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			InputStream xmlResource = classLoader.getResourceAsStream(loggerConfig);
			URL xsdUrl = classLoader.getResource(SCHEMA_PATH);

			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(xsdUrl);

			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);

			org.testgen.logging.config.xml.Configuration xmlConfiguration = (org.testgen.logging.config.xml.Configuration) unmarshaller
					.unmarshal(xmlResource);

			return mapXmlConfiguration(xmlConfiguration, rootLoggerAdder);

		} catch (JAXBException | SAXException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

	private List<Configuration> mapXmlConfiguration(org.testgen.logging.config.xml.Configuration configuration,
			Consumer<Configuration> rootLoggerAdder) {
		List<Configuration> configurations = new ArrayList<>();

		List<RollingFileAppender> fileAppenders = configuration.getAppenders().getRollingFileAppender();

		Map<String, Handler> handlerMap = new HashMap<>();

		for (RollingFileAppender fileAppender : fileAppenders) {
			int limit = 0;

			SizeBasedTriggeringPolicyType sizeTriggeringPolicy = fileAppender.getSizeBasedTriggeringPolicy();

			if (sizeTriggeringPolicy != null) {
				String size = sizeTriggeringPolicy.getSize();

				String memorySize = size.substring(size.length() - 2).toUpperCase();
				Integer base = Integer.valueOf(size.substring(0, size.length() - 2).trim());

				limit = getLogFileSize(memorySize, base);
			}

			try {
				FileHandler handler = new FileHandler(fileAppender.getFileName(), limit, 100, true);
				handler.setLevel(java.util.logging.Level.ALL);
				// TODO change with custom implementation
				handler.setFormatter(new PackageFormatter());

				handlerMap.put(fileAppender.getName(), handler);
			} catch (SecurityException | IOException e) {
				e.printStackTrace(System.err);
			}
		}

		ConsoleAppender consoleAppender = configuration.getConsoleAppender();

		if (consoleAppender != null) {
			ConsoleHandler consoleHandler = new ConsoleHandler();
			// TODO change with custom implementation
			consoleHandler.setFormatter(new PackageFormatter());
			consoleHandler.setLevel(java.util.logging.Level.ALL);

			handlerMap.put(consoleAppender.getName(), consoleHandler);
		}

		List<LoggerType> loggers = configuration.getLoggers().getLogger();

		for (LoggerType logger : loggers) {
			Configuration config = new Configuration(logger.getName(), mapLevelType(logger.getLevel()));

			for (AppenderRefType appenderRef : logger.getAppenderRef()) {
				Handler handler = handlerMap.get(appenderRef.getRef());

				if (handler != null) {
					config.addHandler(handler);
				} else {
					System.err.printf("invalid appender name: %s", appenderRef.getRef());
				}
			}

			configurations.add(config);
		}

		RootLoggerType root = configuration.getRoot();

		if (root != null) {
			Configuration rootConfig = new Configuration("root", mapLevelType(root.getLevel()));

			for (AppenderRefType appenderRef : root.getAppenderRef()) {
				Handler handler = handlerMap.get(appenderRef.getRef());

				if (handler != null) {
					rootConfig.addHandler(handler);
				} else {
					System.err.printf("invalid appender name: %s", appenderRef.getRef());
				}
			}

			rootLoggerAdder.accept(rootConfig);
		}

		return configurations;
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

	private Level mapLevelType(LevelType level) {
		switch (level) {
		case TRACE:
			return Level.TRACE;
		case DEBUG:
			return Level.DEBUG;
		case ERROR:
			return Level.ERROR;
		case INFO:
			return Level.INFO;
		case WARNING:
			return Level.WARNING;
		default:
			throw new IllegalArgumentException(String.format("Unkown level: %s", level));
		}
	}

}
