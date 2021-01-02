package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class CharacterBluePrint extends SimpleBluePrint<Character> {

	CharacterBluePrint(String fieldname, Character value) {
		super(fieldname, value);
	}

	@Override
	protected String createValue(Character value) {
		return value.toString();
	}

	public static class CharacterBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Character;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new CharacterBluePrint(name, (Character) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
