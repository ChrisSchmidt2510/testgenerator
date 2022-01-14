package org.testgen.agent.classdata.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Primitives {
	public static final String JVM_BYTE = "B";
	public static final String JVM_BOOLEAN = "Z";
	public static final String JVM_SHORT = "S";
	public static final String JVM_CHAR = "C";
	public static final String JVM_INT = "I";
	public static final String JVM_FLOAT = "F";
	public static final String JVM_DOUBLE = "D";
	public static final String JVM_LONG = "J";
	public static final String JVM_VOID = "V";

	public static final String JAVA_BYTE = "byte";
	public static final String JAVA_BOOLEAN = "boolean";
	public static final String JAVA_SHORT = "short";
	public static final String JAVA_CHAR = "char";
	public static final String JAVA_INT = "int";
	public static final String JAVA_FLOAT = "float";
	public static final String JAVA_DOUBLE = "double";
	public static final String JAVA_LONG = "long";

	public static final List<String> PRIMTIVE_JAVA_TYPES = Collections.unmodifiableList(Arrays.asList(JAVA_BYTE,
			JAVA_BOOLEAN, JAVA_CHAR, JAVA_SHORT, JAVA_INT, JAVA_FLOAT, JAVA_LONG, JAVA_DOUBLE));

	public static final List<String> PRIMTIVE_JVM_TYPES = Collections.unmodifiableList(
			Arrays.asList(JVM_BYTE, JVM_BOOLEAN, JVM_CHAR, JVM_SHORT, JVM_INT, JVM_FLOAT, JVM_LONG, JVM_DOUBLE));

	private Primitives() {
	}

	public static boolean isPrimitiveJVMDataType(String dataType) {
		return PRIMTIVE_JVM_TYPES.contains(dataType);
	}

	/**
	 * Load Instruction for loading an Integer
	 * @param type
	 * @return
	 */
	public static boolean isILoadPrimitive(String type) {
		return JVM_BOOLEAN.equals(type) || JVM_BYTE.equals(type) || JVM_CHAR.equals(type)//
				|| JVM_SHORT.equals(type) || JVM_INT.equals(type);
	}

}
