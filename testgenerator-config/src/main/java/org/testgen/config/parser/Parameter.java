package org.testgen.config.parser;

public enum Parameter {
	NO_PARAMETER, SINGLE_PARAMETER, MULTIPLE_PARAMETER;

	public boolean hasNoParameter() {
		return this == NO_PARAMETER;
	}

	public boolean hasSingleParameter() {
		return this == SINGLE_PARAMETER;
	}

	public boolean hasMultipleParameter() {
		return this == MULTIPLE_PARAMETER;
	}

	public boolean hasParameter() {
		return hasSingleParameter() || hasMultipleParameter();
	}

}
