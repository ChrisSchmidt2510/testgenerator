package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.awt.List;

import org.junit.Assert;
import org.junit.Test;

public class ClassBluePrintTest {

	@Test
	public void testValueCreation() {
		ClassBluePrint bluePrint = new ClassBluePrint("Class", List.class);
		Assert.assertEquals("List", bluePrint.valueCreation());
		Assert.assertEquals(List.class, bluePrint.getReferenceClass());
	}
}
