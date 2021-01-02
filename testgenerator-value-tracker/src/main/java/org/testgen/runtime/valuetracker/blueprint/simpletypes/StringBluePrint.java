package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class StringBluePrint extends SimpleBluePrint<String> {

	StringBluePrint(String fieldName, String value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(String value) {
		return value;
	}

	public static class StringBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof String;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new StringBluePrint(name, (String) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
