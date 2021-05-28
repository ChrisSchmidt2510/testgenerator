package org.testgen.agent.classdata.analysis.impl;

import java.util.List;

import org.testgen.agent.classdata.analysis.BasicMethodAnalysis;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodType;

import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class ImmutableCollectionGetterAnalyser extends BasicMethodAnalysis {

	@Override
	public boolean canAnalysisBeApplied(MethodInfo method) {
		String returnType = Instructions.getReturnType(method.getDescriptor());

		return isMethodAccessible(method.getAccessFlags()) && !Primitives.JVM_VOID.equals(returnType)
				&& Descriptor.numOfParameters(method.getDescriptor()) == 0 && isTypeCollection(returnType);
	}

	@Override
	public boolean hasAnalysisMatched(MethodInfo method, List<Instruction> instructions) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode()) {

				Instruction invokeStatic = instructions.get(i + 1);

				if (JVMTypes.COLLECTION_TYPES.contains(instruction.getType())
						&& Opcode.INVOKESTATIC == invokeStatic.getOpcode()
						&& JavaTypes.COLLECTIONS.equals(invokeStatic.getClassRef())
						&& invokeStatic.getName().startsWith("unmodifiable") && instructions.size() >= i + 2
						&& Opcode.ARETURN == instructions.get(i + 2).getOpcode()) {

					FieldData field = classData.getField(instruction.getName(),
							Descriptor.toClassName(instruction.getType()));

					addAnalysisResult(method, MethodType.IMMUTABLE_GETTER, field);

					return true;
				}
			}
		}

		return false;
	}

}
