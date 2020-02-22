package de.nvg.testgenerator.properties;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

public class RuntimeProperties {
	private static final String JAVA_AGENT = "-javaagent";
	private static final RuntimeProperties INSTANCE = new RuntimeProperties();

	private final Map<String, String> agentProperties = new HashMap<>();

	private boolean activateTracking = false;

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
		if (agentProperties.isEmpty()) {
			initAgentProperties();
		}

		return Boolean.valueOf(agentProperties.get(PropertyParser.ARG_TRACE_GETTER_CALLS));
	}

	private void initAgentProperties() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String javaAgent = runtime.getInputArguments().stream().filter(args -> args.startsWith(JAVA_AGENT)).findAny()
				.orElse(null);
		String agentArgs = javaAgent.substring(javaAgent.indexOf("=") + 1);

		agentProperties.putAll(PropertyParser.parseAgentProperties(agentArgs));
	}
}
