package de.nvg.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;

public class BooleanBluePrintTest {

	@Test
	public void testValueCreation() {
		BooleanBluePrint booleanBp = new BooleanBluePrint("boolean", true);
		Assert.assertEquals("true", booleanBp.valueCreation());
		Assert.assertTrue(booleanBp.getReferenceClasses().isEmpty());
	}

}
