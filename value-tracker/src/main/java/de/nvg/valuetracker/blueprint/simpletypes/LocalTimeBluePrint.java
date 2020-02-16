package de.nvg.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class LocalTimeBluePrint extends SimpleBluePrint<LocalTime> {

	LocalTimeBluePrint(String fieldName, LocalTime value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "$T.of(" + value.getHour() + "," + value.getMinute() + "," + value.getSecond() + ");";
	}

}
