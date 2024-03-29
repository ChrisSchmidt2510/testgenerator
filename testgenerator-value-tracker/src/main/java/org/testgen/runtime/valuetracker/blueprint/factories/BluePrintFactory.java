package org.testgen.runtime.valuetracker.blueprint.factories;

import java.util.function.BiFunction;

import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;

public interface BluePrintFactory {

	boolean createBluePrintForType(Object value);

	BluePrint createBluePrint(String name, Object value, CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue,
			BiFunction<String, Object, BluePrint> childCallBack);

	default boolean createsSimpleBluePrint() {
		return false;
	}

	default int getPriority() {
		return 1;
	}
}
