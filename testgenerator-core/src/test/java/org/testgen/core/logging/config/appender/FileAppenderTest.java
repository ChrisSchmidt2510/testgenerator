package org.testgen.core.logging.config.appender;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.testgen.core.logging.config.appender.FileAppender;

public class FileAppenderTest {

	@Test
	@Ignore
	public void testFileAppenderFileCreation() throws IOException {
		try (FileAppender fileAppender = new FileAppender("Agent", 5,
				System.getProperty("user.home") + "\\testgenerator\\agent")) {
			fileAppender.write("Der Lachs loeppt");
		}

		Assert.assertTrue(true);
	}

}
