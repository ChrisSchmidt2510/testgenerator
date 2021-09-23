package org.testgen.runtime.valuetracker.blueprint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.valuetracker.blueprint.ProxyBluePrint.ProxyBluePrintFactory;

public class ProxyBluePrintTest {

	private ProxyTest proxyInstance;

	private ProxyBluePrintFactory factory = new ProxyBluePrintFactory();

	@Before
	public void init() {
		InvocationHandler handler = new InvocationHandler() {

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
		BluePrint bluePrint = factory.createBluePrint("proxy", proxyInstance, null, null);

		Assert.assertTrue(bluePrint instanceof ProxyBluePrint);

		ProxyBluePrint proxy = (ProxyBluePrint) bluePrint;
		Assert.assertEquals(ProxyTest.class, proxy.getInterfaceClass());
		Assert.assertThrows(UnsupportedOperationException.class, () -> proxy.getPreExecuteBluePrints());
	}

	public interface ProxyTest {

		public String greet(String name);
	}
}
