package de.nvg.valuetracker.blueprint.simpletypes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class DateBluePrint extends SimpleBluePrint<Date> {

	DateBluePrint(String fieldName, Date value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		// TODO CS mit Andi reden, selbe wie bei sql.Date
		return "new $T(" + value.getTime() + ")";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(Date.class);
	}

}
