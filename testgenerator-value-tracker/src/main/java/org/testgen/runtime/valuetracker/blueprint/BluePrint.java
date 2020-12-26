package org.testgen.runtime.valuetracker.blueprint;

import java.util.List;

public interface BluePrint {

	List<BluePrint> getPreExecuteBluePrints();

	Object getReference();

	String getClassNameOfReference();

	String getName();

	boolean isComplexType();

	boolean isNotBuild();

	void setBuild();

	void resetBuildState();

	default boolean isCollectionBluePrint() {
		return this instanceof AbstractBasicCollectionBluePrint<?>;
	}

	default boolean isContainerBluePrint() {
		return this instanceof AbstractBasicCollectionBluePrint<?> || this instanceof ArrayBluePrint;
	}

	default AbstractBasicCollectionBluePrint<?> castToCollectionBluePrint() {
		if (isCollectionBluePrint()) {
			return (AbstractBasicCollectionBluePrint<?>) this;
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
