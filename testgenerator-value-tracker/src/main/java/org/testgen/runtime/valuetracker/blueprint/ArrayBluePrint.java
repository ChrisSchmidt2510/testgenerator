package org.testgen.runtime.valuetracker.blueprint;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.factories.BluePrintFactory;

public class ArrayBluePrint extends AbstractBasicBluePrint<Object> {
	private final BluePrint[] elements;
	private Class<?> arrayType;
	private int dimensions;

	ArrayBluePrint(String name, Object value, int size) {
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
		return Arrays.stream(elements).filter(bluePrint -> bluePrint != null && bluePrint.isComplexType())
				.collect(Collectors.toList());
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

	@Override
	public void resetBuildState() {
		if (build) {
			build = false;
			Arrays.stream(elements).filter(el -> el != null).forEach(BluePrint::resetBuildState);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
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
		return Objects.equals(name, other.name) && Arrays.equals(elements, other.elements);
	}

	@Override
	public String toString() {
		return value.getClass().getTypeName() + " " + name;
	}

	public static class ArrayBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value != null &&value.getClass().isArray();
		}

		@Override
		public BluePrint createBluePrint(String name, Object value, CurrentlyBuildedBluePrints currentlyBuildedBluePrints,
				BiFunction<String, Object, BluePrint> childCallBack) {
			int length = Array.getLength(value);

			ArrayBluePrint arrayBluePrint = new ArrayBluePrint(name, value, length);

			for (int i = 0; i < length; i++) {
				Object element = Array.get(value, i);

				if (element != null) {

					if (currentlyBuildedBluePrints.isCurrentlyBuilded(value)) {
						int index = i;
						currentlyBuildedBluePrints.addFinishedListener(element, bp -> arrayBluePrint.add(index, bp));

					} else {
						BluePrint bluePrint = childCallBack.apply(name + "Element", element);
						arrayBluePrint.add(i, bluePrint);
					}
				}
			}

			return arrayBluePrint;
		}

	}

}
