package org.testgen.agent;

import java.lang.instrument.Instrumentation;

import org.testgen.agent.transformer.ClassDataTransformer;
import org.testgen.agent.transformer.ValueTrackerTransformer;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public final class ClassTransformerAgent {

	private static final Logger LOGGER = LogManager.getLogger(ClassTransformerAgent.class);

	private ClassTransformerAgent() {
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {

		TestgeneratorConfig.initConfiguration(agentArgs);

		registerAdditionalClassPaths();

		dumpModifiedClassFiles();

		ValueTrackerTransformer valueTrackerTransformer = new ValueTrackerTransformer();

		ClassDataTransformer metaDataTransformer = new ClassDataTransformer();

		instrumentation.addTransformer(valueTrackerTransformer);
		instrumentation.addTransformer(metaDataTransformer);
	}

	private static void registerAdditionalClassPaths() {

		for (String blJar : TestgeneratorConfig.getBlPackageJarDest()) {
			try {
				ClassPool.getDefault().appendClassPath(blJar + "\\*");
			} catch (NotFoundException e) {
				LOGGER.error("cant add BL-Jar " + blJar + "to Javassist-ClassPath", e);
			}
		}
	}

	private static void dumpModifiedClassFiles() {
		String directory = TestgeneratorConfig.getPrintClassFileDirectory();

		if (directory != null)
			CtClass.debugDump = directory;
	}

}
