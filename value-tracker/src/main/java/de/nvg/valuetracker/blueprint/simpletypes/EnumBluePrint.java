package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.regex.Matcher;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class EnumBluePrint extends SimpleBluePrint<Enum<?>> {

	protected EnumBluePrint(String fieldName, Enum<?> value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return value.getClass().getName().replace("$", Matcher.quoteReplacement(".")) + "." + value;

//		Matcher.quoteReplacement("$");
	}

}
