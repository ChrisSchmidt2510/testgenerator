package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class EnumBluePrint extends SimpleBluePrint<Enum<?>> {

	protected EnumBluePrint(String fieldName, Enum<?> value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Enum<?> value) {
		return value.name();
	}

	public static class EnumBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Enum<?>;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new EnumBluePrint(name, (Enum<?>) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
