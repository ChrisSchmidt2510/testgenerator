package org.testgen.runtime.valuetracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.CalendarBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.JavaDateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalDateTimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.XMLGregorianCalendarBluePrint;

public class ObjectValueTrackerTest {

	public enum TestValue {
		FIRST_VALUE, SECOND_VALUE;
	}

	private final ObjectValueTracker valueTracker = ObjectValueTracker.getInstance();

	@Test
	public void testTrackBoolean() {
		BluePrint bluePrint = valueTracker.trackValues(false, "boolean");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("boolean", simpleBluePrint.getName());
		Assert.assertEquals("false", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackCalendar() {
		BluePrint bluePrint = valueTracker.trackValues(new GregorianCalendar(2020, 12 - 1, 31), "calendar");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		CalendarBluePrint calendarBP = (CalendarBluePrint) bluePrint;
		Assert.assertEquals("calendar", calendarBP.getName());
		Assert.assertEquals(2020, calendarBP.getYear());
		Assert.assertEquals(11, calendarBP.getMonth());
		Assert.assertEquals(31, calendarBP.getDay());

		Assert.assertTrue(calendarBP.getHour() == 0 && calendarBP.getMinute() == 0 && calendarBP.getSecond() == 0);
	}

	@Test
	public void testTrackCharacter() {
		BluePrint bluePrint = valueTracker.trackValues('C', "char");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("char", simpleBluePrint.getName());
		Assert.assertEquals("C", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackClass() {
		BluePrint bluePrint = valueTracker.trackValues(String.class, "class");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("class", simpleBluePrint.getName());
		Assert.assertEquals("String", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackEnum() {
		BluePrint bluePrint = valueTracker.trackValues(TestValue.FIRST_VALUE, "testValue");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("testValue", simpleBluePrint.getName());
		Assert.assertEquals("FIRST_VALUE", simpleBluePrint.valueCreation());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testTrackDate() {
		BluePrint bluePrint = valueTracker.trackValues(new Date(2020 - 1900, 12 - 1, 31, 12, 27, 15), "date");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		JavaDateBluePrint calendarBP = (JavaDateBluePrint) bluePrint;
		Assert.assertEquals("date", calendarBP.getName());
		Assert.assertEquals(120, calendarBP.getYear());
		Assert.assertEquals(11, calendarBP.getMonth());
		Assert.assertEquals(31, calendarBP.getDay());
		Assert.assertEquals(12, calendarBP.getHour());
		Assert.assertEquals(27, calendarBP.getMinute());
		Assert.assertEquals(15, calendarBP.getSecond());

		BluePrint bluePrint2 = valueTracker.trackValues(new java.sql.Date(2020 - 1900, 12 - 1, 24), "SqlDate");

		Assert.assertTrue(bluePrint2.isSimpleBluePrint());

		JavaDateBluePrint sqlDate = (JavaDateBluePrint) bluePrint2;
		Assert.assertEquals("SqlDate", sqlDate.getName());
		Assert.assertEquals(120, sqlDate.getYear());
		Assert.assertEquals(11, sqlDate.getMonth());
		Assert.assertEquals(24, sqlDate.getDay());

		Assert.assertTrue(sqlDate.getHour() == 0 && sqlDate.getMinute() == 0 && sqlDate.getSecond() == 0);
	}

	@Test
	public void testTrackLocalDate() {
		BluePrint bluePrint = valueTracker.trackValues(LocalDate.of(2020, Month.DECEMBER, 25), "localDate");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		DateBluePrint dateBp = (DateBluePrint) bluePrint;

		Assert.assertEquals("localDate", bluePrint.getName());
		Assert.assertEquals(2020, dateBp.getYear());
		Assert.assertEquals(12, dateBp.getMonth());
		Assert.assertEquals(25, dateBp.getDay());
	}

	@Test
	public void testTrackLocalTime() {
		BluePrint bluePrint = valueTracker.trackValues(LocalTime.of(23, 59), "localTime");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		TimeBluePrint timeBp = (TimeBluePrint) bluePrint;

		Assert.assertEquals("localTime", bluePrint.getName());
		Assert.assertEquals(23, timeBp.getHour());
		Assert.assertEquals(59, timeBp.getMinute());
		Assert.assertEquals(0, timeBp.getSecond());
	}

	@Test
	public void testTrackLocalDateTime() {
		BluePrint bluePrint = valueTracker.trackValues(
				LocalDateTime.of(LocalDate.of(1998, Month.OCTOBER, 25), LocalTime.of(5, 27, 30)), "localDateTime");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		LocalDateTimeBluePrint dateTime = (LocalDateTimeBluePrint) bluePrint;

		Assert.assertEquals("localDateTime", dateTime.getName());
		Assert.assertEquals(1998, dateTime.getYear());
		Assert.assertEquals(10, dateTime.getMonth());
		Assert.assertEquals(25, dateTime.getDay());

		Assert.assertEquals(5, dateTime.getHour());
		Assert.assertEquals(27, dateTime.getMinute());
		Assert.assertEquals(30, dateTime.getSecond());
	}

	@Test
	public void testTrackNumber() {
		BluePrint bluePrint = valueTracker.trackValues(10, "Integer");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("Integer", simpleBluePrint.getName());
		Assert.assertEquals("10", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackString() {
		BluePrint bluePrint = valueTracker.trackValues("This is a test", "Characters");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		SimpleBluePrint<?> simpleBluePrint = bluePrint.castToSimpleBluePrint();
		Assert.assertEquals("Characters", simpleBluePrint.getName());
		Assert.assertEquals("This is a test", simpleBluePrint.valueCreation());
	}

	@Test
	public void testTrackXMLGregorianCalendar() throws DatatypeConfigurationException {
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(2020, 12, 24, 23, 55, 12,
				600, 60);

		BluePrint bluePrint = valueTracker.trackValues(calendar, "calendar");

		Assert.assertTrue(bluePrint.isSimpleBluePrint());

		XMLGregorianCalendarBluePrint calendarBP = (XMLGregorianCalendarBluePrint) bluePrint;

		Assert.assertEquals("calendar", calendarBP.getName());
		Assert.assertEquals(2020, calendarBP.getYear());
		Assert.assertEquals(12, calendarBP.getMonth());
		Assert.assertEquals(24, calendarBP.getDay());

		Assert.assertEquals(23, calendarBP.getHour());
		Assert.assertEquals(55, calendarBP.getMinute());
		Assert.assertEquals(12, calendarBP.getSecond());
		Assert.assertEquals(600, calendarBP.getMillisecond());
		Assert.assertEquals(60, calendarBP.getTimezone());

	}

}
