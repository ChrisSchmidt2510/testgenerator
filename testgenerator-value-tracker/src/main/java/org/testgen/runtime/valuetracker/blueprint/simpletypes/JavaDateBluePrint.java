package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Date;
import java.util.Objects;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

public class JavaDateBluePrint extends SimpleBluePrint<Date> implements DateBluePrint, TimeBluePrint {
	private int year;
	private int month;
	private int day;

	private int hour;
	private int minute;
	private int second;

	JavaDateBluePrint(String fieldName, Date value) {
		super(fieldName, value);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String createValue(Date value) {

		year = value.getYear();
		month = value.getMonth();
		day = value.getDate();

		if (!(value instanceof java.sql.Date)) {
			hour = value.getHours();
			minute = value.getMinutes();
			second = value.getSeconds();
		}

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
		if (!(obj instanceof JavaDateBluePrint))
			return false;
		JavaDateBluePrint other = (JavaDateBluePrint) obj;
		return Objects.equals(name, other.name) && day == other.day && hour == other.hour && minute == other.minute
				&& month == other.month && second == other.second && year == other.year;
	}

	@Override
	public String toString() {
		return value instanceof java.sql.Date ? String.format("Field: %s Value: %d-%d-%d", name, day, month, year)
				: String.format("Field: %s Value: %d-%d-%d %d:%d:%d", name, day, month, year, hour, minute, second);
	}

	public static class JavaDateBluePrintFactory implements SimpleBluePrintFactory<Date> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Date;
		}

		@Override
		public SimpleBluePrint<Date> createBluePrint(String name, Date value) {
			return new JavaDateBluePrint(name, value);
		}

	}

}
