package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.function.BiFunction;

import javax.xml.datatype.XMLGregorianCalendar;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.BluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.DateBluePrint;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.TimeBluePrint;

public class XMLGregorianCalendarBluePrint extends SimpleBluePrint<XMLGregorianCalendar>
		implements DateBluePrint, TimeBluePrint {

	private int year;
	private int month;
	private int day;

	private int hour;
	private int minute;
	private int second;

	private int millis;

	private int timezone;

	public XMLGregorianCalendarBluePrint(String fieldName, XMLGregorianCalendar value) {
		super(fieldName, value);
	}

	@Override
	protected String createValue(XMLGregorianCalendar value) {
		year = value.getYear();
		month = value.getMonth();
		day = value.getDay();

		hour = value.getHour();
		minute = value.getMinute();
		second = value.getSecond();
		millis = value.getMillisecond();

		timezone = value.getTimezone();
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
	public int getDay() {
		return day;
	}

	@Override
	public int getMonth() {
		return month;
	}

	@Override
	public int getYear() {
		return year;
	}

	public int getMillisecond() {
		return millis;
	}

	public int getTimezone() {
		return timezone;
	}

	public static class XMLGregorianCalendarBluePrintFactory implements BluePrintFactory {

		@Override
		public boolean createBluePrintForType(Object value) {
			return value instanceof XMLGregorianCalendar;
		}

		@Override
		public BluePrint createBluePrint(String name, Object value,
				BiFunction<String, Object, BluePrint> childCallBack) {
			return new XMLGregorianCalendarBluePrint(name, (XMLGregorianCalendar) value);
		}

		@Override
		public boolean createsSimpleBluePrint() {
			return true;
		}

	}

}
