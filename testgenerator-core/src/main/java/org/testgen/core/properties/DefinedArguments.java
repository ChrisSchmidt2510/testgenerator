package org.testgen.core.properties;

import org.testgen.core.properties.parser.Arguments;
import org.testgen.core.properties.parser.Parameter;

public final class DefinedArguments {
	public static final String ARG_CLASS_NAME = "ClassName";
	public static final String ARG_METHOD_NAME = "MethodName";
	public static final String ARG_METHOD_DESC = "MethodDescriptor";
	public static final String ARG_BL_PACKAGE = "BlPackage";
	public static final String ARG_BL_PACKGE_JAR_DEST = "BlPackageJarDestination";
	public static final String ARG_TRACE_READ_FIELD_ACCESS = "TraceReadFieldAccess";

	private DefinedArguments() {
	}

	public static Arguments getArguments() {
		Arguments definedArgs = new Arguments();
		definedArgs.addArgument(ARG_CLASS_NAME, Parameter.SINGLE_PARAMETER, true);
		definedArgs.addArgument(ARG_METHOD_NAME, Parameter.SINGLE_PARAMETER, true);
		definedArgs.addArgument(ARG_METHOD_DESC, Parameter.SINGLE_PARAMETER, true);
		definedArgs.addArgument(ARG_BL_PACKAGE, Parameter.MULTIPLE_PARAMETER, true);
		definedArgs.addArgument(ARG_BL_PACKGE_JAR_DEST, Parameter.MULTIPLE_PARAMETER);
		definedArgs.addArgument(ARG_TRACE_READ_FIELD_ACCESS);

		return definedArgs;
	}

}
