package org.testgen.runtime.proxy.impl;

import org.testgen.runtime.proxy.AbstractProxy;

public class IntegerProxy extends AbstractProxy {
	private int value;

	public IntegerProxy(int value, Object parent, String fieldName, Class<?> dataType) {
		super(parent, fieldName, dataType);
		this.value = value;
	}

	public IntegerProxy(Object parent, String fieldName, Class<?> dataType) {
		super(parent, fieldName, dataType);
	}

	public int getValue() {
		trackReadFieldCalls();
		return value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public int getUntrackedValue() {
		return value;
	}

	public byte getByteValue() {
		trackReadFieldCalls();
		return (byte) value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public byte getUntrackedByteValue() {
		return (byte) value;
	}

	public short getShortValue() {
		trackReadFieldCalls();
		return (short) value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public short getUntrackedShortValue() {
		return (short) value;
	}

	public char getCharValue() {
		trackReadFieldCalls();
		return (char) value;
	}

	/**
	 * @apiNote only for internal use of the testgenerator-context
	 * @return
	 */
	public char getUntrackedCharValue() {
		return (char) value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
