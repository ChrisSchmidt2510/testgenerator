package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;

public class LocalDateTimeBluePrintTest {

	@Test
	public void testValueCreation() {
		LocalDateTimeBluePrint bluePrint = new LocalDateTimeBluePrint("localDateTime",
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
