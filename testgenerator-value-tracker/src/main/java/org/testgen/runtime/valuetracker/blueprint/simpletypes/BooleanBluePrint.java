package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;

public class BooleanBluePrint extends SimpleBluePrint<Boolean> {

	BooleanBluePrint(String fieldName, Boolean value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Boolean value) {
		return value.toString();
	}

	public static class BooleanBluePrintFactory implements SimpleBluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Boolean;
		}

		@Override
		public SimpleBluePrint<?> createBluePrint(String name, Object value) {
			return new BooleanBluePrint(name, (Boolean) value);
		}

	}

}
