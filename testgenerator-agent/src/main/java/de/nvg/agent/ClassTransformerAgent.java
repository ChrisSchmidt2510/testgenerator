package de.nvg.agent;

import java.lang.instrument.Instrumentation;

import de.nvg.agent.transformer.ClassDataTransformer;
import de.nvg.agent.transformer.ValueTrackerTransformer;
import de.nvg.testgenerator.properties.AgentProperties;
import javassist.ClassPool;
import javassist.NotFoundException;

public final class ClassTransformerAgent {

	private ClassTransformerAgent() {
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {

		AgentProperties.initProperties(agentArgs);

		try {
			ClassPool.getDefault()
					.appendClassPath("D:\\Projekt_x64\\boss\\feature\\Backend\\module\\boss-bl\\target\\*");
		} catch (NotFoundException e) {
			e.printStackTrace();
		}

		ValueTrackerTransformer valueTrackerTransformer = new ValueTrackerTransformer();

		ClassDataTransformer metaDataTransformer = new ClassDataTransformer();

		instrumentation.addTransformer(valueTrackerTransformer);
		instrumentation.addTransformer(metaDataTransformer);
	}

}
