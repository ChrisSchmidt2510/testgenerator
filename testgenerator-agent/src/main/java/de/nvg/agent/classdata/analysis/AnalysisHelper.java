package de.nvg.agent.classdata.analysis;

import java.util.List;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.testgenerator.Wrapper;
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
			argumentIndex.setValue(1);
			return fieldDescriptor.equals(methodParameters.get(0));
		case Opcode.ALOAD_2:
		case Opcode.ILOAD_2:
		case Opcode.DLOAD_2:
		case Opcode.FLOAD_2:
		case Opcode.LLOAD_2:
			argumentIndex.setValue(2);
			return fieldDescriptor.equals(methodParameters.get(1));
		case Opcode.ALOAD_3:
		case Opcode.ILOAD_3:
		case Opcode.DLOAD_3:
		case Opcode.FLOAD_3:
		case Opcode.LLOAD_3:
			argumentIndex.setValue(3);
			return fieldDescriptor.equals(methodParameters.get(2));
		case Opcode.ALOAD:
		case Opcode.ILOAD:
		case Opcode.DLOAD:
		case Opcode.FLOAD:
		case Opcode.LLOAD:
			argumentIndex.setValue(instruction.getLocalVariableIndex());
			return fieldDescriptor.equals(methodParameters.get(instruction.getLocalVariableIndex() - 1));
		default:
			return false;
		}
	}

	static FieldData getField(List<FieldData> fields, String name, String type) {
		return fields.stream().filter(field -> field.getName().equals(name) && field.getDataType().equals(type))
				.findAny()
				// can't be null cause the classfile would be otherwise incorrect
				.orElse(null);
	}
}
