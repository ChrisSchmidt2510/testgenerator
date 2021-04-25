package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;

public class EnumBluePrint extends SimpleBluePrint<Enum<?>> {

	protected EnumBluePrint(String fieldName, Enum<?> value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Enum<?> value) {
		return value.name();
	}

	public static class EnumBluePrintFactory implements SimpleBluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Enum<?>;
		}

		@Override
		public SimpleBluePrint<?> createBluePrint(String name, Object value) {
			return new EnumBluePrint(name, (Enum<?>) value);
		}

	}

}
