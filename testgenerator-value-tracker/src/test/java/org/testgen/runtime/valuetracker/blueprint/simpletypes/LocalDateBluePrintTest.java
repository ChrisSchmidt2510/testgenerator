package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;

public class LocalDateBluePrintTest {

	@Test
	public void testValueCreation() {
		LocalDateBluePrint localDateBp = new LocalDateBluePrint("localDate", LocalDate.of(2020, Month.OCTOBER, 25));

		Assert.assertEquals(2020, localDateBp.getYear());
		Assert.assertEquals(10, localDateBp.getMonth());
		Assert.assertEquals(25, localDateBp.getDay());

		Assert.assertEquals(LocalDate.class, localDateBp.getReferenceClass());
	}

}
