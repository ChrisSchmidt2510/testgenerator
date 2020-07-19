package org.testgen.core;

public final class TestgeneratorConstants {
	public static final String FIELDNAME_CLASS_DATA = "testgenerator$classData";
	public static final String FIELDNAME_CALLED_FIELDS = "testgenerator$calledFields";
	public static final String FIELDNAME_METHOD_PARAMETER_TABLE = "testgenerator$methodParameterTable";
	public static final String FIELDNAME_OBJECT_VALUE_TRACKER = "testgenerator$valueTracker";

	private TestgeneratorConstants() {
	}

	public static boolean isTestgeneratorField(String fieldName) {
		return fieldName.startsWith("testgenerator$");
	}

}
