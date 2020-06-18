package de.nvg.valuetracker.blueprint;

import java.util.Arrays;
import java.util.List;

public class ArrayBluePrint extends AbstractBasicBluePrint<Object> {
	private final BluePrint[] elements;

	public ArrayBluePrint(String name, Object value, int size) {
		super(name, value);
		elements = new BluePrint[size];
	}

	public Class<?> getType() {
		return value.getClass();
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

	@Override
	public boolean isComplexType() {
		return true;
	}

	public int size() {
		return elements.length;
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
		if (this == obj)
			return true;
		if (!(obj instanceof ArrayBluePrint))
			return false;
		ArrayBluePrint other = (ArrayBluePrint) obj;
		return Arrays.equals(elements, other.elements);
	}
	
	

}
