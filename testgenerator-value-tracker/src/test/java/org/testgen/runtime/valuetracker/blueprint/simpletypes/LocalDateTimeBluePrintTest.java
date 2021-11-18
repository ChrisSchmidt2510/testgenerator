package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint.LocalDateTimeBluePrintFactory;

public class LocalDateTimeBluePrintTest {

	private LocalDateTimeBluePrintFactory factory = new LocalDateTimeBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(LocalDateTime.now()));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {

		LocalDateTimeBluePrint bluePrint = (LocalDateTimeBluePrint) factory.createBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(2020, Month.OCTOBER, 25), LocalTime.of(12, 55, 3)));

		assertEquals(2020, bluePrint.getYear());
		assertEquals(10, bluePrint.getMonth());
		assertEquals(25, bluePrint.getDay());

		assertEquals(12, bluePrint.getHour());
		assertEquals(55, bluePrint.getMinute());
		assertEquals(3, bluePrint.getSecond());

		assertEquals(LocalDateTime.class, bluePrint.getReferenceClass());
	}
}
