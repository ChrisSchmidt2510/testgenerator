package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class NullBluePrint extends SimpleBluePrint<Object> {

	NullBluePrint(String name) {
		super(name, null);
	}

	@Override
	protected String createValue(Object value) {
		return "null";
	}

}
