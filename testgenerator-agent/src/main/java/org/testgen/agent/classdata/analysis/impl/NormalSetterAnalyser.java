package org.testgen.agent.classdata.analysis.impl;

import java.util.List;

import org.testgen.agent.classdata.analysis.BasicMethodAnalysis;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.instructions.filter.ReverseInstructionFilter;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodType;

import javassist.Modifier;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class NormalSetterAnalyser extends BasicMethodAnalysis {

	@Override
	public boolean canAnalysisBeApplied(MethodInfo method) {
		return isMethodAccessible(method.getAccessFlags()) && Descriptor.numOfParameters(method.getDescriptor()) > 0;
	}

	@Override
	public boolean hasAnalysisMatched(MethodInfo method, List<Instruction> instructions) {
		for (int index = 0; index < instructions.size(); index++) {
			Instruction instruction = instructions.get(index);

			if (Opcode.PUTFIELD == instruction.getOpcode()) {

				List<String> parameters = Instructions.getMethodParams(method.getDescriptor());

				ReverseInstructionFilter instructionFilter = new ReverseInstructionFilter(classFile, instructions);
				List<Instruction> calledLoadInstructions = instructionFilter
						.filterForCalledLoadInstructions(instruction);

				boolean isStaticMethod = Modifier.isStatic(method.getAccessFlags());

				if (!isStaticMethod) {
					removeAload0InstructionFromLoadInstructions(calledLoadInstructions);
				}

				if (instructions.size() < 25 && //
						areAllMethodParametersUsed(calledLoadInstructions, parameters, isStaticMethod)) {

					FieldData field = getField(instruction);

					addAnalysisResult(method, MethodType.REFERENCE_VALUE_SETTER, field);

					return true;
				}
			}
		}

		return false;
	}

}
