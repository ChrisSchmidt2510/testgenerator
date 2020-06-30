package de.nvg.valuetracker.storage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.Type;

public final class ValueStorage {
	private static final ValueStorage INSTANCE = new ValueStorage();

	private final Deque<TestData> testData = new ArrayDeque<>();

	private ValueStorage() {
	}

	public static ValueStorage getInstance() {
		return INSTANCE;
	}

	public void addBluePrint(BluePrint bluePrint, Type type) {
		if (Type.TESTOBJECT == type) {
			testData.peek().testObjectBluePrint = bluePrint;
		} else if (Type.METHOD_PARAMETER == type) {
			testData.peek().methodParameters.add(bluePrint);
		} else if (Type.PROXY == type) {
			testData.peek().proxyObjects.add(bluePrint);
		}
	}

	public void pushNewTestData() {
		testData.push(new TestData());
	}

	public void popAndResetTestData() {
		TestData executedTestData = testData.pop();
		// reset build flag cause some BluePrints could be used in another TestData
		executedTestData.testObjectBluePrint.resetBuildState();
		executedTestData.methodParameters.forEach(BluePrint::resetBuildState);
		executedTestData.proxyObjects.forEach(BluePrint::resetBuildState);

	}

	public Collection<BluePrint> getMethodParameters() {
		return Collections.unmodifiableCollection(testData.peek().methodParameters);
	}

	public Collection<BluePrint> getProxyObjects() {
		return Collections.unmodifiableCollection(testData.peek().proxyObjects);
	}

	public BluePrint getTestObject() {
		return testData.peek().testObjectBluePrint;
	}

	private class TestData {
		private List<BluePrint> methodParameters = new ArrayList<>();

		private List<BluePrint> proxyObjects = new ArrayList<>();

		private BluePrint testObjectBluePrint;
	}

}
