package de.nvg.proxy.impl;

import java.util.Objects;

import de.nvg.proxy.AbstractProxy;

public class ReferenceProxy<E> extends AbstractProxy {
	private E value;

	public ReferenceProxy(E value, Object parentObject, String fieldName, Class<?> fieldDataType) {
		super(parentObject, fieldName, fieldDataType);
		this.value = value;
	}

	public ReferenceProxy(Object parentObject, String fieldName, Class<?> fieldDataType) {
		super(parentObject, fieldName, fieldDataType);
	}

	public E getValue() {
		trackReadFieldCalls();
		return value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public E getUntrackedValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ReferenceProxy) {
			return value.equals(((ReferenceProxy<?>) obj).getValue());
		}

		return value.equals(obj);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}
}
