package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint.CalendarBluePrintFactory;

public class CalenderBluePrintTest {

private CalendarBluePrintFactory factory = new CalendarBluePrintFactory();
	
	@Test
	public void testBluePrintFactory() {
		Calendar calendar = new GregorianCalendar(2020, Calendar.OCTOBER, 25, 12, 5, 3);
		
		Assert.assertTrue(factory.createBluePrintForType(calendar));
		Assert.assertFalse(factory.createBluePrintForType(true));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreation() {
		Calendar calendar = new GregorianCalendar(2020, Calendar.OCTOBER, 25, 12, 5, 3);
		
		CalendarBluePrint bluePrint =(CalendarBluePrint) factory.createBluePrint("calendar", calendar);

		Assert.assertEquals(2020, bluePrint.getYear());
		Assert.assertEquals(9, bluePrint.getMonth());
		Assert.assertEquals(25, bluePrint.getDay());
		Assert.assertEquals(12, bluePrint.getHour());
		Assert.assertEquals(5, bluePrint.getMinute());
		Assert.assertEquals(3, bluePrint.getSecond());

		Assert.assertEquals(GregorianCalendar.class, bluePrint.getReferenceClass());
	}

}
