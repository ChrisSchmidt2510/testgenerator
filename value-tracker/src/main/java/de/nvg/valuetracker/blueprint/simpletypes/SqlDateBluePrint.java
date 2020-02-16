package de.nvg.valuetracker.blueprint.simpletypes;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class SqlDateBluePrint extends SimpleBluePrint<Date> {

	SqlDateBluePrint(String fieldName, Date value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		// TOOD mit Andi reden
		return "new $T(" + value.getTime() + ")";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(Date.class);
	}

}
