package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class DateBluePrint extends SimpleBluePrint<Date> {

	DateBluePrint(String fieldName, Date value) {
		super(fieldName, value);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String createValue(Date value) {

		if (value instanceof java.sql.Date) {
			return "new $T(" + (value.getYear() + 1900) + " - 1900, " + (value.getMonth() + 1) + "-1 , "
					+ value.getDate() + ")";
		}
		return "new $T(" + (value.getYear() + 1900) + " - 1900, " + (value.getMonth() + 1) + "-1 , " + value.getDate()//
				+ " , " + value.getHours() + " , " + value.getMinutes() + " , " + value.getSeconds() + ")";
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		return Arrays.asList(value.getClass());
	}

}
