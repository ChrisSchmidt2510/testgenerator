package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Arrays;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class EnumBluePrint extends SimpleBluePrint<Enum<?>> {

	protected EnumBluePrint(String fieldName, Enum<?> value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Enum<?> value) {
		return "$T." + value;
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(value.getClass());
	}

}
