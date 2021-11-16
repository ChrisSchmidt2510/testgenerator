package org.testgen.runtime.valuetracker.blueprint;

import java.util.List;
import java.util.Objects;

public abstract class SimpleBluePrint<E> extends BasicBluePrint<E> {
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

	@Override
	public boolean isComplexType() {
		return false;
	}

	@Override
	public void resetBuildState() {
		build = false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, createdValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SimpleBluePrint))
			return false;
		SimpleBluePrint<?> other = (SimpleBluePrint<?>) obj;
		return Objects.equals(name, other.name) && Objects.equals(createdValue, other.createdValue);
	}

	@Override
	public String toString() {
		return "Field: " + name + " Value: " + valueCreation();
	}

}
