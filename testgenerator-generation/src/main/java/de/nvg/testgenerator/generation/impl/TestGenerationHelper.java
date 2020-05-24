package de.nvg.testgenerator.generation.impl;

import java.util.Map;
import java.util.Set;

import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;

import de.nvg.runtime.classdatamodel.ClassData;
import de.nvg.runtime.classdatamodel.FieldData;
import de.nvg.runtime.classdatamodel.SignatureData;

public final class TestGenerationHelper {

	private TestGenerationHelper() {
	}

	public static ClassData getClassData(Object reference) {
		return MethodHandles.getStaticFieldValue(reference.getClass(), TestgeneratorConstants.FIELDNAME_CLASS_DATA);
	}

	public static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, TestgeneratorConstants.FIELDNAME_CALLED_FIELDS);
	}

	public static Map<Integer, SignatureData> getMethodSignature(Object reference) {
		return MethodHandles.getFieldValue(reference, TestgeneratorConstants.FIELDNAME_METHOD_SIGNATURE);
	}

}
