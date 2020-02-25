package de.nvg.agent;

import java.lang.instrument.Instrumentation;

import de.nvg.agent.transformer.ClassDataTransformer;
import de.nvg.agent.transformer.ValueTrackerTransformer;
import de.nvg.testgenerator.properties.AgentProperties;

public class ClassTransformerAgent {

	public static void premain(String agentArgs, Instrumentation instrumentation) {

		AgentProperties.initProperties(agentArgs);

		ValueTrackerTransformer valueTrackerTransformer = new ValueTrackerTransformer();

		ClassDataTransformer metaDataTransformer = new ClassDataTransformer();

		instrumentation.addTransformer(valueTrackerTransformer);
		instrumentation.addTransformer(metaDataTransformer);
	}

}
