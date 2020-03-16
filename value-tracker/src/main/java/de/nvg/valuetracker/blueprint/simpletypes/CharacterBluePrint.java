package de.nvg.valuetracker.blueprint.simpletypes;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class CharacterBluePrint extends SimpleBluePrint<Character> {

	CharacterBluePrint(String fieldname, Character value) {
		super(fieldname, value);
	}

	@Override
	protected String createValue(Character value) {
		return "'" + value.toString() + "'";
	}

}
