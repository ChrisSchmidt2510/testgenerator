package de.nvg.valuetracker.blueprint;

import java.util.Collections;
import java.util.List;

public abstract class SimpleBluePrint<E> extends AbstractBasicBluePrint<E> {
	private final String createdValue;

	protected SimpleBluePrint(String name, E value) {
		super(name, value);
		this.createdValue = createValue(value);
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		return null;
	}

	protected abstract String createValue(E value);

	public String valueCreation() {
		return createdValue;
	}

	public List<Class<?>> getReferenceClasses() {
		return Collections.emptyList();
	}

	@Override
	public boolean isComplexType() {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		return value.equals(value);
	}

	@Override
	public String toString() {
		return "Field: " + name + " Value: " + valueCreation();
	}

}
