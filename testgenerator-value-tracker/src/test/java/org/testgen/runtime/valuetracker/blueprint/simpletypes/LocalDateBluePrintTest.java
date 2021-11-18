package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateBluePrint.LocalDateBluePrintFactory;

public class LocalDateBluePrintTest {

	private LocalDateBluePrintFactory factory = new LocalDateBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(LocalDate.now()));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {

		LocalDateBluePrint localDateBp = (LocalDateBluePrint) factory.createBluePrint("localDate",
				LocalDate.of(2020, Month.OCTOBER, 25));

		assertEquals(2020, localDateBp.getYear());
		assertEquals(10, localDateBp.getMonth());
		assertEquals(25, localDateBp.getDay());

		assertEquals(LocalDate.class, localDateBp.getReferenceClass());
	}

}
