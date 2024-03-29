package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

public class CharacterBluePrint extends SimpleBluePrint<Character> {

	CharacterBluePrint(String fieldname, Character value) {
		super(fieldname, value);
	}

	@Override
	protected String createValue(Character value) {
		return value.toString();
	}

	public static class CharacterBluePrintFactory implements SimpleBluePrintFactory<Character> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Character;
		}

		@Override
		public SimpleBluePrint<Character> createBluePrint(String name, Character value) {
			return new CharacterBluePrint(name, value);
		}

	}

}
