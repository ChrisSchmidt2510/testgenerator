package de.nvg.agent.classdata.testclasses;

public class Value {

	private static final Integer DEFAULT_VALUE = 5;

	private Integer valueID;
	private Integer value;

	public Value(Integer value) {
		if (value == null) {
			this.value = DEFAULT_VALUE;
		} else {
			this.value = value;
		}

		this.valueID = this.value;
	}

	public Integer getValueID() {
		return valueID;
	}

	public void setValueID(Integer valueID) {
		if (valueID != null)
			this.valueID = valueID;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value != null ? value : DEFAULT_VALUE;
	}

}
