package de.nvg.valuetracker.blueprint.simpletypes;

import java.math.BigDecimal;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class NumberBluePrint extends SimpleBluePrint<Number> {

	NumberBluePrint(String name, Number value) {
		super(name, value);
	}

	@Override
	public String valueCreation() {
		if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
			return String.valueOf(value.intValue());
		} else if (value instanceof Float) {
			return String.valueOf(value.floatValue());
		} else if (value instanceof Double) {
			return String.valueOf(value.doubleValue());
		} else if (value instanceof Long) {
			return String.valueOf(value.longValue());
		} else if (value instanceof BigDecimal) {
			BigDecimal decimal = (BigDecimal) value;
			return "$T.valueOf(" + decimal.doubleValue() + ").setScale(" + decimal.scale() + ")";
		}
		throw new IllegalArgumentException("unvalid Value for NumberBluePrint");
	}

}
