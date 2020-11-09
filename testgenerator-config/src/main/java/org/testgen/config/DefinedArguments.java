package org.testgen.config;

import org.testgen.config.parser.Arguments;
import org.testgen.config.parser.Parameter;

public final class DefinedArguments {
	public static final String ARG_CLASS_NAME = "ClassName";
	public static final String ARG_METHOD_NAME = "MethodName";
	public static final String ARG_METHOD_DESC = "MethodDescriptor";
	public static final String ARG_BL_PACKAGE = "BlPackage";
	public static final String ARG_BL_PACKGE_JAR_DEST = "BlPackageJarDestination";
	public static final String ARG_TRACE_READ_FIELD_ACCESS = "TraceReadFieldAccess";
	public static final String ARG_PRINT_CLASSFILES_DIR = "PrintClassFilesDir";
	public static final String ARG_CUSTOM_TESTGENERATOR_CLASS = "CustomTestgeneratorClass";

	public static final String ARG_CUSTOM_LOGGER_CONFIG = "CustomLoggerConfiguration";

	private DefinedArguments() {
	}

	public static Arguments getArguments() {
		Arguments definedArgs = new Arguments();
		definedArgs.addArgument(ARG_CLASS_NAME, Parameter.SINGLE_PARAMETER, true);
		definedArgs.addArgument(ARG_METHOD_NAME, Parameter.SINGLE_PARAMETER, true);
		definedArgs.addArgument(ARG_METHOD_DESC, Parameter.SINGLE_PARAMETER, true);
		definedArgs.addArgument(ARG_BL_PACKAGE, Parameter.MULTIPLE_PARAMETER, true);
		definedArgs.addArgument(ARG_BL_PACKGE_JAR_DEST, Parameter.MULTIPLE_PARAMETER);
		definedArgs.addArgument(ARG_PRINT_CLASSFILES_DIR, Parameter.SINGLE_PARAMETER);
		definedArgs.addArgument(ARG_CUSTOM_TESTGENERATOR_CLASS, Parameter.SINGLE_PARAMETER);
		definedArgs.addArgument(ARG_TRACE_READ_FIELD_ACCESS);
		definedArgs.addArgument(ARG_CUSTOM_LOGGER_CONFIG, Parameter.SINGLE_PARAMETER);

		return definedArgs;
	}

}
