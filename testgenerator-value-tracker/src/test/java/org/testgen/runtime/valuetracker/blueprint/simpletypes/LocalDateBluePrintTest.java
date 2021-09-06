package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateBluePrint.LocalDateBluePrintFactory;

public class LocalDateBluePrintTest {

	private LocalDateBluePrintFactory factory = new LocalDateBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(LocalDate.now()));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {

		LocalDateBluePrint localDateBp = (LocalDateBluePrint) factory.createBluePrint("localDate",
				LocalDate.of(2020, Month.OCTOBER, 25));

		Assert.assertEquals(2020, localDateBp.getYear());
		Assert.assertEquals(10, localDateBp.getMonth());
		Assert.assertEquals(25, localDateBp.getDay());

		Assert.assertEquals(LocalDate.class, localDateBp.getReferenceClass());
	}

}
