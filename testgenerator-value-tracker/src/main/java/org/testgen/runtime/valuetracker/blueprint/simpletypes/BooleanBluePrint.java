package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class BooleanBluePrint extends SimpleBluePrint<Boolean> {

	BooleanBluePrint(String fieldName, Boolean value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Boolean value) {
		return value.toString();
	}

	public static class BooleanBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Boolean;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new BooleanBluePrint(name, (Boolean) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
