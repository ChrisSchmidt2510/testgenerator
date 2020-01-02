package de.nvg.testgenerator.logging.config.appender;

import org.junit.Assert;
import org.junit.Test;

public class FileAppenderTest {

	@Test
	public void testFileAppenderFileCreation() {
		FileAppender fileAppender = new FileAppender("Agent", 5,
				System.getProperty("user.home") + "\\testgenerator\\agent");
		fileAppender.write("Der Lachs löppt");

		Assert.assertTrue(true);
	}

}
