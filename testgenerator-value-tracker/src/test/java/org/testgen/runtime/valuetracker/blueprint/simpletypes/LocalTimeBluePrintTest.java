package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint.LocalTimeBluePrintFactory;

public class LocalTimeBluePrintTest {

	private LocalTimeBluePrintFactory factory = new LocalTimeBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(LocalTime.now()));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		LocalTimeBluePrint bluePrint = (LocalTimeBluePrint) factory.createBluePrint("localTime", LocalTime.of(12, 5));

		Assert.assertEquals(12, bluePrint.getHour());
		Assert.assertEquals(5, bluePrint.getMinute());
		Assert.assertEquals(0, bluePrint.getSecond());

		Assert.assertEquals(LocalTime.class, bluePrint.getReferenceClass());
	}
}
