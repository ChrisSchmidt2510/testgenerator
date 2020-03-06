package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class NullBluePrint extends SimpleBluePrint<Object> {

	NullBluePrint(String name) {
		super(name, null);
	}

	@Override
	public String valueCreation() {
		return "null";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Collections.emptyList();
	}

}
