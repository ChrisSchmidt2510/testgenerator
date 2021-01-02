package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

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

	public static class ClassBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Class<?>;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new ClassBluePrint(name, (Class<?>) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
