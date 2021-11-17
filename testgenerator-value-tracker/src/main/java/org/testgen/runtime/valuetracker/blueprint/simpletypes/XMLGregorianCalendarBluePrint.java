package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

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

	@Override
	public int hashCode() {
		return Objects.hash(name, day, hour, millis, minute, month, second, timezone, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof XMLGregorianCalendarBluePrint))
			return false;
		XMLGregorianCalendarBluePrint other = (XMLGregorianCalendarBluePrint) obj;
		return Objects.equals(name, other.name) && day == other.day && hour == other.hour && millis == other.millis
				&& minute == other.minute && month == other.month && second == other.second
				&& timezone == other.timezone && year == other.year;
	}

	@Override
	public String toString() {
		return String.format("Field: %s Value: %d-%d-%d %d:%d:%d timezone %d", name, day, month, year, hour, minute,
				second, timezone);
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
