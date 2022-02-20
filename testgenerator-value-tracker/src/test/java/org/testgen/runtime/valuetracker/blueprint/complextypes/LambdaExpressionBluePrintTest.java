package org.testgen.runtime.valuetracker.blueprint.complextypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.testgen.core.CurrentlyBuiltQueue;
import org.testgen.runtime.proxy.Proxified;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint.LambdaExpressionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class LambdaExpressionBluePrintTest {

	private LambdaExpressionBluePrintFactory factory = new LambdaExpressionBluePrintFactory();

	private CurrentlyBuiltQueue<BluePrint> currentlyBuiltQueue = new CurrentlyBuiltQueue<>();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		int a = 5;
		Runnable run = (Serializable & Runnable & Proxified) () -> System.out.println(a);

		Function<Integer, String> mapper = i -> i.toString();

		assertTrue(factory.createBluePrintForType(run));
		assertFalse(factory.createBluePrintForType(new Runnable() {

			@Override
			public void run() {
				System.out.println(a);
			}
		}));
		assertTrue(factory.createBluePrintForType(mapper));
		assertFalse(factory.createBluePrintForType(5));
		assertFalse(factory.createBluePrintForType(null));
		assertFalse(factory.createsSimpleBluePrint());
		assertEquals(1, factory.getPriority());
	}

	@Test
	public void testTrackFunctionalInterface() {
		Function<Integer, String> mapper = i -> i.toString();

		BluePrint bluePrint = factory.createBluePrint("mapper", mapper, currentlyBuiltQueue, null);

		assertTrue(bluePrint instanceof LambdaExpressionBluePrint);

		LambdaExpressionBluePrint fiBluePrint = (LambdaExpressionBluePrint) bluePrint;

		assertEquals("mapper", fiBluePrint.getName());
		assertTrue(fiBluePrint.isComplexType());
		assertEquals(Function.class, fiBluePrint.getInterfaceClass());
		assertEquals(1, fiBluePrint.numberOfParameters());
		assertTrue(fiBluePrint.getPreExecuteBluePrints().isEmpty());
	}

	@Test
	public void testTrackFunctionalInterfaceWithLocals() {
		int a = 5;
		Runnable run = (Runnable & Proxified) () -> System.out.println(a);

		BluePrint bluePrint = factory.createBluePrint("runnable", run, currentlyBuiltQueue,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		assertTrue(bluePrint instanceof LambdaExpressionBluePrint);

		LambdaExpressionBluePrint fiBluePrint = (LambdaExpressionBluePrint) bluePrint;

		assertEquals("runnable", fiBluePrint.getName());
		assertTrue(fiBluePrint.isComplexType());
		assertEquals(Runnable.class, fiBluePrint.getInterfaceClass());
		assertEquals(0, fiBluePrint.numberOfParameters());
		assertEquals(1, fiBluePrint.getPreExecuteBluePrints().size());

		List<BluePrint> expectedList = Arrays.asList(numFactory.createBluePrint("arg$1", 5));
		assertEquals(expectedList, fiBluePrint.getPreExecuteBluePrints());
	}

}
