	package org.testgen.runtime.valuetracker.storage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		if (Type.TESTOBJECT == type) {
			testData.peek().testObjectBluePrint = bluePrint;
		} else if (Type.METHOD_PARAMETER == type) {
			testData.peek().methodParameters.add(bluePrint);
		}
	}

	public void addProxyBluePrint(ProxyBluePrint proxy, BluePrint bluePrint) {
		Map<ProxyBluePrint, List<BluePrint>> proxyObjects = testData.peek().proxyObjects;

		if (proxyObjects.containsKey(proxy))
			proxyObjects.get(proxy).add(bluePrint);
		else
			proxyObjects.put(proxy, new ArrayList<>(Collections.singletonList(bluePrint)));
	}

	public void pushNewTestData() {
		testData.push(new TestData());
	}

	public void popAndResetTestData() {
		TestData executedTestData = testData.pop();
		// reset build flag cause some BluePrints could be used in another TestData
		executedTestData.testObjectBluePrint.resetBuildState();
		executedTestData.methodParameters.forEach(BluePrint::resetBuildState);
		executedTestData.proxyObjects.values().forEach(list -> list.forEach(BluePrint::resetBuildState));

	}

	public List<BluePrint> getMethodParameters() {
		return Collections.unmodifiableList(testData.peek().methodParameters);
	}

	public Map<ProxyBluePrint, List<BluePrint>> getProxyObjects() {
		return Collections.unmodifiableMap(testData.peek().proxyObjects);
	}

	public BluePrint getTestObject() {
		return testData.peek().testObjectBluePrint;
	}

	private class TestData {
		private List<BluePrint> methodParameters = new ArrayList<>();

		private Map<ProxyBluePrint, List<BluePrint>> proxyObjects = new HashMap<>();

		private BluePrint testObjectBluePrint;
	}

}
