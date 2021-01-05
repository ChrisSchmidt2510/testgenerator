package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;

public class CharacterBluePrint extends SimpleBluePrint<Character> {

	CharacterBluePrint(String fieldname, Character value) {
		super(fieldname, value);
	}

	@Override
	protected String createValue(Character value) {
		return value.toString();
	}

	public static class CharacterBluePrintFactory implements SimpleBluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Character;
		}

		@Override
		public SimpleBluePrint<?> createBluePrint(String name, Object value) {
			return new CharacterBluePrint(name, (Character) value);
		}

	}

}
