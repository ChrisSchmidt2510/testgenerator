package org.testgen.agent.classdata.testclasses;

import java.time.LocalDate;
import java.util.List;

public abstract class ValueTrackerTransformerHelper {

	public void addPrimitiveToList(List<Integer> values, int anotherValue) {
		values.add(anotherValue);
	}

	public static boolean isBefore(LocalDate first, LocalDate second) {
		return first.isBefore(second);
	}

	public String cutString(String value) {
		if (value == null)
			throw new NullPointerException();

		if (value.isEmpty())
			return value;

		return value.trim();
	}

	public LocalDate finallyClause() {
		try {
			return LocalDate.parse("2021.12.31");
		} finally {
			System.out.println("Hello World");
		}
	}

	@SuppressWarnings("unused")
	private void negativeTest() {

	}

	protected void negativeTest2() {
	}

	public abstract void negativeTest3();

	void positiveTest() {
	}
}
