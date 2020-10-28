package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class LocalDateTimeBluePrint extends SimpleBluePrint<LocalDateTime> {

	LocalDateTimeBluePrint(String fieldName, LocalDateTime value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(LocalDateTime value) {
		return "$T.of($T.of(" + value.getYear() + ", $T." + value.getMonth() + ", " + value.getDayOfMonth() //
				+ "), $T.of(" + value.getHour() + ", " + value.getMinute() + ", " + value.getSecond() + "))";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(LocalDateTime.class, LocalDate.class, Month.class, LocalTime.class);
	}

}
