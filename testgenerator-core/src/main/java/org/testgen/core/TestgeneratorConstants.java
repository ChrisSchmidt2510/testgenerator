package org.testgen.core;

public final class TestgeneratorConstants {
	public static final String FIELDNAME_CALLED_FIELDS = "testgenerator$calledFields";
	public static final String FIELDNAME_METHOD_PARAMETER_TABLE = "testgenerator$methodParameterTable";
	public static final String FIELDNAME_OBJECT_VALUE_TRACKER = "testgenerator$valueTracker";

	public static final String CLASS_DATA_METHOD_GET_CLASS_DATA_DESC = "()Lorg/testgen/runtime/classdata/model/ClassData;";

	public static final String CLASS_DATA_METHOD_GET_CLASS_DATA = "getTestgenerator$$ClassData";

	public static final String PROXIFIED_CLASSNAME = "org/testgen/runtime/proxy/Proxified";

	private TestgeneratorConstants() {
	}

	public static boolean isTestgeneratorField(String fieldName) {
		return fieldName.startsWith("testgenerator$");
	}

}
