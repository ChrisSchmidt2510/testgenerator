package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.BooleanBluePrint.BooleanBluePrintFactory;

public class BooleanBluePrintTest {

	private BooleanBluePrintFactory factory = new BooleanBluePrintFactory();
	
	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(false));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreation() {
		SimpleBluePrint<Boolean> bluePrint = factory.createBluePrint("boolean", true);
		assertEquals("true", bluePrint.valueCreation());
		assertEquals(bluePrint.getReferenceClass(), Boolean.class);
	}

}
