package de.nvg.valuetracker.blueprint.simpletypes;

import java.time.LocalDate;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class LocalDateBluePrint extends SimpleBluePrint<LocalDate> {

	LocalDateBluePrint(String fieldName, LocalDate value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "$T.of(" + value.getYear() + ", $T." + value.getMonth() + ", " + value.getDayOfMonth() + ")";
	}

}
