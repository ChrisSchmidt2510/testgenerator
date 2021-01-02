package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Date;
import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;

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

	public static class JavaDateBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Date;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new JavaDateBluePrint(name, (Date) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
