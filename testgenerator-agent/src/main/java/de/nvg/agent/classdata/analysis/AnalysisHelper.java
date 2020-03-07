package de.nvg.agent.classdata.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.constants.Primitives;
import javassist.bytecode.Opcode;

public final class AnalysisHelper {

	private AnalysisHelper() {
	}

	static List<String> getMethodParams(String descriptor) {
		List<String> parameters = new ArrayList<>();

		String methodParameters = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.indexOf(")"));

		while (methodParameters.length() > 0) {
			if (Primitives.isPrimitiveDataType(methodParameters)) {
				parameters.add(String.valueOf(methodParameters.charAt(0)));
				methodParameters = methodParameters.substring(1);
			} else {
				int index = methodParameters.indexOf(";") + 1;

				parameters.add(methodParameters.substring(0, index));
				methodParameters = methodParameters.substring(index);
			}
		}

		return Collections.unmodifiableList(parameters);
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
		}
		return false;
	}

	static List<String> getGenericTypesFromSignature(String signature) {
		String genericType = signature.substring(signature.indexOf("<") + 1, signature.indexOf(">"));

		StringTokenizer tokenizer = new StringTokenizer(genericType, ";");

		List<String> genericTypes = new ArrayList<>(tokenizer.countTokens());

		while (tokenizer.hasMoreTokens()) {
			genericTypes.add(tokenizer.nextToken() + ";");
		}

		return Collections.unmodifiableList(genericTypes);
	}

	static FieldData getField(List<FieldData> fields, String name, String type) {
		return fields.stream().filter(field -> field.getName().equals(name) && field.getDataType().equals(type))
				.findAny()
				// can't be null cause the classfile would be otherwise incorrect
				.orElse(null);
	}
}
