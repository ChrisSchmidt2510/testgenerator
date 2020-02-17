package de.nvg.valuetracker.blueprint;

import java.util.List;

public interface BluePrint {

	public List<BluePrint> getPreExecuteBluePrints();

	public Object getReference();

	public String getName();

	public boolean isComplexType();

	public boolean isNotBuild();

	public void setBuild();

	public default boolean isCollectionBluePrint() {
		return this instanceof BasicCollectionBluePrint<?>;
	}

	public default BasicCollectionBluePrint<?> castToCollectionBluePrint() {
		if (isCollectionBluePrint()) {
			return (BasicCollectionBluePrint<?>) this;
		}

		return null;
	}

	public default boolean isComplexBluePrint() {
		return this instanceof ComplexBluePrint;
	}

	public default ComplexBluePrint castToComplexBluePrint() {
		if (isComplexBluePrint()) {
			return (ComplexBluePrint) this;
		}

		return null;
	}

	public default boolean isSimpleBluePrint() {
		return this instanceof SimpleBluePrint<?>;
	}

	public default SimpleBluePrint<?> castToSimpleBluePrint() {
		if (isSimpleBluePrint()) {
			return (SimpleBluePrint<?>) this;
		}

		return null;
	}

}
