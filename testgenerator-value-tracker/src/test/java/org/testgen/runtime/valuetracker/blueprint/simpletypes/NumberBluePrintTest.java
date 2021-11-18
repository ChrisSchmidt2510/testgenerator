package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class NumberBluePrintTest {

	private NumberBluePrintFactory factory = new NumberBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(5));
		assertTrue(factory.createBluePrintForType((byte)12));
		assertTrue(factory.createBluePrintForType((short) 187));
		assertTrue(factory.createBluePrintForType(17.6f));
		assertTrue(factory.createBluePrintForType(3.1467));
		assertTrue(factory.createBluePrintForType(100L));
		assertTrue(factory.createBluePrintForType(BigDecimal.ONE));
		assertFalse(factory.createBluePrintForType(LocalDate.now()));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreationInteger() {
		SimpleBluePrint<Number> integerBp = factory.createBluePrint("integer", 3);

		assertEquals("3", integerBp.valueCreation());
		assertEquals(Integer.class, integerBp.getReferenceClass());
	}

	@Test
	public void testValueCreationByte() {
		SimpleBluePrint<Number> byteBp =factory.createBluePrint("byte", (byte) 126);

		assertEquals("126", byteBp.valueCreation());
		assertEquals(Byte.class, byteBp.getReferenceClass());
	}

	@Test
	public void testValueCreationShort() {
		SimpleBluePrint<Number> shortBp = factory.createBluePrint("short", (short) 512);

		assertEquals("512", shortBp.valueCreation());
		assertEquals(Short.class, shortBp.getReferenceClass());
	}

	@Test
	public void testValueCreationFloat() {
		SimpleBluePrint<Number> floatBp = factory.createBluePrint("float", 5.12f);

		assertEquals("5.12f", floatBp.valueCreation());
		assertEquals(Float.class, floatBp.getReferenceClass());
	}

	@Test
	public void testValueCreationDouble() {
		SimpleBluePrint<Number> doubleBp = factory.createBluePrint("double", 5.1278);

		assertEquals("5.1278", doubleBp.valueCreation());
		assertEquals(Double.class, doubleBp.getReferenceClass());
	}

	@Test
	public void testValueCreationLong() {
		SimpleBluePrint<Number> longBp = factory.createBluePrint("long", 1_000_000L);

		assertEquals("1000000", longBp.valueCreation());
		assertEquals(Long.class, longBp.getReferenceClass());
	}

	@Test
	public void testValueCreationBigDecimal() {
		NumberBluePrint bigDecimalBp = (NumberBluePrint) factory.createBluePrint("BigDecimal", new BigDecimal("10.005"));

		assertEquals("10.005", bigDecimalBp.valueCreation());
		assertEquals(3, bigDecimalBp.getBigDecimalScale());
		assertEquals(BigDecimal.class, bigDecimalBp.getReferenceClass());
	}

}
