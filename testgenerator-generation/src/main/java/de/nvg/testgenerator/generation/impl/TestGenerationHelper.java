package de.nvg.testgenerator.generation.impl;

import java.util.Set;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.testgenerator.MethodHandles;

public class TestGenerationHelper {
	private static final String FIELD_NAME_CLASS_DATA = "classData";
	private static final String FIELD_NAME_CALLED_FIELDS = "calledFields";

	public static ClassData getClassData(Object reference) {
		return MethodHandles.getStaticFieldValue(reference.getClass(), FIELD_NAME_CLASS_DATA);
	}

	public static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, FIELD_NAME_CALLED_FIELDS);
	}

}
