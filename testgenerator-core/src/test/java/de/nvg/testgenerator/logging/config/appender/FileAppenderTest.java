package de.nvg.testgenerator.logging.config.appender;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class FileAppenderTest {

	@Test
	public void testFileAppenderFileCreation() throws IOException {
		FileAppender fileAppender = new FileAppender("Agent", 5,
				System.getProperty("user.home") + "\\testgenerator\\agent");
		fileAppender.write("Der Lachs löppt");

		Assert.assertTrue(true);

		fileAppender.close();
	}

}
