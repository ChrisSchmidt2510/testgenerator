package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDateTime;
import java.util.Objects;

import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;

public class LocalDateTimeBluePrint extends SimpleBluePrint<LocalDateTime> implements DateBluePrint, TimeBluePrint {
	private int day;
	private int month;
	private int year;

	private int hour;
	private int minute;
	private int second;

	LocalDateTimeBluePrint(String fieldName, LocalDateTime value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(LocalDateTime value) {
		day = value.getDayOfMonth();
		month = value.getMonthValue();
		year = value.getYear();

		hour = value.getHour();
		minute = value.getMinute();
		second = value.getSecond();

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
		if (!(obj instanceof LocalDateTimeBluePrint))
			return false;
		LocalDateTimeBluePrint other = (LocalDateTimeBluePrint) obj;
		return Objects.equals(name, other.name) && day == other.day && hour == other.hour && minute == other.minute
				&& month == other.month && second == other.second && year == other.year;
	}

	@Override
	public String toString() {
		return String.format("Field: %s Value: %d-%d-%d %d:%d:%d", name, day, month, year, hour, minute, second);
	}

	public static class LocalDateTimeBluePrintFactory implements SimpleBluePrintFactory<LocalDateTime> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalDateTime;
		}

		@Override
		public SimpleBluePrint<LocalDateTime> createBluePrint(String name, LocalDateTime value) {
			return new LocalDateTimeBluePrint(name, value);
		}

	}

}
