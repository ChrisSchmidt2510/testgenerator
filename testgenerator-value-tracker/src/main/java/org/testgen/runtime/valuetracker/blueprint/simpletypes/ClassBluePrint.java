package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;

public class ClassBluePrint extends SimpleBluePrint<Class<?>> {

	ClassBluePrint(String fieldname, Class<?> value) {
		super(fieldname, value);
	}

	@Override
	protected String createValue(Class<?> value) {
		return value.getSimpleName();
	}

	@Override
	public Class<?> getReferenceClass() {
		return value;
	}

	public static class ClassBluePrintFactory implements SimpleBluePrintFactory<Class<?>> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Class<?>;
		}

		@Override
		public SimpleBluePrint<Class<?>> createBluePrint(String name, Class<?> value) {
			return new ClassBluePrint(name, value);
		}

	}

}
