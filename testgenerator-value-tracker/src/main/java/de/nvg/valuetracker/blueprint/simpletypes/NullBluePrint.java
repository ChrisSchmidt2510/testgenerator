package de.nvg.valuetracker.blueprint.simpletypes;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class NullBluePrint extends SimpleBluePrint<Object> {

	NullBluePrint(String name) {
		super(name, null);
	}

	@Override
	protected String createValue(Object value) {
		return "null";
	}

}
