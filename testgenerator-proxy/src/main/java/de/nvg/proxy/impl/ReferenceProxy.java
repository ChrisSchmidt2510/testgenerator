package de.nvg.proxy.impl;

import de.nvg.proxy.Proxy;

public class ReferenceProxy<E> extends Proxy {
	private E value;

	public ReferenceProxy(E value, Object parentObject, String fieldName) {
		super(parentObject, fieldName, value.getClass().getName());
		this.value = value;
	}

	public ReferenceProxy(Object parentObject, String fieldName, String fieldDataType) {
		super(parentObject, fieldName, fieldDataType);
	}

	public E getValue() {
		trackReadFieldCalls();
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj != null && obj instanceof ReferenceProxy) {
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
		return value.toString();
	}
}
