package org.testgen.runtime.valuetracker.blueprint.complextypes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class ProxyBluePrintTest {

	private ProxyTest proxyInstance;

	private InvocationHandler handler;

	private ProxyBluePrintFactory factory = new ProxyBluePrintFactory();

	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	@BeforeEach
	public void init() {
		handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return "Hello " + args[0];
			}
		};

		proxyInstance = (ProxyTest) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { ProxyTest.class }, handler);
	}

	@Test
	public void testBluePrintFactory() {
		assertTrue(factory.createBluePrintForType(proxyInstance));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertFalse(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testProxyBluePrint() {
		BluePrint bluePrint = factory.createBluePrint("proxy", proxyInstance, currentlyBuiltQueue,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		assertTrue(bluePrint instanceof ProxyBluePrint);

		ProxyBluePrint proxy = (ProxyBluePrint) bluePrint;
		assertArrayEquals(new Class<?>[] { ProxyTest.class }, proxy.getInterfaceClasses());
		assertTrue(proxy.isComplexType());
		assertThrows(UnsupportedOperationException.class, () -> proxy.getPreExecuteBluePrints());
		assertEquals(handler.getClass(), proxy.getInvocationHandler().getClass());

		Method method = null;

		try {
			method = ProxyTest.class.getMethod("multiplier", Integer.TYPE, Integer.TYPE);
		} catch (NoSuchMethodException | SecurityException e) {
			fail(e);
		}

		proxy.addProxyResult(method, 11);
		assertEquals(Arrays.asList(new SimpleEntry<>(method, numFactory.createBluePrint("multiplier", 11))),
				proxy.getProxyResults());
	}

	public interface ProxyTest {

		public int multiplier(int a, int b);
	}
}
