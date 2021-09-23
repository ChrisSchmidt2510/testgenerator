package org.testgen.runtime.valuetracker.blueprint;

import java.util.List;

import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ComplexBluePrint;

public interface BluePrint {

	List<BluePrint> getPreExecuteBluePrints();

	Object getReference();

	Class<?> getReferenceClass();

	String getSimpleClassName();

	String getClassNameOfReference();

	String getName();

	boolean isComplexType();

	boolean isBuild();

	boolean isNotBuild();

	void setBuild();

	void resetBuildState();

	default boolean isCollectionBluePrint() {
		return this instanceof BasicCollectionBluePrint<?>;
	}

	default BasicCollectionBluePrint<?> castToCollectionBluePrint() {
		if (isCollectionBluePrint()) {
			return (BasicCollectionBluePrint<?>) this;
		}

		return null;
	}

	default boolean isArrayBluePrint() {
		return this instanceof ArrayBluePrint;
	}

	default ArrayBluePrint castToArrayBluePrint() {
		if (isArrayBluePrint()) {
			return (ArrayBluePrint) this;
		}

		return null;
	}

	default boolean isComplexBluePrint() {
		return this instanceof ComplexBluePrint;
	}

	default ComplexBluePrint castToComplexBluePrint() {
		if (isComplexBluePrint()) {
			return (ComplexBluePrint) this;
		}

		return null;
	}

	default boolean isSimpleBluePrint() {
		return this instanceof SimpleBluePrint<?>;
	}

	default SimpleBluePrint<?> castToSimpleBluePrint() {
		if (isSimpleBluePrint()) {
			return (SimpleBluePrint<?>) this;
		}

		return null;
	}

}
