package org.testgen.agent.classdata.analysis.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.analysis.BasicMethodAnalysis;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.instructions.filter.ForwardInstructionFilter;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodType;

import javassist.Modifier;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class CollectionSetterAnalyser extends BasicMethodAnalysis {

	@Override
	public boolean canAnalysisBeApplied(MethodInfo method) {
		return isMethodAccessible(method.getAccessFlags()) && Descriptor.numOfParameters(method.getDescriptor()) > 0;
	}

	@Override
	public boolean hasAnalysisMatched(MethodInfo method, List<Instruction> instructions) {

		List<Instruction> filteredInstructions = instructions.stream()
				.filter(inst -> Opcode.GETFIELD == inst.getOpcode() && isTypeCollection(inst.getType()))
				.collect(Collectors.toList());

		if (filteredInstructions == null || filteredInstructions.isEmpty())
			return false;

		for (Instruction getFieldInstruction : filteredInstructions) {

			ForwardInstructionFilter instructionFilter = new ForwardInstructionFilter(classFile, instructions);
			Instruction collectionInstruction = instructionFilter
					.filterForUseOfGetFieldInstruction(getFieldInstruction);

			Set<String> implementedCollections = getImplementedCollections(collectionInstruction.getClassRef());

			if (implementedCollections.stream().noneMatch(
					col -> JavaTypes.COLLECTION_ADD_METHODS.get(col).contains(collectionInstruction.getName())))
				continue;

			List<Instruction> calledLoadInstructions = instructionFilter.getCalledLoadInstructions();

			Collections.reverse(calledLoadInstructions);

			List<String> methodParams = Instructions.getMethodParams(method.getDescriptor());
			if (areAllMethodParametersUsed(calledLoadInstructions, methodParams,
					Modifier.isStatic(method.getAccessFlags()))) {
				FieldData fieldData = classData.getField(getFieldInstruction.getName(),
						Descriptor.toClassName(getFieldInstruction.getType()));

				addAnalysisResult(method, MethodType.COLLECTION_SETTER, fieldData);
				return true;
			}

		}

		return false;

	}

}
