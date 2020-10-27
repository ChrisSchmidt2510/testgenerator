package org.testgen.agent.classdata.testclasses;

import java.util.Calendar;

public class Value {

	private static final Integer DEFAULT_VALUE = 5;

	private Integer valueID;
	private Integer value;
	private Calendar calendar;

	@SuppressWarnings("unused")
	private int smallValue;

	public Value() {
		this(null, null);
	}

	public Value(Integer value, Calendar calendar) {
		if (value == null) {
			this.value = DEFAULT_VALUE;
		} else {
			this.value = value;
		}
		this.calendar = calendar != null ? (Calendar) calendar.clone() : null;

		this.valueID = this.value;
	}

	public Integer getValueID() {
		return valueID;
	}

	public void setValueID(Integer valueID) {
		if (valueID != null) {
			this.valueID = valueID;
		}
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value != null ? value : DEFAULT_VALUE;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public void setSmallValue(float value) {
		smallValue = (int) value;
	}

}
