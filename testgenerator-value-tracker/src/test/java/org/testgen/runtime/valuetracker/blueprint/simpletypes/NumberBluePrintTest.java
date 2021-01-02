package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class NumberBluePrintTest {

	@Test
	public void testValueCreationInteger() {
		NumberBluePrint integerBp = new NumberBluePrint("integer", 3);

		assertEquals("3", integerBp.valueCreation());
		assertEquals(Integer.class, integerBp.getReferenceClass());
	}

	@Test
	public void testValueCreationByte() {
		NumberBluePrint byteBp = new NumberBluePrint("byte", (byte) 126);

		assertEquals("126", byteBp.valueCreation());
		assertEquals(Byte.class, byteBp.getReferenceClass());
	}

	@Test
	public void testValueCreationShort() {
		NumberBluePrint shortBp = new NumberBluePrint("short", (short) 512);

		assertEquals("512", shortBp.valueCreation());
		assertEquals(Short.class, shortBp.getReferenceClass());
	}

	@Test
	public void testValueCreationFloat() {
		NumberBluePrint floatBp = new NumberBluePrint("float", 5.12f);

		assertEquals("5.12f", floatBp.valueCreation());
		assertEquals(Float.class, floatBp.getReferenceClass());
	}

	@Test
	public void testValueCreationDouble() {
		NumberBluePrint doubleBp = new NumberBluePrint("double", 5.1278);

		assertEquals("5.1278", doubleBp.valueCreation());
		assertEquals(Double.class, doubleBp.getReferenceClass());
	}

	@Test
	public void testValueCreationLong() {
		NumberBluePrint longBp = new NumberBluePrint("long", 1_000_000L);

		assertEquals("1000000", longBp.valueCreation());
		assertEquals(Long.class, longBp.getReferenceClass());
	}

	@Test
	public void testValueCreationBigDecimal() {
		NumberBluePrint bigDecimalBp = new NumberBluePrint("BigDecimal", new BigDecimal("10.005"));

		assertEquals("10.005", bigDecimalBp.valueCreation());
		assertEquals(3, bigDecimalBp.getBigDecimalScale());
		assertEquals(BigDecimal.class, bigDecimalBp.getReferenceClass());
	}

}
