package de.nvg.valuetracker.blueprint.simpletypes;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class StringBluePrint extends SimpleBluePrint<String> {

	StringBluePrint(String fieldName, String value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(String value) {
		return "\"" + value + "\"";
	}

}
