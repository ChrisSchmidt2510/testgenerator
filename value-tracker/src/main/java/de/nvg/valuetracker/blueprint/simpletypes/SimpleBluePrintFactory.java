package de.nvg.valuetracker.blueprint.simpletypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.nvg.valuetracker.blueprint.BluePrint;

public class SimpleBluePrintFactory {

	private static final List<Class<?>> INTEGERS = Arrays.asList(Integer.class, Short.class, Byte.class, Long.class,
			Float.class, Double.class, BigDecimal.class);

	public static BluePrint of(String fieldName, Object value) {
		if (value == null) {
			return new NullBluePrint(fieldName);
		} else if (INTEGERS.contains(value.getClass())) {
			return new NumberBluePrint(fieldName, (Number) value);
		} else if (value instanceof Boolean) {
			return new BooleanBluePrint(fieldName, (Boolean) value);
		} else if (value instanceof Character) {
			return new CharacterBluePrint(fieldName, (Character) value);
		} else if (value instanceof Calendar || value instanceof GregorianCalendar) {
			return new CalendarBluePrint(fieldName, (Calendar) value);
		} else if (value instanceof LocalDate) {
			return new LocalDateBluePrint(fieldName, (LocalDate) value);
		} else if (value instanceof LocalTime) {
			return new LocalTimeBluePrint(fieldName, (LocalTime) value);
		} else if (value instanceof LocalDateTime) {
			return new LocalDateTimeBluePrint(fieldName, (LocalDateTime) value);
		} else if (value instanceof Date) {
			return new DateBluePrint(fieldName, (Date) value);
		} else if (value instanceof java.sql.Date) {
			return new SqlDateBluePrint(fieldName, (java.sql.Date) value);
		} else if (value instanceof String) {
			return new StringBluePrint(fieldName, (String) value);
		} else if (value instanceof Enum) {
			return new EnumBluePrint(fieldName, (Enum<?>) value);
		}

		return null;
	}

}
