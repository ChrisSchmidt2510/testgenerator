package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint.JavaDateBluePrintFactory;

public class JavaDateBluePrintTest {
	
private JavaDateBluePrintFactory factory = new JavaDateBluePrintFactory();
	
	@SuppressWarnings("deprecation")
	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(new Date()));
		Assert.assertTrue(factory.createBluePrintForType(new java.sql.Date(2021-1900, 9-1, 5)));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testJavaUtilDateValueCreation() {
		@SuppressWarnings("deprecation")
		Date date = new Date(2020 - 1900, 9, 25);
		JavaDateBluePrint dateBp = (JavaDateBluePrint) factory.createBluePrint("date", date);

		Assert.assertEquals(120, dateBp.getYear());
		Assert.assertEquals(9, dateBp.getMonth());
		Assert.assertEquals(25, dateBp.getDay());

		Assert.assertEquals(0, dateBp.getHour());
		Assert.assertEquals(0, dateBp.getMinute());
		Assert.assertEquals(0, dateBp.getSecond());

		Assert.assertEquals(Date.class, dateBp.getReferenceClass());
	}

	@Test
	public void testJavaSqlDateValueCreation() {
		@SuppressWarnings("deprecation")
		java.sql.Date sqlDate = new java.sql.Date(2020 - 1900, 10 - 1, 25);
		JavaDateBluePrint dateBp = (JavaDateBluePrint) factory.createBluePrint("sqlDate", sqlDate);

		Assert.assertEquals(120, dateBp.getYear());
		Assert.assertEquals(9, dateBp.getMonth());
		Assert.assertEquals(25, dateBp.getDay());

		Assert.assertEquals(0, dateBp.getHour());
		Assert.assertEquals(0, dateBp.getMinute());
		Assert.assertEquals(0, dateBp.getSecond());
		Assert.assertEquals(java.sql.Date.class, dateBp.getReferenceClass());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testImmutablityOfBluePrint() {
		Date date = new Date(2020 - 1900, 9, 25);

		JavaDateBluePrint dateBp = (JavaDateBluePrint) factory.createBluePrint("date", date);

		Assert.assertEquals(120, dateBp.getYear());
		Assert.assertEquals(9, dateBp.getMonth());
		Assert.assertEquals(25, dateBp.getDay());

		Assert.assertEquals(0, dateBp.getHour());
		Assert.assertEquals(0, dateBp.getMinute());
		Assert.assertEquals(0, dateBp.getSecond());

		date = new Date(2020 - 1900, 11, 24);
		// value of the Blueprint dont change, once created
		Assert.assertEquals(120, dateBp.getYear());
		Assert.assertEquals(9, dateBp.getMonth());
		Assert.assertEquals(25, dateBp.getDay());

		Assert.assertEquals(0, dateBp.getHour());
		Assert.assertEquals(0, dateBp.getMinute());
		Assert.assertEquals(0, dateBp.getSecond());
	}

}
