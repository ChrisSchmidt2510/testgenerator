package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateBluePrintTest {

	@Test
	public void testJavaUtilDateValueCreation() {
		@SuppressWarnings("deprecation")
		Date date = new Date(2020 - 1900, 9, 25);
		DateBluePrint dateBp = new DateBluePrint("date", date);

		Assert.assertEquals("new $T(2020 - 1900, 10-1 , 25 , 0 , 0 , 0)", dateBp.valueCreation());
		Assert.assertEquals(Arrays.asList(Date.class), dateBp.getReferenceClasses());
	}

	@Test
	public void testJavaSqlDateValueCreation() {
		@SuppressWarnings("deprecation")
		java.sql.Date sqlDate = new java.sql.Date(2020 - 1900, 10 - 1, 25);
		DateBluePrint dateBp = new DateBluePrint("date", sqlDate);

		Assert.assertEquals("new $T(2020 - 1900, 10-1 , 25)", dateBp.valueCreation());
		Assert.assertEquals(Arrays.asList(java.sql.Date.class), dateBp.getReferenceClasses());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testImmutablityOfBluePrint() {
		Date date = new Date(2020 - 1900, 9, 25);

		DateBluePrint dateBp = new DateBluePrint("date", date);
		Assert.assertEquals("new $T(2020 - 1900, 10-1 , 25 , 0 , 0 , 0)", dateBp.valueCreation());

		date = new Date(2020 - 1900, 11, 24);
		// value of the Blueprint dont change, once created
		Assert.assertEquals("new $T(2020 - 1900, 10-1 , 25 , 0 , 0 , 0)", dateBp.valueCreation());
	}

}
