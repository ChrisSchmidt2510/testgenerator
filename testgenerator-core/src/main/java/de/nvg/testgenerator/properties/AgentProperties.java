package de.nvg.testgenerator.properties;

import java.util.List;

import de.nvg.testgenerator.properties.parser.ArgumentParser;

public final class AgentProperties {

	private static AgentProperties instance;

	private String className;
	private String method;
	private String methodDescriptor;
	private List<String> blPackage;
	private List<String> blPackageJarDestination;
	private boolean traceReadFieldAccess;

	private AgentProperties() {
	}

	public static void initProperties(String arguments) {

		ArgumentParser parser = new ArgumentParser(arguments, DefinedArguments.getArguments());

		AgentProperties agentProperties = new AgentProperties();

		agentProperties.className = parser.getArgumentValue(DefinedArguments.ARG_CLASS_NAME);
		agentProperties.method = parser.getArgumentValue(DefinedArguments.ARG_METHOD_NAME);
		agentProperties.methodDescriptor = parser.getArgumentValue(DefinedArguments.ARG_METHOD_DESC);
		agentProperties.blPackage = parser.getArgumentValues(DefinedArguments.ARG_BL_PACKAGE);
		agentProperties.blPackageJarDestination = parser.getArgumentValues(DefinedArguments.ARG_BL_PACKGE_JAR_DEST);
		agentProperties.traceReadFieldAccess = parser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS);

		if (instance == null) {
			instance = agentProperties;
		}

	}

	public static AgentProperties getInstance() {
		return instance;
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

	public boolean isTraceReadFieldAccess() {
		return traceReadFieldAccess;
	}

}
