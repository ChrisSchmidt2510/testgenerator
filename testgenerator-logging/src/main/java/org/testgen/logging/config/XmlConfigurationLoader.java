package org.testgen.logging.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.testgen.config.TestgeneratorConfig;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlConfigurationLoader {
	private static final String SCHEMA_PATH = "META-INF/testgenerator-logging.xsd";
	private static final String DEFAULT_LOGGING_XML = "META-INF/testgenerator-default-logging.xml";

	public static List<Configuration> parseXMLConfiguration(Consumer<Configuration> rootLoggerAdder) {
		InputStream xmlResource = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		String loggerConfig = TestgeneratorConfig.getCustomLoggerConfig();

		if (loggerConfig != null) {
			try {

				Path loggerConfigPath = Paths.get(loggerConfig);
				if (loggerConfigPath.isAbsolute()) {
					byte[] bytesOfFile = Files.readAllBytes(loggerConfigPath);
					xmlResource = new ByteArrayInputStream(bytesOfFile);
				} else {
					xmlResource = classLoader.getResourceAsStream(loggerConfig);
				}

			} catch (IOException e) {
				throw new IllegalArgumentException(String.format("cant read logger config: %s", loggerConfig), e);
			}
		} else {
			xmlResource = classLoader.getResourceAsStream(DEFAULT_LOGGING_XML);
		}

		try {
			URL xsdUrl = classLoader.getResource(SCHEMA_PATH);

			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(xsdUrl);

			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setSchema(schema);

			LoggerConfigParser handler = new LoggerConfigParser(rootLoggerAdder);

			SAXParser saxParser = parserFactory.newSAXParser();
			saxParser.parse(new InputSource(xmlResource), handler);

			return handler.getConfigurations();
		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

}
