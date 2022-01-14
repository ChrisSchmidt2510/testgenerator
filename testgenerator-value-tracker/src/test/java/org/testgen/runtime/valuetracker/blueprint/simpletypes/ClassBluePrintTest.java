package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.List;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.ClassBluePrint.ClassBluePrintFactory;

public class ClassBluePrintTest {
	
private ClassBluePrintFactory factory = new ClassBluePrintFactory();
	
	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(Integer.class));
		assertFalse(factory.createBluePrintForType(LocalDate.now()));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() {
		SimpleBluePrint<Class<?>> bluePrint = factory.createBluePrint("Class", List.class);
		assertEquals("List", bluePrint.valueCreation());
		assertEquals(List.class, bluePrint.getReferenceClass());
	}
}
