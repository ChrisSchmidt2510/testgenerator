package org.testgen.runtime.valuetracker.blueprint;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.testgen.runtime.valuetracker.BluePrintUnderProcessRegistration;

public interface SimpleBluePrintFactory<T> extends BluePrintFactory {

	SimpleBluePrint<T> createBluePrint(String name, T value);
	
	@SuppressWarnings("unchecked")
	@Override
	default BluePrint createBluePrint(String name, Object value, Predicate<Object> currentlyBuildedFilter,
			BluePrintUnderProcessRegistration registration, BiFunction<String, Object, BluePrint> childCallBack) {
		return createBluePrint(name, (T) value);
	}

	@Override
	default boolean createsSimpleBluePrint() {
		return true;
	}
}
