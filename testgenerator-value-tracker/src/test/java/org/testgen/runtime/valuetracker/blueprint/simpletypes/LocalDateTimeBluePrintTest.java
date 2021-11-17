package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint.LocalDateTimeBluePrintFactory;

public class LocalDateTimeBluePrintTest {

	private LocalDateTimeBluePrintFactory factory = new LocalDateTimeBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(LocalDateTime.now()));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {

		LocalDateTimeBluePrint bluePrint = (LocalDateTimeBluePrint) factory.createBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(2020, Month.OCTOBER, 25), LocalTime.of(12, 55, 3)));

		Assert.assertEquals(2020, bluePrint.getYear());
		Assert.assertEquals(10, bluePrint.getMonth());
		Assert.assertEquals(25, bluePrint.getDay());

		Assert.assertEquals(12, bluePrint.getHour());
		Assert.assertEquals(55, bluePrint.getMinute());
		Assert.assertEquals(3, bluePrint.getSecond());

		Assert.assertEquals(LocalDateTime.class, bluePrint.getReferenceClass());
	}
}
