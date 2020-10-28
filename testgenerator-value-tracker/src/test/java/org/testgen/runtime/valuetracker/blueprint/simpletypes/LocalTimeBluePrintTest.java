package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class LocalTimeBluePrintTest {

	@Test
	public void testValueCreation() {
		LocalTimeBluePrint localTimeBp = new LocalTimeBluePrint("localTime", LocalTime.of(12, 5));

		Assert.assertEquals("$T.of(12,5,0)", localTimeBp.valueCreation());
		Assert.assertEquals(Arrays.asList(LocalTime.class), localTimeBp.getReferenceClasses());
	}
}
