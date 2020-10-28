package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import org.junit.Assert;
import org.junit.Test;

public class EnumBluePrintTest {

	public enum Sample {
		TYPE, ANOTHER_TYPE;
	}

	@Test
	public void testValueCreation() {
		EnumBluePrint enumBp = new EnumBluePrint("enum", Sample.ANOTHER_TYPE);

		Assert.assertEquals("$T.ANOTHER_TYPE", enumBp.valueCreation());
	}
}
