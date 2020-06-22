package de.nvg.valuetracker;

import org.junit.Assert;
import org.junit.Test;

import de.nvg.valuetracker.blueprint.ArrayBluePrint;
import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.simpletypes.SimpleBluePrintFactory;

public class ObjectValueTrackerTest {
	private final ObjectValueTracker valueTracker = new ObjectValueTracker();

	@Test
	public void testTrackArrays() {
		int[] array = new int[] { 10, 15, 20, 25, 30 };

		BluePrint bluePrint = valueTracker.trackValues(array, "array");

		Assert.assertTrue(bluePrint instanceof ArrayBluePrint);

		ArrayBluePrint arrayBluePrint = (ArrayBluePrint) bluePrint;
		BluePrint[] elements = arrayBluePrint.getElements();

		BluePrint[] expected = new BluePrint[5];
		expected[0] = SimpleBluePrintFactory.of("array1", 10);
		expected[1] = SimpleBluePrintFactory.of("array2", 15);
		expected[2] = SimpleBluePrintFactory.of("array3", 20);
		expected[3] = SimpleBluePrintFactory.of("array4", 25);
		expected[4] = SimpleBluePrintFactory.of("array5", 30);

		Assert.assertArrayEquals(expected, elements);
	}

	@Test
	public void testTrackMultiDimArrays() {
		int[][] array = new int[2][];
		int[] first = new int[] { 1, 2, 3, 4 };
		int[] second = new int[] { 10, 9, 8, 7, 6 };
		array[0] = first;
		array[1] = second;

		BluePrint bluePrint = valueTracker.trackValues(array, "array");

		Assert.assertTrue(bluePrint instanceof ArrayBluePrint);

		ArrayBluePrint arrayBluePrint = (ArrayBluePrint) bluePrint;
		BluePrint[] elements = arrayBluePrint.getElements();

		ArrayBluePrint firstRow = new ArrayBluePrint("array1", first, 4);
		firstRow.add(0, SimpleBluePrintFactory.of("array11", 1));
		firstRow.add(1, SimpleBluePrintFactory.of("array12", 2));
		firstRow.add(2, SimpleBluePrintFactory.of("array13", 3));
		firstRow.add(3, SimpleBluePrintFactory.of("array14", 4));

		ArrayBluePrint secondRow = new ArrayBluePrint("array2", second, 5);
		secondRow.add(0, SimpleBluePrintFactory.of("array21", 10));
		secondRow.add(1, SimpleBluePrintFactory.of("array22", 9));
		secondRow.add(2, SimpleBluePrintFactory.of("array23", 8));
		secondRow.add(3, SimpleBluePrintFactory.of("array24", 7));
		secondRow.add(4, SimpleBluePrintFactory.of("array25", 6));

		BluePrint[] expected = new BluePrint[] { firstRow, secondRow };

		Assert.assertArrayEquals(expected, elements);

	}
}
