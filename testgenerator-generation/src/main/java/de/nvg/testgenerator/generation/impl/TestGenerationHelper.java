package de.nvg.testgenerator.generation.impl;

import java.util.Set;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.MethodHandles;
import de.nvg.testgenerator.TestgeneratorConstants;

public class TestGenerationHelper {

	public static ClassData getClassData(Object reference) {
		return MethodHandles.getStaticFieldValue(reference.getClass(), TestgeneratorConstants.CLASS_DATA);
	}

	public static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, TestgeneratorConstants.CALLED_FIELDS);
	}

}
