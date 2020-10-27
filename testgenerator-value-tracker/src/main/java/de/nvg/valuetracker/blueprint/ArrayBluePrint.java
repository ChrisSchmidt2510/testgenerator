package de.nvg.valuetracker.blueprint;

import java.util.Arrays;
import java.util.List;

public class ArrayBluePrint extends AbstractBasicBluePrint<Object> {
	private final BluePrint[] elements;
	private Class<?> arrayType;
	private int dimensions;

	public ArrayBluePrint(String name, Object value, int size) {
		super(name, value);

		elements = new BluePrint[size];
		init(value);
	}

	private void init(Object array) {
		int dimenisons = 0;
		Class<?> arrayType = array.getClass();

		while (arrayType.isArray()) {
			dimenisons++;
			arrayType = arrayType.getComponentType();
		}

		this.dimensions = dimenisons;
		this.arrayType = arrayType;
	}

	public Class<?> getType() {
		return value.getClass();
	}

	public Class<?> getBaseType() {
		return arrayType;
	}

	public int getDimensions() {
		return dimensions;
	}

	@Override
	public List<BluePrint> getPreExecuteBluePrints() {
		throw new UnsupportedOperationException();
	}

	public void add(int index, BluePrint element) {
		elements[index] = element;
	}

	public BluePrint[] getElements() {
		return elements;
	}

	public int size() {
		return elements.length;
	}

	@Override
	public boolean isComplexType() {
		return true;
	}

	public void resetBuildState() {
		if (build) {
			build = false;
			Arrays.stream(elements).forEach(BluePrint::resetBuildState);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ArrayBluePrint)) {
			return false;
		}
		ArrayBluePrint other = (ArrayBluePrint) obj;
		return Arrays.equals(elements, other.elements);
	}

	@Override
	public String toString() {
		return value.getClass().getTypeName() + " " + name;
	}

}
