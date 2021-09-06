package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.awt.List;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.ClassBluePrint.ClassBluePrintFactory;

public class ClassBluePrintTest {
	
private ClassBluePrintFactory factory = new ClassBluePrintFactory();
	
	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(Integer.class));
		Assert.assertFalse(factory.createBluePrintForType(LocalDate.now()));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		SimpleBluePrint<Class<?>> bluePrint = factory.createBluePrint("Class", List.class);
		Assert.assertEquals("List", bluePrint.valueCreation());
		Assert.assertEquals(List.class, bluePrint.getReferenceClass());
	}
}
