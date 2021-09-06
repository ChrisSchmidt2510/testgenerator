package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint.XMLGregorianCalendarBluePrintFactory;

public class XMLGregorianCalendarBluePrintTest {

	private XMLGregorianCalendarBluePrintFactory factory = new XMLGregorianCalendarBluePrintFactory();

	@Test
	public void testBluePrintFactory() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();

		Assert.assertTrue(factory.createBluePrintForType(calendar));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertTrue(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testValueCreation() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2020, 12, 31, 60);

		XMLGregorianCalendarBluePrint bluePrint = (XMLGregorianCalendarBluePrint) factory.createBluePrint("calendar",
				calendar);

		Assert.assertEquals("calendar", bluePrint.getName());
		Assert.assertEquals(2020, bluePrint.getYear());
		Assert.assertEquals(12, bluePrint.getMonth());
		Assert.assertEquals(31, bluePrint.getDay());
		Assert.assertEquals(60, bluePrint.getTimezone());

		Assert.assertTrue(bluePrint.getHour() == 0 && bluePrint.getMinute() == 0 && bluePrint.getSecond() == 0);
	}
}
