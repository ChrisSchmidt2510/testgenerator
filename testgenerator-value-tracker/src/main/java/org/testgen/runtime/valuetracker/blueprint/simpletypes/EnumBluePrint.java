package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Arrays;
import java.util.List;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class EnumBluePrint extends SimpleBluePrint<Enum<?>> {

	protected EnumBluePrint(String fieldName, Enum<?> value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Enum<?> value) {
		return "$T." + value.name();
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(value.getClass());
	}

}
