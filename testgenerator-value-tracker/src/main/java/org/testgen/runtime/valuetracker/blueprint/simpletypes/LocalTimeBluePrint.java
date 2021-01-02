package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;
import java.util.function.BiFunction;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;

public class LocalTimeBluePrint extends SimpleBluePrint<LocalTime> implements TimeBluePrint {
	private int hour;

	private int minute;

	private int second;

	LocalTimeBluePrint(String fieldName, LocalTime value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(LocalTime value) {
		hour = value.getHour();
		minute = value.getMinute();
		second = value.getSecond();

		return null;
	}

	@Override
	public int getHour() {
		return hour;
	}

	@Override
	public int getMinute() {
		return minute;
	}

	@Override
	public int getSecond() {
		return second;
	}

	public static class LocalTimeBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalTime;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new LocalTimeBluePrint(name, (LocalTime) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
