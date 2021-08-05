package org.testgen.agent.classdata.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;

public class MethodAnalyser {

	private static final Logger LOGGER = LogManager.getLogger(MethodAnalyser.class);

	private static final List<MethodAnalysis> ANALYSER = new ArrayList<>();

	static {
		ServiceLoader<MethodAnalysis> serviceLoader = ServiceLoader.load(MethodAnalysis.class);
		serviceLoader.forEach(ANALYSER::add);
	}

	public MethodAnalyser(ClassData classData, ClassFile classFile) {
		ANALYSER.forEach(an -> initAnalyser(an, classData, classFile));
	}

	private void initAnalyser(MethodAnalysis analyser, ClassData classData, ClassFile classFile) {
		analyser.setClassData(classData);
		analyser.setClassFile(classFile);
	}

	public void analyse(MethodInfo method, List<Instruction> instructions) {
		LOGGER.info("Starting Analysis of Method: " +method);
		
		for (MethodAnalysis analyser : ANALYSER) {
			String analyserName = analyser.getClass().getName();
			
			LOGGER.debug(String.format("use %s for analysis", analyserName));
			
			if(!analyser.canAnalysisBeApplied(method)) {
				LOGGER.debug(String.format("requirements for analyser %s arent fulfilled", analyserName));
				continue;
			}
			
			if(analyser.hasAnalysisMatched(method, instructions)) {
				LOGGER.debug(String.format("analysis found a result with analyser %s", analyserName));
				break;
			}
		}
	}

}
