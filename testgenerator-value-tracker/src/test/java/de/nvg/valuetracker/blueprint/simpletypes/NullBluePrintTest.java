package de.nvg.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;

public class NullBluePrintTest {

	@Test
	public void testValueCreation() {
		NullBluePrint nullBp = new NullBluePrint("null");

		Assert.assertEquals("null", nullBp.valueCreation());
		Assert.assertTrue(nullBp.getReferenceClasses().isEmpty());
	}

}
