package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.util.Objects;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.datetime.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

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

	@Override
	public int hashCode() {
		return Objects.hash(name, day, month, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LocalDateBluePrint))
			return false;
		LocalDateBluePrint other = (LocalDateBluePrint) obj;
		return Objects.equals(name, other.name) && day == other.day && month == other.month && year == other.year;
	}
	
	@Override
	public String toString() {
		return String.format("Field: %s Value: %d-%d-%d", name, day, month, year);
	}

	public static class LocalDateBluePrintFactory implements SimpleBluePrintFactory<LocalDate> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalDate;
		}

		@Override
		public SimpleBluePrint<LocalDate> createBluePrint(String name, LocalDate value) {
			return new LocalDateBluePrint(name, value);
		}

	}

}
