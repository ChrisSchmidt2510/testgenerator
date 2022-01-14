package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint.CalendarBluePrintFactory;

public class CalenderBluePrintTest {

private CalendarBluePrintFactory factory = new CalendarBluePrintFactory();
	
	@Test
	public void testBluePrintFactory() {
		Calendar calendar = new GregorianCalendar(2020, Calendar.OCTOBER, 25, 12, 5, 3);
		
		assertTrue(factory.createBluePrintForType(calendar));
		assertFalse(factory.createBluePrintForType(true));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testValueCreation() {
		Calendar calendar = new GregorianCalendar(2020, Calendar.OCTOBER, 25, 12, 5, 3);
		
		CalendarBluePrint bluePrint =(CalendarBluePrint) factory.createBluePrint("calendar", calendar);

		assertEquals(2020, bluePrint.getYear());
		assertEquals(9, bluePrint.getMonth());
		assertEquals(25, bluePrint.getDay());
		assertEquals(12, bluePrint.getHour());
		assertEquals(5, bluePrint.getMinute());
		assertEquals(3, bluePrint.getSecond());

		assertEquals(GregorianCalendar.class, bluePrint.getReferenceClass());
	}

}
