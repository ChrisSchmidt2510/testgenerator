package de.nvg.agent.classdata.testclasses;

import java.util.Arrays;
import java.util.List;

public class Switch {
	private String[] switchConstants = { "here", "there", "foo" };

	private List<String> list = Arrays.asList("foo");

	public String tableSwitch(int a) {
		for (String string : list) {
			System.out.println(string);
		}

		switch (a) {
		case 1:
			return switchConstants[0];
		case 2:
			return switchConstants[1];
		case 3:
			return switchConstants[2];
		case 4:
			return switchConstants[3];
		default:
			throw new IllegalArgumentException();
		}
	}

}
