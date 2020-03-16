package de.nvg.valuetracker.blueprint.simpletypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

public class NumberBluePrintTest {

	@Test
	public void testValueCreationInteger() {
		NumberBluePrint integerBp = new NumberBluePrint("integer", 3);

		assertEquals("3", integerBp.valueCreation());
		assertTrue(integerBp.getReferenceClasses().isEmpty());
	}

	@Test
	public void testValueCreationByte() {
		NumberBluePrint byteBp = new NumberBluePrint("byte", (byte) 126);

		assertEquals("(byte)126", byteBp.valueCreation());
		assertTrue(byteBp.getReferenceClasses().isEmpty());
	}

	@Test
	public void testValueCreationShort() {
		NumberBluePrint shortBp = new NumberBluePrint("short", (short) 512);

		assertEquals("(short)512", shortBp.valueCreation());
		assertTrue(shortBp.getReferenceClasses().isEmpty());
	}

	@Test
	public void testValueCreationFloat() {
		NumberBluePrint floatBp = new NumberBluePrint("float", 5.12f);

		assertEquals("5.12f", floatBp.valueCreation());
		assertTrue(floatBp.getReferenceClasses().isEmpty());
	}

	@Test
	public void testValueCreationDouble() {
		NumberBluePrint doubleBp = new NumberBluePrint("double", 5.1278);

		assertEquals("5.1278", doubleBp.valueCreation());
		assertTrue(doubleBp.getReferenceClasses().isEmpty());
	}

	@Test
	public void testValueCreationLong() {
		NumberBluePrint longBp = new NumberBluePrint("long", 1_000_000L);

		assertEquals("1000000L", longBp.valueCreation());
		assertTrue(longBp.getReferenceClasses().isEmpty());
	}

	@Test
	public void testValueCreationBigDecimal() {
		NumberBluePrint bigDecimalBp = new NumberBluePrint("BigDecimal", new BigDecimal("10.005"));

		assertEquals("$T.valueOf(10.005).setScale(3)", bigDecimalBp.valueCreation());
		assertEquals(Arrays.asList(BigDecimal.class), bigDecimalBp.getReferenceClasses());
	}

}
