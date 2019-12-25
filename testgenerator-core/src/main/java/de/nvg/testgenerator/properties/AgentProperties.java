package de.nvg.testgenerator.properties;

import java.util.Map;

public class AgentProperties {

	private static AgentProperties INSTANCE;

	private String className;
	private String method;
	private String methodDescriptor;
	private String blPackage;
	private boolean traceGetterCalls;

	private AgentProperties() {
	}

	public static void initProperties(String arguments) {
		Map<String, String> properties = PropertyParser.parseAgentProperties(arguments);

		AgentProperties agentProperties = new AgentProperties();

		agentProperties.className = properties.get(PropertyParser.ARG_CLASS);
		agentProperties.method = properties.get(PropertyParser.ARG_METHOD);
		agentProperties.methodDescriptor = properties.get(PropertyParser.ARG_METHOD_DESC);
		agentProperties.blPackage = properties.get(PropertyParser.ARG_BL_PACKAGE);
		agentProperties.traceGetterCalls = Boolean.valueOf(properties.get(PropertyParser.ARG_TRACE_GETTER_CALLS));

		if (INSTANCE == null) {
			INSTANCE = agentProperties;
		}

	}

	public static AgentProperties getInstance() {
		return INSTANCE;
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

	public String getBlPackage() {
		return blPackage;
	}

	public boolean isTraceGetterCalls() {
		return traceGetterCalls;
	}

}
