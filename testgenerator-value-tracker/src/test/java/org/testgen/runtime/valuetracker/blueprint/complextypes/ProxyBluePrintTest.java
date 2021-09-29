package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class ProxyBluePrintTest {

	private ProxyTest proxyInstance;

	private InvocationHandler handler;

	private ProxyBluePrintFactory factory = new ProxyBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	@Before
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
		Assert.assertTrue(factory.createBluePrintForType(proxyInstance));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testProxyBluePrint() {
		BluePrint bluePrint = factory.createBluePrint("proxy", proxyInstance, currentlyBuildedBluePrints,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		Assert.assertTrue(bluePrint instanceof ProxyBluePrint);

		ProxyBluePrint proxy = (ProxyBluePrint) bluePrint;
		Assert.assertArrayEquals(new Class<?>[] { ProxyTest.class }, proxy.getInterfaceClasses());
		Assert.assertTrue(proxy.isComplexType());
		Assert.assertTrue(proxy.getPreExecuteBluePrints().isEmpty());
		Assert.assertEquals(handler.getClass(), proxy.getInvocationHandler().getClass());

		proxy.addProxyResult("value", 11);
		Assert.assertEquals(Arrays.asList(numFactory.createBluePrint("value", 11)), proxy.getPreExecuteBluePrints());
	}

	public interface ProxyTest {

		public String greet(String name);
	}
}
