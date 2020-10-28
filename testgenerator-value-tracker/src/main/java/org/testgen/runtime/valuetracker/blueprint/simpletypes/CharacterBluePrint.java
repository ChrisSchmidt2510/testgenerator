package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class CharacterBluePrint extends SimpleBluePrint<Character> {

	CharacterBluePrint(String fieldname, Character value) {
		super(fieldname, value);
	}

	@Override
	protected String createValue(Character value) {
		return "'" + value.toString() + "'";
	}

}
