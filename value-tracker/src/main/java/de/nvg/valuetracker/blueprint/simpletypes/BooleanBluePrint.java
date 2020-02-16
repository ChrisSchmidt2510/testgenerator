package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class BooleanBluePrint extends SimpleBluePrint<Boolean> {

	BooleanBluePrint(String fieldName, Boolean value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return value.toString();
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Collections.emptyList();
	}

}
