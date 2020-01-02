package de.nvg.testgenerator.logging.config.appender;

import java.io.Closeable;

public interface Appender extends Closeable {

	public void write(String message);

}
