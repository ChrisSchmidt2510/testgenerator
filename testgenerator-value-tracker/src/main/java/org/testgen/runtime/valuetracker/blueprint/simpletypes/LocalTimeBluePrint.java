package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.time.LocalTime;
import java.util.Objects;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;
import org.testgen.runtime.valuetracker.blueprint.factories.SimpleBluePrintFactory;

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

	@Override
	public int hashCode() {
		return Objects.hash(name, hour, minute, second);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LocalTimeBluePrint))
			return false;
		LocalTimeBluePrint other = (LocalTimeBluePrint) obj;
		return Objects.equals(name, other.name) && hour == other.hour && minute == other.minute
				&& second == other.second;
	}
	
	@Override
	public String toString() {
		return String.format("Field: %s Value: %d:%d:%d", name, hour, minute, second);
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
