package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint.JavaDateBluePrintFactory;

public class JavaDateBluePrintTest {
	
private JavaDateBluePrintFactory factory = new JavaDateBluePrintFactory();
	
	@SuppressWarnings("deprecation")
	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(new Date()));
		assertTrue(factory.createBluePrintForType(new java.sql.Date(2021-1900, 9-1, 5)));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testJavaUtilDateValueCreation() {
		@SuppressWarnings("deprecation")
		Date date = new Date(2020 - 1900, 9, 25);
		JavaDateBluePrint dateBp = (JavaDateBluePrint) factory.createBluePrint("date", date);

		assertEquals(120, dateBp.getYear());
		assertEquals(9, dateBp.getMonth());
		assertEquals(25, dateBp.getDay());

		assertEquals(0, dateBp.getHour());
		assertEquals(0, dateBp.getMinute());
		assertEquals(0, dateBp.getSecond());

		assertEquals(Date.class, dateBp.getReferenceClass());
	}

	@Test
	public void testJavaSqlDateValueCreation() {
		@SuppressWarnings("deprecation")
		java.sql.Date sqlDate = new java.sql.Date(2020 - 1900, 10 - 1, 25);
		JavaDateBluePrint dateBp = (JavaDateBluePrint) factory.createBluePrint("sqlDate", sqlDate);

		assertEquals(120, dateBp.getYear());
		assertEquals(9, dateBp.getMonth());
		assertEquals(25, dateBp.getDay());

		assertEquals(0, dateBp.getHour());
		assertEquals(0, dateBp.getMinute());
		assertEquals(0, dateBp.getSecond());
		assertEquals(java.sql.Date.class, dateBp.getReferenceClass());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testImmutablityOfBluePrint() {
		Date date = new Date(2020 - 1900, 9, 25);

		JavaDateBluePrint dateBp = (JavaDateBluePrint) factory.createBluePrint("date", date);

		assertEquals(120, dateBp.getYear());
		assertEquals(9, dateBp.getMonth());
		assertEquals(25, dateBp.getDay());

		assertEquals(0, dateBp.getHour());
		assertEquals(0, dateBp.getMinute());
		assertEquals(0, dateBp.getSecond());

		date = new Date(2020 - 1900, 11, 24);
		// value of the Blueprint dont change, once created
		assertEquals(120, dateBp.getYear());
		assertEquals(9, dateBp.getMonth());
		assertEquals(25, dateBp.getDay());

		assertEquals(0, dateBp.getHour());
		assertEquals(0, dateBp.getMinute());
		assertEquals(0, dateBp.getSecond());
	}

}
