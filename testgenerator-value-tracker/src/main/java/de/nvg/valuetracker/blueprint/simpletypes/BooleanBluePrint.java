package de.nvg.valuetracker.blueprint.simpletypes;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class BooleanBluePrint extends SimpleBluePrint<Boolean> {

	BooleanBluePrint(String fieldName, Boolean value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Boolean value) {
		return value.toString();
	}

}
