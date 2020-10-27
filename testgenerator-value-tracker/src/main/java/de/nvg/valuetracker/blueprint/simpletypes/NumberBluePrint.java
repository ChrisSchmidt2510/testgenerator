package de.nvg.valuetracker.blueprint.simpletypes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.SimpleBluePrint;

public class NumberBluePrint extends SimpleBluePrint<Number> {

	NumberBluePrint(String name, Number value) {
		super(name, value);
	}

	@Override
	protected String createValue(Number value) {
		if (value instanceof Integer) {
			return String.valueOf(value.intValue());
		} else if (value instanceof Short) {
			return "(short)" + value.intValue();
		} else if (value instanceof Byte) {
			return "(byte)" + value.intValue();
		} else if (value instanceof Float) {
			return String.valueOf(value.floatValue() + "f");
		} else if (value instanceof Double) {
			return String.valueOf(value.doubleValue());
		} else if (value instanceof Long) {
			return String.valueOf(value.longValue() + "L");
		} else if (value instanceof BigDecimal) {
			BigDecimal decimal = (BigDecimal) value;
			return "$T.valueOf(" + decimal.doubleValue() + ").setScale(" + decimal.scale() + ")";
		}
		throw new IllegalArgumentException("unvalid Value for NumberBluePrint");
	}

	@Override
	public List<Class<?>> getReferenceClasses() {
		if (value instanceof BigDecimal) {
			return Arrays.asList(BigDecimal.class);
		}

		return Collections.emptyList();
	}

}
