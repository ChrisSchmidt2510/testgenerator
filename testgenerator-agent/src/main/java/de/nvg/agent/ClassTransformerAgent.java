package de.nvg.agent;

import java.lang.instrument.Instrumentation;

import de.nvg.agent.transformer.ClassDataTransformer;
import de.nvg.agent.transformer.ValueTrackerTransformer;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.properties.AgentProperties;
import javassist.ClassPool;
import javassist.NotFoundException;

public final class ClassTransformerAgent {

	private static final Logger LOGGER = LogManager.getLogger(ClassTransformerAgent.class);

	private ClassTransformerAgent() {
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {

		AgentProperties.initProperties(agentArgs);

		registerAdditionalClassPaths();

		ValueTrackerTransformer valueTrackerTransformer = new ValueTrackerTransformer();

		ClassDataTransformer metaDataTransformer = new ClassDataTransformer();

		instrumentation.addTransformer(valueTrackerTransformer);
		instrumentation.addTransformer(metaDataTransformer);
	}

	private static void registerAdditionalClassPaths() {

		for (String blJar : AgentProperties.getInstance().getBlPackageJarDest()) {
			try {
				ClassPool.getDefault().appendClassPath(blJar + "\\*");
			} catch (NotFoundException e) {
				LOGGER.error("cant add BL-Jar " + blJar + "to Javassist-ClassPath", e);
			}
		}
	}

}
