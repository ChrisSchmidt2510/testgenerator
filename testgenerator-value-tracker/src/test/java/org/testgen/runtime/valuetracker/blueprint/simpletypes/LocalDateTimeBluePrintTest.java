package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class LocalDateTimeBluePrintTest {

	@Test
	public void testValueCreation() {
		LocalDateTimeBluePrint localDateTimeBp = new LocalDateTimeBluePrint("localDateTime",
				LocalDateTime.of(LocalDate.of(2020, Month.OCTOBER, 25), LocalTime.of(12, 55, 3)));

		Assert.assertEquals("$T.of($T.of(2020, $T.OCTOBER, 25), $T.of(12, 55, 3))", localDateTimeBp.valueCreation());
		Assert.assertEquals(Arrays.asList(LocalDateTime.class, LocalDate.class, Month.class, LocalTime.class),
				localDateTimeBp.getReferenceClasses());
	}
}
