package de.nvg.javaagent;

import java.lang.instrument.Instrumentation;

import de.nvg.javaagent.transformer.ClassDataTransformer;
import de.nvg.javaagent.transformer.ValueTrackerTransformer;
import de.nvg.testgenerator.logging.Level;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.properties.AgentProperties;

public class ClassTransformerAgent {

	static {
		// Initalise Log-Level
		Logger.getInstance().setLevel(Level.ERROR);
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {

		AgentProperties.initProperties(agentArgs);

		ValueTrackerTransformer valueTrackerTransformer = new ValueTrackerTransformer();

		ClassDataTransformer metaDataTransformer = new ClassDataTransformer();

		instrumentation.addTransformer(valueTrackerTransformer);
		instrumentation.addTransformer(metaDataTransformer);
	}

}
