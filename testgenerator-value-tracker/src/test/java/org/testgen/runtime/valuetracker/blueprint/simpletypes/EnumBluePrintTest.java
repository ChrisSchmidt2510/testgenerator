package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.EnumBluePrint.EnumBluePrintFactory;

public class EnumBluePrintTest {

	public enum Sample {
		TYPE, ANOTHER_TYPE;
	}

	private EnumBluePrintFactory factory = new EnumBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(Sample.ANOTHER_TYPE));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		SimpleBluePrint<Enum<?>> bluePrint = factory.createBluePrint("enum", Sample.ANOTHER_TYPE);

		Assert.assertEquals("ANOTHER_TYPE", bluePrint.valueCreation());
	}
}
