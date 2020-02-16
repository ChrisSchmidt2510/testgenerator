package de.nvg.valuetracker.blueprint.simpletypes;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class LocalDateTimeBluePrint extends SimpleBluePrint<LocalDateTime> {

	LocalDateTimeBluePrint(String fieldName, LocalDateTime value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "$T.of(" + value.getYear() + ", $T." + value.getMonth() + ", " + value.getDayOfMonth() + ", "
				+ value.getHour() + ", " + value.getMinute() + ", " + value.getSecond() + ")";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(LocalDateTime.class, Month.class);
	}

}
