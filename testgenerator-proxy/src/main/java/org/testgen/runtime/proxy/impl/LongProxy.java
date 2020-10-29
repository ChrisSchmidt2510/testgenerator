package org.testgen.runtime.proxy.impl;

import org.testgen.runtime.proxy.AbstractProxy;

public class LongProxy extends AbstractProxy {
	private long value;

	public LongProxy(long value, Object parent, String fieldName) {
		super(parent, fieldName, long.class);
		this.value = value;
	}

	public LongProxy(Object parent, String fieldName) {
		super(parent, fieldName, long.class);
	}

	public long getValue() {
		trackReadFieldCalls();
		return value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public long getUntrackedValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
