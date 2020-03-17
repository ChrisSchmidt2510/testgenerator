package de.nvg.testgenerator.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Primitives {

	public static final String JVM_BYTE = "B";
	public static final String JVM_BOOLEAN = "Z";
	public static final String JVM_SHORT = "S";
	public static final String JVM_CHAR = "C";
	public static final String JVM_INT = "I";
	public static final String JVM_FLOAT = "F";
	public static final String JVM_DOUBLE = "D";
	public static final String JVM_LONG = "L";
	public static final String JVM_VOID = "V";

	public static final String JAVA_BYTE = "byte";
	public static final String JAVA_BOOLEAN = "boolean";
	public static final String JAVA_SHORT = "short";
	public static final String JAVA_CHAR = "char";
	public static final String JAVA_INT = "int";
	public static final String JAVA_FLOAT = "float";
	public static final String JAVA_DOUBLE = "double";
	public static final String JAVA_LONG = "long";

	public static boolean isPrimitiveDataType(String dataType) {
		return !dataType.endsWith(";");
	}

	public static final List<String> PRIMTIVE_JAVA_TYPES = Collections.unmodifiableList(Arrays.asList(JAVA_BYTE,
			JAVA_BOOLEAN, JAVA_CHAR, JAVA_SHORT, JAVA_INT, JAVA_FLOAT, JAVA_LONG, JAVA_DOUBLE));

}
