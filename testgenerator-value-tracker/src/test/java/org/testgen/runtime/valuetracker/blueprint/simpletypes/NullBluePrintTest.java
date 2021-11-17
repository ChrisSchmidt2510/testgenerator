package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;

public class NullBluePrintTest {
	
	private NullBluePrintFactory factory = new NullBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		SimpleBluePrint<Object> bluePrint = factory.createBluePrint("value", null);

		Assert.assertEquals("null", bluePrint.valueCreation());
		Assert.assertTrue(bluePrint.getReferenceClass() == null);
	}

}
