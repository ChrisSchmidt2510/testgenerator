package org.testgen.logging.appender;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.function.Consumer;

public interface Appender extends Closeable {

	void write(String message);

	void write(Throwable throwable);

	void write(Consumer<PrintStream> message);

}