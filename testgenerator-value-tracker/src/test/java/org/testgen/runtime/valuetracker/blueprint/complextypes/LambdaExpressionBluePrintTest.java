package org.testgen.runtime.valuetracker.blueprint.complextypes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.proxy.Proxified;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.BluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.LambdaExpressionBluePrint.LambdaExpressionBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class LambdaExpressionBluePrintTest {

	private LambdaExpressionBluePrintFactory factory = new LambdaExpressionBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	private NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

	@Test
	public void testBluePrintFactory() {
		int a = 5;
		Runnable run = (Serializable & Runnable & Proxified) () -> System.out.println(a);

		Function<Integer, String> mapper = i -> i.toString();

		Assert.assertTrue(factory.createBluePrintForType(run));
		Assert.assertFalse(factory.createBluePrintForType(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(a);
			}
		}));
		Assert.assertTrue(factory.createBluePrintForType(mapper));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}
	
	@Test
	public void testTrackFunctionalInterface() {
		Function<Integer, String> mapper = i -> i.toString();
		
		BluePrint bluePrint = factory.createBluePrint("mapper", mapper, currentlyBuildedBluePrints, null);
	
		Assert.assertTrue(bluePrint instanceof LambdaExpressionBluePrint);
		
		LambdaExpressionBluePrint fiBluePrint = (LambdaExpressionBluePrint) bluePrint;
		
		Assert.assertEquals("mapper", fiBluePrint.getName());
		Assert.assertTrue(fiBluePrint.isComplexType());
		Assert.assertEquals(Function.class, fiBluePrint.getInterfaceClass());
		Assert.assertEquals(1, fiBluePrint.numberOfParameters());
		Assert.assertTrue(fiBluePrint.getPreExecuteBluePrints().isEmpty());
	}

	@Test
	public void testTrackFunctionalInterfaceWithLocals() {
		int a = 5;
		Runnable run = (Runnable & Proxified) () -> System.out.println(a);

		BluePrint bluePrint = factory.createBluePrint("runnable", run, currentlyBuildedBluePrints,
				(name, value) -> numFactory.createBluePrint(name, (Number) value));

		Assert.assertTrue(bluePrint instanceof LambdaExpressionBluePrint);

		LambdaExpressionBluePrint fiBluePrint = (LambdaExpressionBluePrint) bluePrint;

		Assert.assertEquals("runnable", fiBluePrint.getName());
		Assert.assertTrue(fiBluePrint.isComplexType());
		Assert.assertEquals(Runnable.class, fiBluePrint.getInterfaceClass());
		Assert.assertEquals(0, fiBluePrint.numberOfParameters());
		Assert.assertEquals(1, fiBluePrint.getPreExecuteBluePrints().size());

		List<BluePrint> expectedList = Arrays.asList(numFactory.createBluePrint("arg$1", 5));
		Assert.assertEquals(expectedList, fiBluePrint.getPreExecuteBluePrints());
	}
	
}
