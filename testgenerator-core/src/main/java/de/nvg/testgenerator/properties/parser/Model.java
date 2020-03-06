package de.nvg.testgenerator.properties.parser;

import java.util.Objects;

public class Model {
	private final String name;
	private final Parameter parameter;
	private final boolean required;

	public Model(String name) {
		this.name = name;
		this.parameter = Parameter.NO_PARAMETER;
		this.required = false;
	}

	public Model(String name, boolean required) {
		this.name = name;
		this.parameter = Parameter.NO_PARAMETER;
		this.required = required;
	}

	public Model(String name, Parameter parameter) {
		this.name = name;
		this.parameter = parameter;
		this.required = false;
	}

	public Model(String name, Parameter parameter, boolean required) {
		this.name = name;
		this.parameter = parameter;
		this.required = required;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		return Objects.equals(name, other.name);
	}

	public String getName() {
		return name;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public boolean isRequired() {
		return required;
	}

}
