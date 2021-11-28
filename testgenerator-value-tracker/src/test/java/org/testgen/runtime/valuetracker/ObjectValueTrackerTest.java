package org.testgen.runtime.valuetracker;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.testgen.runtime.proxy.impl.DoubleProxy;
import org.testgen.runtime.proxy.impl.FloatProxy;
import org.testgen.runtime.proxy.impl.IntegerProxy;
import org.testgen.runtime.proxy.impl.ReferenceProxy;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.Type;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrintTest.ProxyTest;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

public class ObjectValueTrackerTest {

	private final ObjectValueTracker valueTracker = ObjectValueTracker.getInstance();

	@Test
	public void testTrackValuesCache() {
		List<Integer> list = Arrays.asList(1, 2, 3);

		BluePrint bluePrint = valueTracker.trackNormalValue(list, "list");

		BluePrint copyBluePrint = valueTracker.trackNormalValue(list, "sameList");

		assertEquals(bluePrint.getName(), copyBluePrint.getName());
		assertTrue(bluePrint == copyBluePrint);
	}

	@Test
	public void testTrack() {
		ValueStorage.getInstance().pushNewTestData();

		valueTracker.track(5, "value", Type.METHOD_PARAMETER);
		List<BluePrint> methodParameters = ValueStorage.getInstance().getMethodParameters();

		assertEquals(1, methodParameters.size());
		assertEquals("5", ((NumberBluePrint) methodParameters.get(0)).valueCreation());

		valueTracker.track("hello", "value", Type.TESTOBJECT);
		assertEquals("hello",
				ValueStorage.getInstance().getTestObject().castToSimpleBluePrint().valueCreation());

		assertThrows(IllegalArgumentException.class,
				() -> valueTracker.track(10, "value", Type.PROXY),"invalid Type: Proxy");

		ValueStorage.getInstance().popAndResetTestData();
	}

	@Test
	public void testTrackProxy() {
		ValueStorage.getInstance().pushNewTestData();

		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		};

		Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { ProxyTest.class }, handler);

		ProxyBluePrint proxyBluePrint = valueTracker.trackProxy(proxy, "proxy");
		assertArrayEquals(new Class<?>[] { ProxyTest.class }, proxyBluePrint.getInterfaceClasses());
		
		List<ProxyBluePrint> proxyObjects = ValueStorage.getInstance().getProxyObjects();
		assertEquals(1, proxyObjects.size());
		assertTrue(proxyObjects.get(0) == proxyBluePrint);
	}

	@Test
	public void testGetProxyValue() {
		IntegerProxy intProxy = new IntegerProxy(5, this, "value", Integer.TYPE);
		assertEquals(5, ObjectValueTracker.getTestgeneratorProxyValue(intProxy));

		IntegerProxy byteProxy = new IntegerProxy(10, this, "value", Byte.TYPE);
		assertEquals((byte) 10, ObjectValueTracker.getTestgeneratorProxyValue(byteProxy));

		IntegerProxy charProxy = new IntegerProxy('C', this, "value", Character.TYPE);
		assertEquals('C', ObjectValueTracker.getTestgeneratorProxyValue(charProxy));

		IntegerProxy shortProxy = new IntegerProxy(255, this, "value", Short.TYPE);
		assertEquals((short) 255, ObjectValueTracker.getTestgeneratorProxyValue(shortProxy));

		ReferenceProxy<String> referenceProxy = new ReferenceProxy<String>("hello", this, "value", String.class);
		assertEquals("hello", ObjectValueTracker.getTestgeneratorProxyValue(referenceProxy));

		DoubleProxy doubleProxy = new DoubleProxy(3.1415, this, "value");
		assertEquals(3.1415, ObjectValueTracker.getTestgeneratorProxyValue(doubleProxy));

		FloatProxy floatProxy = new FloatProxy(3.4f, this, "value");
		assertEquals(3.4f, ObjectValueTracker.getTestgeneratorProxyValue(floatProxy));

		LocalDate date = LocalDate.now();
		assertEquals(date, ObjectValueTracker.getTestgeneratorProxyValue(date));
	}

}
