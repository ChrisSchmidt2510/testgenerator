package de.nvg.valuetracker.blueprint.simpletypes;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.nvg.valuetracker.blueprint.BluePrint;

public final class SimpleBluePrintFactory {

	private SimpleBluePrintFactory() {
	}

	public static BluePrint of(String fieldName, Object value) {
		if (value == null) {
			return new NullBluePrint(fieldName);
		} else if (value instanceof Number) {
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
		} else if (value instanceof Date || value instanceof java.sql.Date) {
			return new DateBluePrint(fieldName, (Date) value);
		} else if (value instanceof String) {
			return new StringBluePrint(fieldName, (String) value);
		} else if (value instanceof Enum) {
			return new EnumBluePrint(fieldName, (Enum<?>) value);
		}

		return null;
	}

}
