package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class NullBluePrint extends SimpleBluePrint<Object> {

	NullBluePrint(String name) {
		super(name, null);
	}

	@Override
	protected String createValue(Object value) {
		return "null";
	}

	public static class NullBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value == null;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new NullBluePrint(name);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
