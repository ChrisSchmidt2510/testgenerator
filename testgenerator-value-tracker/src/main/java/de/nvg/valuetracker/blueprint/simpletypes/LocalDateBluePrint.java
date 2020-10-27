package de.nvg.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class LocalDateBluePrint extends SimpleBluePrint<LocalDate> {

	LocalDateBluePrint(String fieldName, LocalDate value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(LocalDate value) {
		return "$T.of(" + value.getYear() + ", $T." + value.getMonth() + ", " + value.getDayOfMonth() + ")";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(LocalDate.class, Month.class);
	}

}
