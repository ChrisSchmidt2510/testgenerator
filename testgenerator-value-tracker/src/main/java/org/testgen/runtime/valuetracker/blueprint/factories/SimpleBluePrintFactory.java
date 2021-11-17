package org.testgen.runtime.valuetracker.blueprint.factories;

import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public interface SimpleBluePrintFactory<T> extends BluePrintFactory {

	SimpleBluePrint<T> createBluePrint(String name, T value);

	@SuppressWarnings("unchecked")
	@Override
	default BluePrint createBluePrint(String name, Object value, CurrentlyBuildedBluePrints registration,
			BiFunction<String, Object, BluePrint> childCallBack) {
		return createBluePrint(name, (T) value);
	}

	@Override
	default boolean createsSimpleBluePrint() {
		return true;
	}
}
