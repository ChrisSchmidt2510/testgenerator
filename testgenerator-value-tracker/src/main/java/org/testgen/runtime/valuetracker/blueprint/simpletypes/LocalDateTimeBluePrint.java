package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
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

	public static class LocalDateTimeBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalDateTime;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new LocalDateTimeBluePrint(name, (LocalDateTime) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
