package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

public class StringBluePrint extends SimpleBluePrint<String> {

	StringBluePrint(String fieldName, String value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(String value) {
		return value;
	}

	public static class StringBluePrintFactory implements SimpleBluePrintFactory<String> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof String;
		}

		@Override
		public SimpleBluePrint<String> createBluePrint(String name, String value) {
			return new StringBluePrint(name, value);
		}

	}

}
