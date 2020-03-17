package de.nvg.proxy.impl;

import de.nvg.proxy.AbstractProxy;

public class BooleanProxy extends AbstractProxy {
	private boolean value;

	public BooleanProxy(boolean value, Object parent, String fieldName) {
		super(parent, fieldName, boolean.class);
		this.value = value;
	}

	public BooleanProxy(Object parent, String fieldName) {
		super(parent, fieldName, boolean.class);
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		trackReadFieldCalls();

		return value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public boolean getUntrackedValue() {
		return value;
	}

}
