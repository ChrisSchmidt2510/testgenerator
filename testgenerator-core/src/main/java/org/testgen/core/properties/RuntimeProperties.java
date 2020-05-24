package org.testgen.core.properties;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.testgen.core.properties.parser.ArgumentParser;

public final class RuntimeProperties {
	private static final String JAVA_AGENT = "-javaagent";
	private static final RuntimeProperties INSTANCE = new RuntimeProperties();
	private ArgumentParser argParser;

	private boolean activateTracking;

	private RuntimeProperties() {
	}

	public static RuntimeProperties getInstance() {
		return INSTANCE;
	}

	public boolean isTrackingActive() {
		return activateTracking;
	}

	public void setActivateTracking(boolean activateTracking) {
		this.activateTracking = activateTracking;
	}

	public boolean wasFieldTrackingActivated() {
		if (argParser == null) {
			initAgentProperties();
		}

		return argParser.hasArgument(DefinedArguments.ARG_TRACE_READ_FIELD_ACCESS);
	}

	private void initAgentProperties() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String javaAgent = runtime.getInputArguments().stream().filter(args -> args.startsWith(JAVA_AGENT)).findAny()
				.orElse(null);
		String agentArgs = javaAgent.substring(javaAgent.indexOf('=') + 1);

		argParser = new ArgumentParser(agentArgs, DefinedArguments.getArguments());
	}
}
