package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Calendar;
import java.util.Objects;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

public class CalendarBluePrint extends SimpleBluePrint<Calendar> implements DateBluePrint, TimeBluePrint {
	private int year;
	private int month;
	private int day;

	private int hour;
	private int minute;
	private int second;

	CalendarBluePrint(String fieldName, Calendar value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Calendar value) {
		year = value.get(Calendar.YEAR);
		month = value.get(Calendar.MONTH);
		day = value.get(Calendar.DAY_OF_MONTH);

		hour = value.get(Calendar.HOUR_OF_DAY);
		minute = value.get(Calendar.MINUTE);
		second = value.get(Calendar.SECOND);

		return null;
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
	public int hashCode() {
		return Objects.hash(name, day, hour, minute, month, second, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CalendarBluePrint))
			return false;
		CalendarBluePrint other = (CalendarBluePrint) obj;
		return Objects.equals(name, other.name) && day == other.day && hour == other.hour && minute == other.minute
				&& month == other.month && second == other.second && year == other.year;
	}

	@Override
	public String toString() {
		return String.format("Field: %s Value: %d-%d-%d %d:%d:%d", name, day, month, year, hour, minute, second);
	}

	public static class CalendarBluePrintFactory implements SimpleBluePrintFactory<Calendar> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Calendar;
		}

		@Override
		public SimpleBluePrint<Calendar> createBluePrint(String name, Calendar value) {
			return new CalendarBluePrint(name, value);
		}

	}

}
