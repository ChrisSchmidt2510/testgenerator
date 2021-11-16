package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.BooleanBluePrint.BooleanBluePrintFactory;

public class BooleanBluePrintTest {

	private BooleanBluePrintFactory factory = new BooleanBluePrintFactory();
	
	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(false));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreation() {
		SimpleBluePrint<Boolean> bluePrint = factory.createBluePrint("boolean", true);
		Assert.assertEquals("true", bluePrint.valueCreation());
		Assert.assertEquals(bluePrint.getReferenceClass(), Boolean.class);
	}

}
