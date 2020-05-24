package org.testgen.core;

import java.util.Objects;

public class Wrapper<T> {
	private T value;

	public Wrapper(T value) {
		this.value = value;
	}

	public Wrapper() {
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Objects.toString(value.toString());
	}

}
