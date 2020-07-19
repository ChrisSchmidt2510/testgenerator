package de.nvg.testgenerator.generation.impl;

import java.util.Set;

import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.ClassDataFactory;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;

public final class TestGenerationHelper {

	private TestGenerationHelper() {
	}

	public static ClassData getClassData(Object reference) {
		return ClassDataFactory.getInstance().getClassData(reference.getClass());
	}

	public static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, TestgeneratorConstants.FIELDNAME_CALLED_FIELDS);
	}
}
