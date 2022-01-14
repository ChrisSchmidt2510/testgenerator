package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint.LocalTimeBluePrintFactory;

public class LocalTimeBluePrintTest {

	private LocalTimeBluePrintFactory factory = new LocalTimeBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(LocalTime.now()));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		LocalTimeBluePrint bluePrint = (LocalTimeBluePrint) factory.createBluePrint("localTime", LocalTime.of(12, 5));

		assertEquals(12, bluePrint.getHour());
		assertEquals(5, bluePrint.getMinute());
		assertEquals(0, bluePrint.getSecond());

		assertEquals(LocalTime.class, bluePrint.getReferenceClass());
	}
}
