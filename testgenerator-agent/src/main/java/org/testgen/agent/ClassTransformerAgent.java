package org.testgen.agent;

import java.lang.instrument.Instrumentation;

import org.testgen.config.TestgeneratorConfig;

import javassist.CtClass;

public final class ClassTransformerAgent {

	private ClassTransformerAgent() {
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {

		TestgeneratorConfig.initConfiguration(agentArgs);

		dumpModifiedClassFiles();
		
		instrumentation.addTransformer(new StartingTransformer());
	}

	private static void dumpModifiedClassFiles() {
		String directory = TestgeneratorConfig.getPrintClassFileDirectory();

		if (directory != null)
			CtClass.debugDump = directory;
	}

}
