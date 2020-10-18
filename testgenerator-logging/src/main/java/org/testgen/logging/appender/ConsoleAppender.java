package org.testgen.logging.appender;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Consumer;

public class ConsoleAppender implements Appender {

	@Override
	public void close() throws IOException {
		// nothing to close
	}

	@Override
	public void write(String message) {
		System.out.print(message);
	}

	@Override
	public void write(Throwable throwable) {
		throwable.printStackTrace();
	}

	@Override
	public void write(Consumer<PrintStream> message) {
		message.accept(System.out);
	}

}
