package org.testgen.logging.handler;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class ConsoleHandler extends StreamHandler {

	public ConsoleHandler() {
		setOutputStream(System.out);
	}

	@Override
	public synchronized void publish(LogRecord record) {
		super.publish(record);
		flush();
	}

	@Override
	public synchronized void close() throws SecurityException {
		flush();
	}

}
