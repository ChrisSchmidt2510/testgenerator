package org.testgen.runtime.proxy.impl;

import org.testgen.runtime.proxy.AbstractProxy;

public class FloatProxy extends AbstractProxy {
	private float value;

	public FloatProxy(float value, Object parent, String fieldName) {
		super(parent, fieldName, float.class);
		this.value = value;
	}

	public FloatProxy(Object parent, String fieldName) {
		super(parent, fieldName, float.class);
	}

	public float getValue() {
		trackReadFieldCalls();
		return value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public float getUntrackedValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
