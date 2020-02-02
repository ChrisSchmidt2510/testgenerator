package de.nvg.javaagent.classdata.modification.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExceptionHandler {
	private final List<ExceptionHandlerModel> exceptionHandlers = new ArrayList<>();
	private int startIndex = 0;

	public void addExceptionHandler(int endIndex, int codeLength, String exceptionClassName) {
		ExceptionHandlerModel handler = new ExceptionHandlerModel(startIndex, endIndex, exceptionClassName);
		exceptionHandlers.add(handler);

		startIndex = endIndex + codeLength;
	}

	public List<ExceptionHandlerModel> getExceptionHandlers() {
		return Collections.unmodifiableList(exceptionHandlers);
	}

	public class ExceptionHandlerModel {
		/** included */
		public final int startIndex;
		/** excluded */
		public final int endIndex;
		public final String exeptionClassName;

		public ExceptionHandlerModel(int startIndex, int endIndex, String exceptionClassName) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.exeptionClassName = exceptionClassName;
		}
	}
}
