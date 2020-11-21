package org.testgen.runtime.proxy.impl;

import org.testgen.runtime.proxy.AbstractProxy;

public class DoubleProxy extends AbstractProxy {
	private double value;

	public DoubleProxy(double value, Object parent, String fieldName) {
		super(parent, fieldName, double.class);
		this.value = value;
	}

	public DoubleProxy(Object parent, String fieldName) {
		super(parent, fieldName, double.class);
	}

	public double getValue() {
		trackReadFieldCalls();
		return value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public double getUntrackedValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
