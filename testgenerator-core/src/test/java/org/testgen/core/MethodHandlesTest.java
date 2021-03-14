package org.testgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MethodHandlesTest {
	private String setFieldValue = "fieldName";
	private String getFieldValue = "field";

	@Test
	public void testSetFieldValue() {
		MethodHandles.setFieldValue(this, "setFieldValue", "newName");
		assertEquals("newName", setFieldValue);
	}

	@Test
	public void testGetFieldValue() {
		Object fieldValue = MethodHandles.getFieldValue(this, "getFieldValue");
		assertEquals(fieldValue, getFieldValue);
	}
}
