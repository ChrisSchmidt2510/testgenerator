package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

public class CalenderBluePrintTest {

	@Test
	public void testValueCreation() {
		Calendar calendar = new GregorianCalendar(2020, Calendar.OCTOBER, 25, 12, 5, 3);

		CalendarBluePrint calendarBp = new CalendarBluePrint("calendar", calendar);

		Assert.assertEquals(2020, calendarBp.getYear());
		Assert.assertEquals(9, calendarBp.getMonth());
		Assert.assertEquals(25, calendarBp.getDay());
		Assert.assertEquals(12, calendarBp.getHour());
		Assert.assertEquals(5, calendarBp.getMinute());
		Assert.assertEquals(3, calendarBp.getSecond());

		Assert.assertEquals(GregorianCalendar.class, calendarBp.getReferenceClass());
	}

}
