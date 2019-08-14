package de.nvg.valuetracker.blueprint.simpletypes;

import java.sql.Date;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class SqlDateBluePrint extends SimpleBluePrint<Date> {

	SqlDateBluePrint(String fieldName, Date value) {
		super(fieldName, value);
	}

	@Override
	public String valueCreation() {
		// TOOD mit Andi reden
		return "new java.sql.Date(" + value.getTime() + ");";
	}

}
