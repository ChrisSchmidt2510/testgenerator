package de.nvg.testgenerator.logging.config.appender;

import java.io.IOException;

public class ConsoleAppender implements Appender {

	@Override
	public void close() throws IOException {
		// nothing to close
	}

	@Override
	public void write(String message) {
		System.out.print(message);
	}

}
