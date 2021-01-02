package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

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

	public static class LocalDateBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalDate;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new LocalDateBluePrint(name, (LocalDate) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
