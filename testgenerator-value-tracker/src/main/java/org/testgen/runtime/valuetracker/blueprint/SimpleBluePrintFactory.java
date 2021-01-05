package org.testgen.runtime.valuetracker.blueprint;

import java.util.function.BiFunction;

public interface SimpleBluePrintFactory extends BluePrintFactory {

	SimpleBluePrint<?> createBluePrint(String name, Object value);

	@Override
	default BluePrint createBluePrint(String name, Object value, BiFunction<String, Object, BluePrint> childCallBack) {
		return createBluePrint(name, value);
	}

	@Override
	default boolean createsSimpleBluePrint() {
		return true;
	}
}
