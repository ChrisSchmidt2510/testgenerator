package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class StringBluePrint extends SimpleBluePrint<String> {

	StringBluePrint(String fieldName, String value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "\"" + value + "\"";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Collections.emptyList();
	}

}
