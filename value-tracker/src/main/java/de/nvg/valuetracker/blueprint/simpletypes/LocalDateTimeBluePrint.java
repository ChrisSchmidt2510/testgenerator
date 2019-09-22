package de.nvg.valuetracker.blueprint.simpletypes;

import java.time.LocalDateTime;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class LocalDateTimeBluePrint extends SimpleBluePrint<LocalDateTime> {

	LocalDateTimeBluePrint(String fieldName, LocalDateTime value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "LocalDateTime.of(" + value.getYear() + ", Month." + value.getMonth() + ", " + value.getDayOfMonth()
				+ ", " + value.getHour() + ", " + value.getMinute() + ", " + value.getSecond() + ");";
	}

}
