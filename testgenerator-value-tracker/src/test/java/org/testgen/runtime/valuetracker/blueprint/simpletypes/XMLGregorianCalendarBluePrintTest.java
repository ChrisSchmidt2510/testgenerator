package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint.XMLGregorianCalendarBluePrintFactory;

public class XMLGregorianCalendarBluePrintTest {

	private XMLGregorianCalendarBluePrintFactory factory = new XMLGregorianCalendarBluePrintFactory();

	@Test
	public void testBluePrintFactory() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();

		assertTrue(factory.createBluePrintForType(calendar));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertTrue(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2020, 12, 31, 60);

		XMLGregorianCalendarBluePrint bluePrint = (XMLGregorianCalendarBluePrint) factory.createBluePrint("calendar",
				calendar);

		assertEquals("calendar", bluePrint.getName());
		assertEquals(2020, bluePrint.getYear());
		assertEquals(12, bluePrint.getMonth());
		assertEquals(31, bluePrint.getDay());
		assertEquals(60, bluePrint.getTimezone());

		assertTrue(bluePrint.getHour() == 0 && bluePrint.getMinute() == 0 && bluePrint.getSecond() == 0);
	}
}
