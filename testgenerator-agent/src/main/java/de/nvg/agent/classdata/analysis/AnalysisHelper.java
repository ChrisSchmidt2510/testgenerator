package de.nvg.agent.classdata.analysis;

import java.util.List;

import org.testgen.core.Wrapper;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.FieldData;
import javassist.bytecode.Opcode;

public final class AnalysisHelper {

	private AnalysisHelper() {
	}

	static boolean isDescriptorEqual(List<Instruction> instructions, int fieldInstructionIndex, String fieldDescriptor,
			List<String> methodParameters, Wrapper<Integer> argumentIndex) {

		Instruction instruction = instructions.get(fieldInstructionIndex - 1);

		switch (instruction.getOpcode()) {
		case Opcode.ALOAD_1:
		case Opcode.ILOAD_1:
		case Opcode.DLOAD_1:
		case Opcode.FLOAD_1:
		case Opcode.LLOAD_1:
			return compareDescriptors(fieldDescriptor, 1, //
					methodParameters, argumentIndex);
		case Opcode.ALOAD_2:
		case Opcode.ILOAD_2:
		case Opcode.DLOAD_2:
		case Opcode.FLOAD_2:
		case Opcode.LLOAD_2:
			return compareDescriptors(fieldDescriptor, 2, //
					methodParameters, argumentIndex);
		case Opcode.ALOAD_3:
		case Opcode.ILOAD_3:
		case Opcode.DLOAD_3:
		case Opcode.FLOAD_3:
		case Opcode.LLOAD_3:
			return compareDescriptors(fieldDescriptor, 3, //
					methodParameters, argumentIndex);
		case Opcode.ALOAD:
		case Opcode.ILOAD:
		case Opcode.DLOAD:
		case Opcode.FLOAD:
		case Opcode.LLOAD:
			return compareDescriptors(fieldDescriptor, instruction.getLocalVariableIndex(), methodParameters,
					argumentIndex);
		default:
			return false;
		}
	}

	private static boolean compareDescriptors(String fieldDescriptor, int localVariableIndex,
			List<String> methodParameters, Wrapper<Integer> argumentIndex) {
		if ((localVariableIndex - 1) < methodParameters.size()) {
			argumentIndex.setValue(localVariableIndex);
			return fieldDescriptor.equals(methodParameters.get(localVariableIndex - 1));
		}

		return false;
	}

	static FieldData getField(List<FieldData> fields, String name, String type) {
		return fields.stream().filter(field -> field.getName().equals(name) && field.getDataType().equals(type))
				.findAny()
				// can't be null cause the classfile would be otherwise incorrect
				.orElse(null);
	}
}
