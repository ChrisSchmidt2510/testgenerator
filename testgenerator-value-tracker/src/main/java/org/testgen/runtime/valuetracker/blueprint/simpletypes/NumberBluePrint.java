package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class NumberBluePrint extends SimpleBluePrint<Number> {
	private int bigDecimalScale;

	NumberBluePrint(String name, Number value) {
		super(name, value);
	}

	@Override
	protected String createValue(Number value) {
		if (value instanceof Integer) {
			return Integer.toString(value.intValue());
		} else if (value instanceof Short) {
			return Integer.toString(value.intValue());
		} else if (value instanceof Byte) {
			return Integer.toString(value.intValue());
		} else if (value instanceof Float) {
			return Float.toString(value.floatValue()) + "f";
		} else if (value instanceof Double) {
			return String.valueOf(value.doubleValue());
		} else if (value instanceof Long) {
			return String.valueOf(value.longValue());
		} else if (value instanceof BigDecimal) {
			BigDecimal decimal = (BigDecimal) value;

			bigDecimalScale = decimal.scale();
			return String.valueOf(decimal.doubleValue());
		}
		throw new IllegalArgumentException("unvalid Value for NumberBluePrint");
	}

	public int getBigDecimalScale() {
		return bigDecimalScale;
	}

	public static class NumberBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof Number;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new NumberBluePrint(name, (Number) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
