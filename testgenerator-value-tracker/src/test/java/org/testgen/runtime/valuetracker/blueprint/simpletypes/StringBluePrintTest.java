package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;

public class StringBluePrintTest {

	@Test
	public void testValueCreation() {
		StringBluePrint stringBp = new StringBluePrint("string", "testgenerator");

		Assert.assertEquals("testgenerator", stringBp.valueCreation());
		Assert.assertEquals(String.class, stringBp.getReferenceClass());
	}
}
