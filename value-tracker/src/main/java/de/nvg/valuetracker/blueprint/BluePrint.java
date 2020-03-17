package de.nvg.valuetracker.blueprint;

import java.util.List;

public interface BluePrint {

	List<BluePrint> getPreExecuteBluePrints();

	Object getReference();

	String getName();

	boolean isComplexType();

	boolean isNotBuild();

	void setBuild();

	default boolean isCollectionBluePrint() {
		return this instanceof AbstractBasicCollectionBluePrint<?>;
	}

	default AbstractBasicCollectionBluePrint<?> castToCollectionBluePrint() {
		if (isCollectionBluePrint()) {
			return (AbstractBasicCollectionBluePrint<?>) this;
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
