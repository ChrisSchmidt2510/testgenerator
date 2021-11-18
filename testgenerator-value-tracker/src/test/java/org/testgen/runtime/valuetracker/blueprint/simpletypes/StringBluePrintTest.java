package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

public class StringBluePrintTest {

	private StringBluePrintFactory factory = new StringBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType("Test"));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreation() {
		SimpleBluePrint<String> bluePrint = factory.createBluePrint("string", "testgenerator");

		assertEquals("testgenerator", bluePrint.valueCreation());
		assertEquals(String.class, bluePrint.getReferenceClass());
	}
}
