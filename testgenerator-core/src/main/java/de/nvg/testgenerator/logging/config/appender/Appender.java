package de.nvg.testgenerator.logging.config.appender;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.function.Consumer;

public interface Appender extends Closeable {

	public void write(String message);

	public void write(Throwable throwable);

	public void write(Consumer<PrintStream> message);

}
