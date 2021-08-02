package org.testgen.agent.classdata.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ServiceLoader;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.core.Wrapper;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.Modifier;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

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

	public Map<Integer, FieldData> analyseConstructor(String methodDescriptor, //
			List<Instruction> filteredInstructions, List<Instruction> allInstructions) {
		Map<Integer, FieldData> initialzedFields = new HashMap<>();

		LOGGER.info("Starting Analysing Constructor " + methodDescriptor);

		if (filteredInstructions != null) {
			List<String> params = Instructions.getMethodParams(methodDescriptor);

			for (Instruction instruction : filteredInstructions) {

				if (Opcode.PUTFIELD == instruction.getOpcode()) {
					Wrapper<Integer> constructorParameterIndex = new Wrapper<>();

					String type = Descriptor.toClassName(instruction.getType());
					// inner classes got an instance of the outerclass as an constructor argument,
					// we ignore this because in the java code it doesnt exist
					if ((classData.isInnerClass() && !classData.getOuterClass().equals(type)
							&& AnalysisHelper.isDescriptorEqual(allInstructions, allInstructions.indexOf(instruction), //
									instruction.getType(), params, constructorParameterIndex))
							|| (!classData.isInnerClass() && AnalysisHelper.isDescriptorEqual(allInstructions,
									allInstructions.indexOf(instruction), //
									instruction.getType(), params, constructorParameterIndex))) {

						FieldData field = new FieldData.Builder().withName(instruction.getName()).withDataType(type)
								.build();

						initialzedFields.put(constructorParameterIndex.getValue(), field);
					}
					// invokespecial oder invokevirtual
				} else {
					Entry<MethodData, FieldData> method = classData.getMethod(instruction.getName(),
							instruction.getType());

					if (method != null) {

						MethodType type = method.getKey().getMethodType();
						if (MethodType.REFERENCE_VALUE_SETTER == type || MethodType.COLLECTION_SETTER == type) {

							Optional<Integer> argumentIndex = AnalysisHelper.getArgumentIndex(instruction,
									allInstructions);
							if (argumentIndex.isPresent()) {
								initialzedFields.put(argumentIndex.get(), method.getValue());
							}
						}
					}
				}
			}
		}

		return Collections.unmodifiableMap(initialzedFields);

	}

}
