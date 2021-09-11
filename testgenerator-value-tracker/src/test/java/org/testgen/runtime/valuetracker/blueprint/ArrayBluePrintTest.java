package org.testgen.runtime.valuetracker.blueprint;

import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint.ArrayBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

public class ArrayBluePrintTest {

	private ArrayBluePrintFactory factory = new ArrayBluePrintFactory();
	private NumberBluePrintFactory numberFactory = new NumberBluePrintFactory();
	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();

	@Test
	public void testBluePrintFactory() {
		Assert.assertTrue(factory.createBluePrintForType(new int[5]));
		Assert.assertFalse(factory.createBluePrintForType(5));
		Assert.assertFalse(factory.createBluePrintForType(null));
		Assert.assertFalse(factory.createsSimpleBluePrint());
		Assert.assertEquals(1, factory.getPriority());
	}

	@Test
	public void testTrackArrays() {

		int[] array = new int[] { 10, 15, 20, 25, 30 };

		BluePrint bluePrint = factory.createBluePrint("array", array, currentlyBuildedBluePrints,
				(name, value) -> numberFactory.createBluePrint(name, (Number) value));

		Assert.assertTrue(bluePrint.isArrayBluePrint());

		ArrayBluePrint arrayBluePrint = bluePrint.castToArrayBluePrint();
		Assert.assertEquals(int[].class, arrayBluePrint.getType());
		Assert.assertEquals(int.class, arrayBluePrint.getBaseType());
		Assert.assertEquals(1, arrayBluePrint.getDimensions());
		Assert.assertEquals(5, arrayBluePrint.size());
		Assert.assertTrue(arrayBluePrint.isComplexType());
		
		BluePrint[] elements = arrayBluePrint.getElements();

		BluePrint[] expected = new BluePrint[5];
		expected[0] = numberFactory.createBluePrint("arrayElement", 10);
		expected[1] = numberFactory.createBluePrint("arrayElement", 15);
		expected[2] = numberFactory.createBluePrint("arrayElement", 20);
		expected[3] = numberFactory.createBluePrint("arrayElement", 25);
		expected[4] = numberFactory.createBluePrint("arrayElement", 30);

		Assert.assertEquals("array", arrayBluePrint.getName());
		Assert.assertArrayEquals(expected, elements);
	}

	@Test
	public void testTrackMultiDimArrays() {
		int[][] array = new int[2][];
		int[] first = new int[] { 1, 2, 3, 4 };
		int[] second = new int[] { 10, 9, 8, 7, 6 };
		array[0] = first;
		array[1] = second;

		BiFunction<String, Object, BluePrint> numberCallBack = (name, value) -> numberFactory.createBluePrint(name,
				(Number) value);

		BiFunction<String, Object, BluePrint> callBack = (name, value) -> factory.createBluePrintForType(value)
				? factory.createBluePrint(name, value, currentlyBuildedBluePrints, numberCallBack)
				: numberCallBack.apply(name, value);

		BluePrint bluePrint = factory.createBluePrint("array", array, currentlyBuildedBluePrints, callBack);
		Assert.assertTrue(bluePrint.isArrayBluePrint());

		ArrayBluePrint arrayBluePrint = bluePrint.castToArrayBluePrint();
		Assert.assertEquals(int[][].class, arrayBluePrint.getType());
		Assert.assertEquals(int.class, arrayBluePrint.getBaseType());
		Assert.assertEquals(2, arrayBluePrint.getDimensions());
		Assert.assertEquals(2, arrayBluePrint.size());
		Assert.assertTrue(arrayBluePrint.isComplexType());
		Assert.assertEquals(2, arrayBluePrint.getPreExecuteBluePrints().size());
		
		BluePrint[] elements = arrayBluePrint.getElements();

		ArrayBluePrint firstRow = new ArrayBluePrint("arrayElement", first, 4);
		firstRow.add(0, numberFactory.createBluePrint("arrayElementElement", 1));
		firstRow.add(1, numberFactory.createBluePrint("arrayElementElement", 2));
		firstRow.add(2, numberFactory.createBluePrint("arrayElementElement", 3));
		firstRow.add(3, numberFactory.createBluePrint("arrayElementElement", 4));

		ArrayBluePrint secondRow = new ArrayBluePrint("arrayElement", second, 5);
		secondRow.add(0, numberFactory.createBluePrint("arrayElementElement", 10));
		secondRow.add(1, numberFactory.createBluePrint("arrayElementElement", 9));
		secondRow.add(2, numberFactory.createBluePrint("arrayElementElement", 8));
		secondRow.add(3, numberFactory.createBluePrint("arrayElementElement", 7));
		secondRow.add(4, numberFactory.createBluePrint("arrayElementElement", 6));

		BluePrint[] expected = new BluePrint[] { firstRow, secondRow };

		
		Assert.assertEquals("array", arrayBluePrint.getName());
		Assert.assertArrayEquals(expected, elements);
	}
}
