package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

public class NullBluePrint extends SimpleBluePrint<Object> {

	NullBluePrint(String name) {
		super(name, null);
	}

	@Override
	protected String createValue(Object value) {
		return "null";
	}

	public static class NullBluePrintFactory implements SimpleBluePrintFactory<Object> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value == null;
		}

		@Override
		public SimpleBluePrint<Object> createBluePrint(String name, Object value) {
			return new NullBluePrint(name);
		}

	}

}
