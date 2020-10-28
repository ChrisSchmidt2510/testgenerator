package org.testgen.runtime.valuetracker.blueprint.simpletypes;

import java.util.Collections;
import java.util.List;

import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;

public class ClassBluePrint extends SimpleBluePrint<Class<?>> {

	ClassBluePrint(String fieldname, Class<?> value) {
		super(fieldname, value);
	}

	protected String createValue(Class<?> value) {
		return "$T.class";
	}

	public List<Class<?>> getReferenceClasses() {
		return Collections.singletonList(value);
	}

}
