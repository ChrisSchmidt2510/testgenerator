package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint.EnumBluePrintFactory;

public class EnumBluePrintTest {

	public enum Sample {
		TYPE, ANOTHER_TYPE;
	}

	private EnumBluePrintFactory factory = new EnumBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(Sample.ANOTHER_TYPE));
		assertFalse(factory.createBluePrintForType(null));
		assertFalse(factory.createBluePrintForType(5));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		SimpleBluePrint<Enum<?>> bluePrint = factory.createBluePrint("enum", Sample.ANOTHER_TYPE);

		assertEquals("ANOTHER_TYPE", bluePrint.valueCreation());
	}
}
