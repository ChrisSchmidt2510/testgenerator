package org.testgen.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.testgen.config.parser.ArgumentParser;

public final class TestgeneratorConfig {

	private static final String LIST_ARGS_SEPARATUR = ",";

	private static final String EMPTY_ARGUMENT = "";

	// Agent-Properties
	private static final String PARAM_CLASS_NAME = "TestgeneratorClassName";
	private static final String PARAM_CLASS_NAMES = "TestgeneratorClassNames";
	private static final String PARAM_METHOD_NAME = "TestgeneratorMethodName";
	private static final String PARAM_METHOD_DESC = "TestgeneratorMethodDesc";
	private static final String PARAM_BL_PACKAGE = "TestgeneratorBlPackage";
	private static final String PARAM_BL_PACKAGE_JAR_DEST = "TestgeneratorBlPackageJarDest";
	private static final String PARAM_PRINT_CLASS_FILE_DIR = "TestgeneratorPrintClassFileDir";

	private static final String PARAM_CUSTOM_TESTGENERATOR_CLASS = "TestgeneratorCustomTestgeneratorClass";
	private static final String PARAM_TRACE_READ_FIELD_ACCESS = "TestgeneratorTraceReadFieldAccess";

	// Runtime-Properties
	private static final String PARAM_RUNTIME_FIELD_TRACKING = "TestgeneratorRuntimeFieldTracking";
	private static final String PARAM_RUNTIME_PROXY_FIELD_TRACKING = "TestgeneratorRuntimeProxyFieldTracking";
	private static final String PARAM_RUNTIME_PROXY_TRACKING = "TestgeneratorRuntimeProxyTracking";

	private TestgeneratorConfig() {
	}

	public static void initConfiguration(String arguments) {
		ArgumentParser parser = new ArgumentParser(arguments, DefinedArguments.getArguments());

//		System.setProperties(null);

		String className = parser.getArgumentValue(DefinedArguments.ARG_CLASS_NAME);
		System.setProperty(PARAM_CLASS_NAME, className);
		System.setProperty(PARAM_CLASS_NAMES, generateSystemPropertyArgument(isInnerClass(className)));
		System.setProperty(PARAM_METHOD_NAME, parser.getArgumentValue(DefinedArguments.ARG_METHOD_NAME));
		System.setProperty(PARAM_METHOD_DESC, parser.getArgumentValue(DefinedArguments.ARG_METHOD_DESC));
		System.setProperty(PARAM_BL_PACKAGE,
				generateSystemPropertyArgument(parser.getArgumentValues(DefinedArguments.ARG_BL_PACKAGE)));
		System.setProperty(PARAM_BL_PACKAGE_JAR_DEST,
				generateSystemPropertyArgument(parser.getArgumentValues(DefinedArguments.ARG_BL_PACKGE_JAR_DEST)));
		System.setProperty(PARAM_PRINT_CLASS_FILE_DIR,
				checkForEmptyArgument(parser.getArgumentValue(DefinedArguments.ARG_PRINT_CLASSFILES_DIR)));
		System.setProperty(PARAM_CUSTOM_TESTGENERATOR_CLASS,
				checkForEmptyArgument(parser.getArgumentValue(DefinedArguments.ARG_COSTUM_TESTGENERATOR_CLASS)));
		System.setProperty(PARAM_TRACE_READ_FIELD_ACCESS,
				Boolean.toString(parser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS)));

		String booleanFalse = Boolean.toString(false);
		System.setProperty(PARAM_RUNTIME_FIELD_TRACKING, booleanFalse);
		System.setProperty(PARAM_RUNTIME_PROXY_FIELD_TRACKING, booleanFalse);
		System.setProperty(PARAM_RUNTIME_PROXY_TRACKING, booleanFalse);
	}

	public static String getClassName() {
		return System.getProperty(PARAM_CLASS_NAME);
	}

	public static List<String> getClassNames() {
		String classNames = System.getProperty(PARAM_CLASS_NAMES);

		return Collections.unmodifiableList(convertArgumentToList(classNames));
	}

	public static String getMethodName() {
		return System.getProperty(PARAM_METHOD_NAME);
	}

	public static String getMethodDescriptor() {
		return System.getProperty(PARAM_METHOD_DESC);
	}

	public static List<String> getBlPackages() {
		String blPackages = System.getProperty(PARAM_BL_PACKAGE);

		return Collections.unmodifiableList(convertArgumentToList(blPackages));
	}

	public static List<String> getBlPackageJarDest() {
		String blPackageJarDest = System.getProperty(PARAM_BL_PACKAGE_JAR_DEST);

		return checkStringFilled(blPackageJarDest) ? //
				Collections.unmodifiableList(convertArgumentToList(blPackageJarDest)) : //
				Collections.emptyList();
	}

	public static String getPrintClassFileDirectory() {
		String property = System.getProperty(PARAM_PRINT_CLASS_FILE_DIR);

		return checkStringFilled(property) ? property : null;
	}

	public static String getCustomTestgeneratorClass() {
		String property = System.getProperty(PARAM_CUSTOM_TESTGENERATOR_CLASS);

		return checkStringFilled(property) ? property : null;
	}

	public static boolean traceReadFieldAccess() {
		return Boolean.getBoolean(PARAM_TRACE_READ_FIELD_ACCESS);
	}

	public static boolean isFieldTrackingActivated() {
		return Boolean.getBoolean(PARAM_RUNTIME_FIELD_TRACKING);
	}

	public static boolean isProxyFieldTrackingActivated() {
		return Boolean.getBoolean(PARAM_RUNTIME_PROXY_FIELD_TRACKING);
	}

	public static boolean isProxyTrackingActivated() {
		return Boolean.getBoolean(PARAM_RUNTIME_PROXY_TRACKING);
	}

	public static void setFieldTracking(boolean fieldTracking) {
		System.setProperty(PARAM_RUNTIME_FIELD_TRACKING, Boolean.toString(fieldTracking));
	}

	public static void setProxyFieldTracking(boolean proxyFieldTracking) {
		System.setProperty(PARAM_RUNTIME_PROXY_FIELD_TRACKING, Boolean.toString(proxyFieldTracking));
	}

	public static void setProxyTracking(boolean proxyTracking) {
		System.setProperty(PARAM_RUNTIME_PROXY_TRACKING, Boolean.toString(proxyTracking));
	}

	private static List<String> isInnerClass(String className) {
		if (className.contains("$")) {
			return Arrays.asList(className.substring(0, className.indexOf("$")), className);
		}

		return Collections.singletonList(className);
	}

	private static String checkForEmptyArgument(String argument) {
		return argument == null ? EMPTY_ARGUMENT : argument;
	}

	private static String generateSystemPropertyArgument(List<String> listArgument) {
		if (listArgument == null) {
			return EMPTY_ARGUMENT;
		}
		return String.join(LIST_ARGS_SEPARATUR, listArgument);
	}

	private static List<String> convertArgumentToList(String property) {
		List<String> result = new ArrayList<>();

		StringTokenizer tokenizer = new StringTokenizer(property, LIST_ARGS_SEPARATUR);

		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextToken());
		}

		return result;
	}

	private static boolean checkStringFilled(String str) {
		return str != null && !str.trim().isEmpty();
	}

}
