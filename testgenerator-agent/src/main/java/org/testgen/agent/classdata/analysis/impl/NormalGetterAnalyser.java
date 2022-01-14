package org.testgen.agent.classdata.analysis.impl;

import java.util.List;
import java.util.Map;

import org.testgen.agent.classdata.analysis.BasicMethodAnalysis;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.core.MapBuilder;

import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class NormalGetterAnalyser extends BasicMethodAnalysis {

	private static final Map<String, Integer> PRIMITIVE_RETURN_OPCODES = //
			MapBuilder.<String, Integer>hashMapBuilder()//
					.add(Primitives.JVM_INT, Opcode.IRETURN)//
					.add(Primitives.JVM_SHORT, Opcode.IRETURN)//
					.add(Primitives.JVM_BYTE, Opcode.IRETURN)//
					.add(Primitives.JVM_CHAR, Opcode.IRETURN)//
					.add(Primitives.JVM_BOOLEAN, Opcode.IRETURN)//
					.add(Primitives.JVM_FLOAT, Opcode.FRETURN)//
					.add(Primitives.JVM_DOUBLE, Opcode.DRETURN)//
					.add(Primitives.JVM_LONG, Opcode.LRETURN)//
					.add("[", Opcode.ARETURN).toUnmodifiableMap();

	@Override
	public boolean canAnalysisBeApplied(MethodInfo method) {
		return isMethodAccessible(method.getAccessFlags())
				&& !Primitives.JVM_VOID.equals(Instructions.getReturnType(method.getDescriptor()));
	}

	@Override
	public boolean hasAnalysisMatched(MethodInfo method, List<Instruction> instructions) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode() && classData.getName().equals(instruction.getClassRef())) {

				Integer returnOpcode = PRIMITIVE_RETURN_OPCODES.get(instruction.getType());
				int opcode = instructions.get(i + 1).getOpcode();

				if ((returnOpcode != null && returnOpcode.equals(opcode)) || Opcode.ARETURN == opcode) {

					FieldData field = getField(instruction);

					MethodType methodType = field.isMutable()
							|| (!field.isMutable() && JavaTypes.COLLECTION_LIST.contains(field.getDataType()))
									? MethodType.REFERENCE_VALUE_GETTER
									: MethodType.IMMUTABLE_GETTER;

					addAnalysisResult(method, methodType, field);

					return true;
				}
			}
		}
		return false;

	}

}
