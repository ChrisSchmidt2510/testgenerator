package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class NullBluePrint extends SimpleBluePrint<Object> {

	protected NullBluePrint(String name) {
		super(name, null);
	}

	public String valueCreation() {
		return "null";
	}

	public List<Class<?>> getReferenceClasses() {
		return Collections.emptyList();
	}

}
