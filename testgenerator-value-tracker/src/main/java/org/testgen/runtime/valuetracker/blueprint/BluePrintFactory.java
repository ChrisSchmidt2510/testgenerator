package org.testgen.runtime.valuetracker.blueprint;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.testgen.runtime.valuetracker.ObjectValueTracker.BluePrintUnderProcessRegistration;

public interface BluePrintFactory {

	boolean createBluePrintForType(Object value);

	BluePrint createBluePrint(String name, Object value, Predicate<Object> currentlyBuildedFilter,
			BluePrintUnderProcessRegistration registration, BiFunction<String, Object, BluePrint> childCallBack);

	default boolean createsSimpleBluePrint() {
		return false;
	}

	default int getPriority() {
		return 1;
	}
}
