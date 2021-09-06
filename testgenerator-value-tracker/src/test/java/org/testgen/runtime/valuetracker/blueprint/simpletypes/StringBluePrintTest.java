package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

public class StringBluePrintTest {

	private StringBluePrintFactory factory = new StringBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType("Test"));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreation() {
		SimpleBluePrint<String> bluePrint = factory.createBluePrint("string", "testgenerator");

		Assert.assertEquals("testgenerator", bluePrint.valueCreation());
		Assert.assertEquals(String.class, bluePrint.getReferenceClass());
	}
}
