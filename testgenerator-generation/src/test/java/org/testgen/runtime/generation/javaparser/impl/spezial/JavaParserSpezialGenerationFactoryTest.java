package org.testgen.runtime.generation.javaparser.impl.spezial;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest.CustomInvocationHandler;
import org.testgen.runtime.generation.javaparser.impl.spezial.ProxySpezialObjectGenerationTest.Greeter;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint.LambdaExpressionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ProxyBluePrint.ProxyBluePrintFactory;

public class JavaParserSpezialGenerationFactoryTest {

	private JavaParserSpezialGenerationFactory spezialFactory = new JavaParserSpezialGenerationFactory();

	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	@Test
	public void testSpezialGenerationLambdaExpression() {
		Runnable run = () -> System.out.println("hello world");

		LambdaExpressionBluePrintFactory factory = new LambdaExpressionBluePrintFactory();
		BluePrint bluePrint = factory.createBluePrint("run", run, currentlyBuiltQueue, null);

		assertEquals(LambdaExpressionSpezialObjectGeneration.class, spezialFactory.of(bluePrint).getClass());
	}

	@Test
	public void testSpezialGenerationProxy() {
		Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class[] { Greeter.class }, new CustomInvocationHandler());

		ProxyBluePrintFactory factory = new ProxyBluePrintFactory();
		BluePrint bluePrint = factory.createBluePrint("proxy", proxy, currentlyBuiltQueue, null);

		assertEquals(ProxySpezialObjectGeneration.class, spezialFactory.of(bluePrint).getClass());
	}

}
