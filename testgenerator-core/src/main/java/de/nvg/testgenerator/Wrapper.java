package de.nvg.testgenerator;

public class Wrapper<T> {
	private T value;

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

}
