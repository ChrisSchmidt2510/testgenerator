package de.nvg.testgenerator.properties.parser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Arguments {

	private Set<Model> arguments = new HashSet<>();

	public void addArgument(String name) {
		arguments.add(new Model(name));
	}

	public void addArgument(String name, boolean required) {
		arguments.add(new Model(name, required));
	}

	public void addArgument(String name, Parameter parameter) {
		arguments.add(new Model(name, parameter));
	}

	public void addArgument(String name, Parameter parameter, boolean required) {
		arguments.add(new Model(name, parameter, required));
	}

	public Model getArgument(String name) {
		return arguments.stream().filter(arg -> arg.getName().equals(name)).findAny()
				.orElseThrow(() -> new IllegalArgumentException(name + "is not a valid Argument"));
	}

	public Set<Model> getAllArguments() {
		return Collections.unmodifiableSet(arguments);
	}

}
