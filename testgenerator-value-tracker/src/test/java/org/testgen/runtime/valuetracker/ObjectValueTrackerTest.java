package org.testgen.runtime.valuetracker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
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

		BluePrint bluePrint = valueTracker.trackValues(list, "list");

		BluePrint copyBluePrint = valueTracker.trackValues(list, "sameList");

		Assert.assertEquals(bluePrint.getName(), copyBluePrint.getName());
		Assert.assertTrue(bluePrint == copyBluePrint);
	}

	@Test
	public void testTrack() {
		ValueStorage.getInstance().pushNewTestData();

		valueTracker.track(5, "value", Type.METHOD_PARAMETER);
		List<BluePrint> methodParameters = ValueStorage.getInstance().getMethodParameters();

		Assert.assertEquals(1, methodParameters.size());
		Assert.assertEquals("5", ((NumberBluePrint) methodParameters.get(0)).valueCreation());

		valueTracker.track("hello", "value", Type.TESTOBJECT);
		Assert.assertEquals("hello",
				ValueStorage.getInstance().getTestObject().castToSimpleBluePrint().valueCreation());

		Assert.assertThrows("invalid Type: Proxy", IllegalArgumentException.class,
				() -> valueTracker.track(10, "value", Type.PROXY));

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
		Assert.assertArrayEquals(new Class<?>[] { ProxyTest.class }, proxyBluePrint.getInterfaceClasses());
		
		List<ProxyBluePrint> proxyObjects = ValueStorage.getInstance().getProxyObjects();
		Assert.assertEquals(1, proxyObjects.size());
		Assert.assertTrue(proxyObjects.get(0) == proxyBluePrint);
	}

	@Test
	public void testGetProxyValue() {
		IntegerProxy intProxy = new IntegerProxy(5, this, "value", Integer.TYPE);
		Assert.assertEquals(5, ObjectValueTracker.getProxyValue(intProxy));

		IntegerProxy byteProxy = new IntegerProxy(10, this, "value", Byte.TYPE);
		Assert.assertEquals((byte) 10, ObjectValueTracker.getProxyValue(byteProxy));

		IntegerProxy charProxy = new IntegerProxy('C', this, "value", Character.TYPE);
		Assert.assertEquals('C', ObjectValueTracker.getProxyValue(charProxy));

		IntegerProxy shortProxy = new IntegerProxy(255, this, "value", Short.TYPE);
		Assert.assertEquals((short) 255, ObjectValueTracker.getProxyValue(shortProxy));

		ReferenceProxy<String> referenceProxy = new ReferenceProxy<String>("hello", this, "value", String.class);
		Assert.assertEquals("hello", ObjectValueTracker.getProxyValue(referenceProxy));

		DoubleProxy doubleProxy = new DoubleProxy(3.1415, this, "value");
		Assert.assertEquals(3.1415, ObjectValueTracker.getProxyValue(doubleProxy));

		FloatProxy floatProxy = new FloatProxy(3.4f, this, "value");
		Assert.assertEquals(3.4f, ObjectValueTracker.getProxyValue(floatProxy));

		LocalDate date = LocalDate.now();
		Assert.assertEquals(date, ObjectValueTracker.getProxyValue(date));
	}

}
