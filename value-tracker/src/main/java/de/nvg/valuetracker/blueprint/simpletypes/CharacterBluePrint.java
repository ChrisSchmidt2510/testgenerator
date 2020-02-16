package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class CharacterBluePrint extends SimpleBluePrint<Character> {

	CharacterBluePrint(String fieldname, Character value) {
		super(fieldname, value);
	}

	@Override
	public String valueCreation() {
		return "'" + value.toString() + "'";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Collections.emptyList();
	}

}
