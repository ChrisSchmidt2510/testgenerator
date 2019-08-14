package de.nvg.valuetracker.blueprint.simpletypes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NumberBluePrintTest {

	@Test
	public void test() {
		NumberBluePrint number = new NumberBluePrint("NumberBluePrint", 3);

		assertEquals("3", number.valueCreation());
	}

}
