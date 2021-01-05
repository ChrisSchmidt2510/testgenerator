package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;

public class StringBluePrint extends SimpleBluePrint<String> {

	StringBluePrint(String fieldName, String value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(String value) {
		return value;
	}

	public static class StringBluePrintFactory implements SimpleBluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof String;
		}

		@Override
		public SimpleBluePrint<?> createBluePrint(String name, Object value) {
			return new StringBluePrint(name, (String) value);
		}

	}

}
