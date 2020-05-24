package de.nvg.agent;

import java.lang.instrument.Instrumentation;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.AgentProperties;

import de.nvg.agent.transformer.ClassDataTransformer;
import de.nvg.agent.transformer.ValueTrackerTransformer;
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
