package de.nvg.valuetracker.blueprint;

import java.util.List;

public abstract class SimpleBluePrint<E> extends BasicBluePrint<E> {

	protected SimpleBluePrint(String name, E value) {
		super(name, value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return null;
	}

	public abstract String valueCreation();

	@Override
	public String toString() {
		// TODO just temporary
		build = true;

		return "Field: " + name + " Value: " + valueCreation();
	}

}
