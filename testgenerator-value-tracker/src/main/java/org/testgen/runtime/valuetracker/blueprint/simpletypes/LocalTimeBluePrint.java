package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrintFactory;
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

	public static class LocalTimeBluePrintFactory implements SimpleBluePrintFactory<LocalTime> {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof LocalTime;
		}

		@Override
		public SimpleBluePrint<LocalTime> createBluePrint(String name, LocalTime value) {
			return new LocalTimeBluePrint(name, value);
		}

	}

}
