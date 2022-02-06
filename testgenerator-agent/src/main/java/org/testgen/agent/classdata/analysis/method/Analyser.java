package org.testgen.agent.classdata.analysis.method;

import java.util.ArrayList;
import java.util.List;

import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.config.DefinedArguments;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

/**
 * Entry point of the analysis part of the testgenerator framework, you can
 * provide your custom implementation of the analysis by extending this class
 * and add the parameter {@link DefinedArguments#ARG_CUSTOM_ANALYSIS_CLASS} to
 * start configuration.
 *
 */
public abstract class Analyser {
	protected static final List<MethodAnalysis> ANALYSER = new ArrayList<>();

	/**
	 * Initialize all managed {@link MethodAnalysis} with {@link ClassData} and
	 * {@link ClassFile}.
	 * 
	 * @param classData
	 * @param classFile
	 */
	protected Analyser(ClassData classData, ClassFile classFile) {
		ANALYSER.forEach(ma -> initializeMethodAnalyser(ma, classData, classFile));
	}

	private void initializeMethodAnalyser(MethodAnalysis analyser, ClassData classData, ClassFile classFile) {
		analyser.setClassData(classData);
		analyser.setClassFile(classFile);
	}

	/**
	 * Within this method will all {@link MethodAnalysis} run, until one
	 * {@link MethodAnalysis} is applied successfully.
	 * 
	 * @param method
	 * @param instructions
	 */
	public abstract void analyseMethod(MethodInfo method, List<Instruction> instructions);

	/**
	 * resets all {@link MethodAnalysis} that are managed. The concrete reset
	 * mechanism is defined in each {@link MethodAnalysis}.
	 */
	public void resetMethodAnalyser() {
		ANALYSER.forEach(MethodAnalysis::reset);
	}
}
