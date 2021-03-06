package org.testgen.runtime.valuetracker.blueprint;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
	public void resetBuildState() {
		build = false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SimpleBluePrint))
			return false;
		SimpleBluePrint<?> other = (SimpleBluePrint<?>) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "Field: " + name + " Value: " + valueCreation();
	}

}
