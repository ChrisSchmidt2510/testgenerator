package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

public class CalenderBluePrintTest {

	@Test
	public void testValueCreation() {
		Calendar calendar = new GregorianCalendar(2020, Calendar.OCTOBER, 25, 12, 5, 3);

		CalendarBluePrint calendarBp = new CalendarBluePrint("calendar", calendar);
		Assert.assertEquals("new $T(2020, 10-1, 25, 12, 5, 3)", calendarBp.valueCreation());
	}

}
