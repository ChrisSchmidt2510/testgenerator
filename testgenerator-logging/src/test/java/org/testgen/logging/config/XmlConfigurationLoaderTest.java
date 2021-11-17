package org.testgen.logging.config;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.testgen.core.ReflectionUtil;
import org.testgen.logging.handler.ConsoleHandler;

public class XmlConfigurationLoaderTest {

	private Configuration rootLogger;

	@After
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
		Assert.assertEquals(Level.ERROR, config.getLevel());
		Assert.assertEquals(2, config.getHandlers().size());
		Assert.assertTrue(config.getHandlers().stream().anyMatch(h -> h instanceof ConsoleHandler));


		compareFileHandler(getFileHandler(config.getHandlers()));
	}
	
	private void compareConfigModification(Configuration config) {
		Assert.assertEquals(Level.TRACE, config.getLevel());
		Assert.assertEquals(1, config.getHandlers().size());
		
		compareFileHandler(getFileHandler(config.getHandlers()));
	}
	
	private void compareRootConfig(Configuration rootConfig) {
		Assert.assertEquals(Level.INFO, rootConfig.getLevel());
		Assert.assertEquals(2, rootConfig.getHandlers().size());
		
		Assert.assertTrue(rootConfig.getHandlers().stream().anyMatch(h -> h instanceof ConsoleHandler));
		
		compareFileHandler(getFileHandler(rootConfig.getHandlers()));
	}

	private void compareFileHandler(FileHandler fileHandler) {
		Assert.assertEquals("%h/testgenerator/Agent%g.log",
				ReflectionUtil.getField(FileHandler.class, "pattern", fileHandler));
		Assert.assertEquals((int) 1024 * 1024 * 1024,
				(int) ReflectionUtil.getField(FileHandler.class, "limit", fileHandler));
		Assert.assertEquals((int) 100, (int) ReflectionUtil.getField(FileHandler.class, "count", fileHandler));
		Assert.assertTrue(ReflectionUtil.getField(FileHandler.class, "append", fileHandler));
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
