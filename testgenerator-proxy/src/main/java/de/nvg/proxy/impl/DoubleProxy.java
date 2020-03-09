package de.nvg.proxy.impl;

import de.nvg.proxy.Proxy;

public class DoubleProxy extends Proxy {
	private double value;

	public DoubleProxy(double value, Object parent, String fieldName) {
		super(parent, fieldName, "double");
		this.value = value;
	}

	public DoubleProxy(Object parent, String fieldName) {
		super(parent, fieldName, "double");
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
}
