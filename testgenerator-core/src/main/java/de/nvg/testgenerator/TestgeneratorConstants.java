package de.nvg.testgenerator;

public class TestgeneratorConstants {
	public static final String FIELDNAME_CLASS_DATA = "testgenerator$classData";
	public static final String FIELDNAME_CALLED_FIELDS = "testgenerator$calledFields";
	public static final String FIELDNAME_METHOD_SIGNATURE = "testgenerator$methodSignature";
	public static final String FIELDNAME_OBJECT_VALUE_TRACKER = "testgenerator$valueTracker";

	public static boolean isTestgeneratorField(String fieldName) {
		return fieldName.startsWith("testgenerator$");
	}

}
