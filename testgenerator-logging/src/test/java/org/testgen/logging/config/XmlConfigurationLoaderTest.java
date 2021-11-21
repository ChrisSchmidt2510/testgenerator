package org.testgen.logging.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testgen.core.ReflectionUtil;
import org.testgen.logging.handler.ConsoleHandler;

public class XmlConfigurationLoaderTest {

	private Configuration rootLogger;

	@AfterEach
	public void reset() {
		rootLogger = null;
	}

	@Test
	public void testParseXMLConfiguration() {
		String xmlPath = "META-INF/test-logger-config.xml";

		InputStream xmlResource = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlPath);

		Consumer<Configuration> rootAdder = root -> rootLogger = root;

		List<Configuration> xmlConfig = XmlConfigurationLoader.parseXmlConfiguration(xmlResource, rootAdder);

		compareConfigOrgTestgenAgent(getConfig(xmlConfig, "org.testgen.agent"));
		compareConfigModification(getConfig(xmlConfig, "org.testgen.agent.classdata.modification"));
		compareRootConfig(rootLogger);
	}

	private void compareConfigOrgTestgenAgent(Configuration config) {
		assertEquals(Level.ERROR, config.getLevel());
		assertEquals(2, config.getHandlers().size());
		assertTrue(config.getHandlers().stream().anyMatch(h -> h instanceof ConsoleHandler));


		compareFileHandler(getFileHandler(config.getHandlers()));
	}
	
	private void compareConfigModification(Configuration config) {
		assertEquals(Level.TRACE, config.getLevel());
		assertEquals(1, config.getHandlers().size());
		
		compareFileHandler(getFileHandler(config.getHandlers()));
	}
	
	private void compareRootConfig(Configuration rootConfig) {
		assertEquals(Level.INFO, rootConfig.getLevel());
		assertEquals(2, rootConfig.getHandlers().size());
		
		assertTrue(rootConfig.getHandlers().stream().anyMatch(h -> h instanceof ConsoleHandler));
		
		compareFileHandler(getFileHandler(rootConfig.getHandlers()));
	}

	private void compareFileHandler(FileHandler fileHandler) {
		assertEquals("%h/testgenerator/Agent%g.log",
				ReflectionUtil.getField(FileHandler.class, "pattern", fileHandler));
		assertEquals((int) 1024 * 1024 * 1024,
				(int) ReflectionUtil.getField(FileHandler.class, "limit", fileHandler));
		assertEquals((int) 100, (int) ReflectionUtil.getField(FileHandler.class, "count", fileHandler));
		assertTrue((boolean) ReflectionUtil.getField(FileHandler.class, "append", fileHandler));
	}
	
	private Configuration getConfig(List<Configuration> configs, String packageName) {
		return configs.stream()
				.filter(config -> config.getPackageName().equals(packageName)).findAny()
				.orElseThrow(() -> new IllegalArgumentException("config for package " +packageName+ " must exist"));
	}
	
	private FileHandler getFileHandler(List<Handler> handlers) {
		return handlers.stream().filter(h -> h instanceof FileHandler)
		.map(h -> (FileHandler) h).findAny().orElseThrow(() -> new IllegalArgumentException());

	}

}
