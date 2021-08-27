package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import javax.xml.datatype.XMLGregorianCalendar;

import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;

public class XMLGregorianCalendarBluePrint extends SimpleBluePrint<XMLGregorianCalendar>
		implements DateBluePrint, TimeBluePrint {

	private int year;
	private int month;
	private int day;

	private int hour;
	private int minute;
	private int second;

	private int millis;

	private int timezone;

	public XMLGregorianCalendarBluePrint(String fieldName, XMLGregorianCalendar value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(XMLGregorianCalendar value) {
		year = value.getYear();
		month = value.getMonth();
		day = value.getDay();

		if (value.getHour() >= 0)
			hour = value.getHour();

		if (value.getMinute() > 0)
			minute = value.getMinute();

		if (value.getSecond() >= 0)
			second = value.getSecond();

		if (value.getMillisecond() >= 0)
			millis = value.getMillisecond();

		timezone = value.getTimezone();
		return null;
	}

	@Override
	public int getHour() {
		return hour;
	}

	@Override
	public int getMinute() {
		return minute;
	}

	@Override
	public int getSecond() {
		return second;
	}

	@Override
	public int getDay() {
		return day;
	}

	@Override
	public int getMonth() {
		return month;
	}

	@Override
	public int getYear() {
		return year;
	}

	public int getMillisecond() {
		return millis;
	}

	public int getTimezone() {
		return timezone;
	}

	public static class XMLGregorianCalendarBluePrintFactory implements SimpleBluePrintFactory<XMLGregorianCalendar> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof XMLGregorianCalendar;
		}

		@Override
		public SimpleBluePrint<XMLGregorianCalendar> createBluePrint(String name, XMLGregorianCalendar value) {
			return new XMLGregorianCalendarBluePrint(name, value);
		}

	}

}
