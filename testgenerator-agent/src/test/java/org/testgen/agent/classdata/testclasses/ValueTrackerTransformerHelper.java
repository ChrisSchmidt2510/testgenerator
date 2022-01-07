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

	private void negativeTest() {

	}

	protected void negativeTest2() {
	}

	public abstract void negativeTest3();

	void positiveTest() {
	}
}
