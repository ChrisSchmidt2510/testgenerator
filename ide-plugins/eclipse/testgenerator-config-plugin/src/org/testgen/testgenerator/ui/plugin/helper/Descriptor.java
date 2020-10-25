package org.testgen.testgenerator.ui.plugin.helper;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.testgen.testgenerator.ui.plugin.TestgeneratorActivator;

@SuppressWarnings("restriction")
public final class Descriptor {

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
	public static final String JAVA_VOID = "void";

	public static final String ARRAY_BRACKET = "[";

	private final IJavaProject project;

	private Descriptor(IJavaProject project) {
		this.project = project;
	}

	public static Descriptor getInstance(IJavaProject project) {
		return new Descriptor(project);
	}

	public String getJvmFullQualifiedName(String argument, IType type) {
		try {
			String arrayDimensions = arrayCheck(argument);

			String typeName = JavaModelUtil.getResolvedTypeName(argument, type);

			String primitiveType = primitiveTypeCheck(typeName);

			if (primitiveType != null) {
				return arrayDimensions != null ? arrayDimensions + primitiveType : primitiveType;
			}

			String returnType = "L" + project.findType(typeName).getFullyQualifiedName().replace(".", "/") + ";";
			return arrayDimensions != null ? arrayDimensions + returnType : returnType;
		} catch (JavaModelException e) {
			TestgeneratorActivator.log(e);
		}

		return null;
	}

	private String primitiveTypeCheck(String typeName) {
		switch (typeName) {
		case JAVA_BOOLEAN:
			return JVM_BOOLEAN;
		case JAVA_BYTE:
			return JVM_BYTE;
		case JAVA_SHORT:
			return JVM_SHORT;
		case JAVA_CHAR:
			return JVM_CHAR;
		case JAVA_INT:
			return JVM_INT;
		case JAVA_FLOAT:
			return JVM_FLOAT;
		case JAVA_LONG:
			return JVM_LONG;
		case JAVA_DOUBLE:
			return JVM_DOUBLE;
		case JAVA_VOID:
			return JVM_VOID;
		default:
			return null;
		}
	}

	private String arrayCheck(String type) {
		if (type.contains(ARRAY_BRACKET)) {
			return type.substring(0, type.lastIndexOf(ARRAY_BRACKET) + 1);
		}

		return null;
	}

}
