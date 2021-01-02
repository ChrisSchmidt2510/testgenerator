package org.testgen.runtime.generation.api;

import java.util.Set;

import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.runtime.classdata.access.ClassDataAccess;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.FieldData;

public class GenerationHelper {

	public static ClassData getClassData(Object reference) {
		return ClassDataAccess.getClassData(reference.getClass());
	}

	public static Set<FieldData> getCalledFields(Object reference) {
		return MethodHandles.getFieldValue(reference, TestgeneratorConstants.FIELDNAME_CALLED_FIELDS);
	}

}
