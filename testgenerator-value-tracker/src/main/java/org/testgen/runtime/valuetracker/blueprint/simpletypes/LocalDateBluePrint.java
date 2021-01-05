package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;

import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;

public class LocalDateBluePrint extends SimpleBluePrint<LocalDate> implements DateBluePrint {
	private int year;
	private int month;
	private int day;

	LocalDateBluePrint(String fieldName, LocalDate value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(LocalDate value) {
		year = value.getYear();
		month = value.getMonthValue();
		day = value.getDayOfMonth();

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

	public static class LocalDateBluePrintFactory implements SimpleBluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalDate;
		}

		@Override
		public SimpleBluePrint<?> createBluePrint(String name, Object value) {
			return new LocalDateBluePrint(name, (LocalDate) value);
		}

	}

}
