package de.nvg.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class LocalDateBluePrintTest {

	@Test
	public void testValueCreation() {
		LocalDateBluePrint localDateBp = new LocalDateBluePrint("localDate", LocalDate.of(2020, Month.OCTOBER, 25));

		Assert.assertEquals("$T.of(2020, $T.OCTOBER, 25)", localDateBp.valueCreation());
		Assert.assertEquals(Arrays.asList(LocalDate.class, Month.class), localDateBp.getReferenceClasses());
	}

}
