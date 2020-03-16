package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class CalendarBluePrint extends SimpleBluePrint<Calendar> {

	CalendarBluePrint(String fieldName, Calendar value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(Calendar value) {
		return "new $T(" + value.get(Calendar.YEAR) + ", " + (value.get(Calendar.MONTH) + 1) + "-1, "
				+ value.get(Calendar.DAY_OF_MONTH) + ", " + value.get(Calendar.HOUR_OF_DAY) + ", "
				+ value.get(Calendar.MINUTE) + ", " + value.get(Calendar.SECOND) + ")";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(value.getClass());
	}

}
