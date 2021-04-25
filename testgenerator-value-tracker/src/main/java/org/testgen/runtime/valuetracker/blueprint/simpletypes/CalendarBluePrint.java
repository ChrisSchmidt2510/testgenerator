package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Calendar;

import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;

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

	public static class CalendarBluePrintFactory implements SimpleBluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Calendar;
		}

		@Override
		public SimpleBluePrint<?> createBluePrint(String name, Object value) {
			return new CalendarBluePrint(name, (Calendar) value);
		}

	}

}
