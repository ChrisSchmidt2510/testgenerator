package de.nvg.valuetracker.blueprint.simpletypes;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class StringBluePrint extends SimpleBluePrint<String> {

	StringBluePrint(String fieldName, String value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "\"" + value + "\";";
	}

}
