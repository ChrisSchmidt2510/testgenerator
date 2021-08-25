package org.testgen.logging;

import java.util.function.Consumer;

public class HelperObject {
	private static final int STACK_DEPTH =2;
	
	public static void catchRuntimeException(Consumer<Throwable> consumer) {
		try {
			throwRuntimeException();
		} catch (RuntimeException e) {
			modifyStackTrace(e);
			consumer.accept(e);
		}
	}

	private static void throwRuntimeException() {
		throw new RuntimeException("sth bad happend");
	}

	private static void modifyStackTrace(RuntimeException exception) {
		StackTraceElement[] stackTrace = exception.getStackTrace();

		StackTraceElement[] modifiedStackTrace = new StackTraceElement[STACK_DEPTH];

		for (int i = 0; i < STACK_DEPTH; i++) {
			modifiedStackTrace[i] = stackTrace[i];
		}

		exception.setStackTrace(modifiedStackTrace);
	}
}
