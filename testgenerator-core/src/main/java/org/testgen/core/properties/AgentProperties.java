package org.testgen.core.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testgen.core.properties.parser.ArgumentParser;

public final class AgentProperties {

	private static AgentProperties instance;

	private List<String> classNames;
	private String className;
	private String method;
	private String methodDescriptor;
	private List<String> blPackage;
	private List<String> blPackageJarDestination;

	private String printClassFileDirectory;
	private boolean traceReadFieldAccess;

	private AgentProperties() {
	}

	public static void initProperties(String arguments) {

		ArgumentParser parser = new ArgumentParser(arguments, DefinedArguments.getArguments());

		AgentProperties agentProperties = new AgentProperties();

		String className = parser.getArgumentValue(DefinedArguments.ARG_CLASS_NAME);
		agentProperties.classNames = isInnerClass(className);
		agentProperties.className = className;
		agentProperties.method = parser.getArgumentValue(DefinedArguments.ARG_METHOD_NAME);
		agentProperties.methodDescriptor = parser.getArgumentValue(DefinedArguments.ARG_METHOD_DESC);
		agentProperties.blPackage = parser.getArgumentValues(DefinedArguments.ARG_BL_PACKAGE);
		agentProperties.blPackageJarDestination = parser.getArgumentValues(DefinedArguments.ARG_BL_PACKGE_JAR_DEST);
		agentProperties.printClassFileDirectory = parser.getArgumentValue(DefinedArguments.ARG_PRINT_CLASSFILES_DIR);
		agentProperties.traceReadFieldAccess = parser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS);

		if (instance == null) {
			instance = agentProperties;
		}

	}

	public static AgentProperties getInstance() {
		return instance;
	}

	public List<String> getClassNames() {
		return classNames;
	}

	public String getClassName() {
		return className;
	}

	public String getMethod() {
		return method;
	}

	public String getMethodDescriptor() {
		return methodDescriptor;
	}

	public List<String> getBlPackage() {
		return blPackage;
	}

	public List<String> getBlPackageJarDest() {
		return blPackageJarDestination;
	}

	public boolean printClassFiles() {
		return printClassFileDirectory != null;
	}

	public String getPrintClassFileDirectory() {
		return printClassFileDirectory;
	}

	public boolean isTraceReadFieldAccess() {
		return traceReadFieldAccess;
	}

	private static List<String> isInnerClass(String className) {
		if (className.contains("$")) {
			return Arrays.asList(className.substring(0, className.indexOf("$")), className);
		}

		return Collections.singletonList(className);
	}

}
