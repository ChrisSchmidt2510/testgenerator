package de.nvg.valuetracker.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.nvg.valuetracker.blueprint.BluePrint;
import de.nvg.valuetracker.blueprint.Type;

public final class ValueStorage {
	private static final ValueStorage INSTANCE = new ValueStorage();

	private List<BluePrint> methodParameters = new ArrayList<>();

	private BluePrint testObjectBluePrint;

	private ValueStorage() {
	}

	public static ValueStorage getInstance() {
		return INSTANCE;
	}

	public void addBluePrint(BluePrint bluePrint, Type type) {
		if (Type.TESTOBJECT == type) {
			testObjectBluePrint = bluePrint;
		} else if (Type.METHOD_PARAMETER == type) {
			methodParameters.add(bluePrint);
		}
	}

	public Collection<BluePrint> getMethodParameters() {
		return Collections.unmodifiableCollection(methodParameters);
	}

	public BluePrint getTestObject() {
		return testObjectBluePrint;
	}
}
