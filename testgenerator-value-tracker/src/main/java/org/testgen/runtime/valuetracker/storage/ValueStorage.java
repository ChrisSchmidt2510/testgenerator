package org.testgen.runtime.valuetracker.storage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.Type;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;

public final class ValueStorage {
	private static final ValueStorage INSTANCE = new ValueStorage();

	private final Deque<TestData> testData = new ArrayDeque<>();

	private ValueStorage() {
	}

	public static ValueStorage getInstance() {
		return INSTANCE;
	}

	public void addBluePrint(BluePrint bluePrint, Type type) {
		if (Type.TESTOBJECT == type)
			testData.peek().testObjectBluePrint = bluePrint;

		else if (Type.METHOD_PARAMETER == type)
			testData.peek().methodParameters.add(bluePrint);

		else
			throw new IllegalArgumentException("invalid Type: " + type);
	}

	public void addProxyBluePrint(ProxyBluePrint proxy) {
		testData.peek().proxyObjects.add(proxy);
	}

	public void pushNewTestData() {
		testData.push(new TestData());
	}

	public void popAndResetTestData() {
		TestData executedTestData = testData.pop();
		// reset build flag cause some BluePrints could be used in another TestData
		if (executedTestData.testObjectBluePrint != null)
			executedTestData.testObjectBluePrint.resetBuildState();

		executedTestData.methodParameters.forEach(BluePrint::resetBuildState);
		executedTestData.proxyObjects.forEach(BluePrint::resetBuildState);
	}

	public List<BluePrint> getMethodParameters() {
		return Collections.unmodifiableList(testData.peek().methodParameters);
	}

	public List<ProxyBluePrint> getProxyObjects() {
		return Collections.unmodifiableList(testData.peek().proxyObjects);
	}

	public BluePrint getTestObject() {
		return testData.peek().testObjectBluePrint;
	}

	private class TestData {
		private List<BluePrint> methodParameters = new ArrayList<>();

		private List<ProxyBluePrint> proxyObjects = new ArrayList<>();

		private BluePrint testObjectBluePrint;
	}

}
