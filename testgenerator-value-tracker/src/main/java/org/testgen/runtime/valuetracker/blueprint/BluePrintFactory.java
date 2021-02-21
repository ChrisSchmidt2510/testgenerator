package org.testgen.runtime.valuetracker.blueprint;

import java.util.function.BiFunction;

public interface BluePrintFactory {

	boolean createBluePrintForType(Object value);

	BluePrint createBluePrint(String name, Object value, BiFunction<String, Object, BluePrint> childCallBack);

	default boolean createsSimpleBluePrint() {
		return false;
	}

	default int getPriority() {
		return 1;
	}
}
