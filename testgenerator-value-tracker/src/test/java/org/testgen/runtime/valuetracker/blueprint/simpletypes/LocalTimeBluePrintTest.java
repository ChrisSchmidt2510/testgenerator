package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;

public class LocalTimeBluePrintTest {

	@Test
	public void testValueCreation() {
		LocalTimeBluePrint bluePrint = new LocalTimeBluePrint("localTime", LocalTime.of(12, 5));

		Assert.assertEquals(12, bluePrint.getHour());
		Assert.assertEquals(5, bluePrint.getMinute());
		Assert.assertEquals(0, bluePrint.getSecond());

		Assert.assertEquals(LocalTime.class, bluePrint.getReferenceClass());
	}
}
