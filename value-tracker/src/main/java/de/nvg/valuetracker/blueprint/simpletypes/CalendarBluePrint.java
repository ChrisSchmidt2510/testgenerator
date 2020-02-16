package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Calendar;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class CalendarBluePrint extends SimpleBluePrint<Calendar> {

	CalendarBluePrint(String fieldName, Calendar value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		return "new $T(" + value.get(Calendar.YEAR) + ", " + value.get(Calendar.MONTH) + ", "
				+ value.get(Calendar.DAY_OF_MONTH) + ", " + value.get(Calendar.HOUR_OF_DAY) + ", "
				+ value.get(Calendar.MINUTE) + ", " + value.get(Calendar.SECOND) + ")";
	}

}
