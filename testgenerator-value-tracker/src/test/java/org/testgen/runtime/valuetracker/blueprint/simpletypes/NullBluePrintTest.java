package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;

public class NullBluePrintTest {
	
	private NullBluePrintFactory factory = new NullBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(null));
		assertFalse(factory.createBluePrintForType(5));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		SimpleBluePrint<Object> bluePrint = factory.createBluePrint("value", null);

		assertEquals("null", bluePrint.valueCreation());
		assertTrue(bluePrint.getReferenceClass() == null);
	}

}
