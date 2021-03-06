package org.testgen.agent.classdata.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.core.Wrapper;

import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

/**
 * Can be used for a Collection, a List, a Queue and a Set
 * 
 * @author Christoph
 *
 */
public class CollectionSetterAnalyser implements MethodAnalysis {
	private final List<FieldData> fields;
	private List<Instruction> instructions;

	public CollectionSetterAnalyser(List<FieldData> fields) {
		this.fields = fields;
	}

	@Override
	public boolean analyse(String descriptor, final List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		this.instructions = instructions;

		List<Instruction> filteredInstructions = instructions.stream()
				.filter(inst -> Opcode.GETFIELD == inst.getOpcode()).collect(Collectors.toList());

		if (filteredInstructions != null) {

			for (Instruction getFieldInstruction : filteredInstructions) {

				for (int index = instructions.indexOf(getFieldInstruction); index < instructions.size(); index++) {
					Instruction instruction = instructions.get(index);

					if (Opcode.GETFIELD == instruction.getOpcode()
							&& JVMTypes.COLLECTION_TYPES.contains(instruction.getType())) {

						FieldData field = AnalysisHelper.getField(fields, instruction.getName(),
								Descriptor.toClassName(instruction.getType()));

						// if field is null, the field isnt in the class -> checked method can't be a
						// setter for this field
						if (field != null) {
							List<String> params = Instructions.getMethodParams(descriptor);

							List<String> genericTypes = field.getSignature() != null ? field.getSignature()
									.getSubTypes().stream().map(SignatureData::getType).collect(Collectors.toList())
									: Collections.emptyList();

							if (checkParameterAndMethod(params, genericTypes, index)) {
								fieldWrapper.setValue(field);

								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean checkParameterAndMethod(List<String> methodParameter, List<String> collectionGenericType,
			int instructionIndex) {

		if (methodParameter.equals(collectionGenericType) && instructions.size() < 25
				&& Opcode.INVOKEINTERFACE == instructions.get(instructionIndex + 1 + collectionGenericType.size())
						.getOpcode()) {

			Instruction bytecodeInvokeInterface = instructions.get(instructionIndex + 1 + collectionGenericType.size());

			List<String> interfaceParams = Instructions.getMethodParams(bytecodeInvokeInterface.getType());

			List<String> methodNames = JVMTypes.COLLECTION_ADD_METHODS
					.get(Descriptor.of(bytecodeInvokeInterface.getClassRef()));

			List<String> paramList = createParamList(interfaceParams.size());

			if (methodNames != null && methodNames.contains(bytecodeInvokeInterface.getName())
					&& interfaceParams.size() == methodParameter.size() && paramList.equals(interfaceParams)) {

				return true;
			}
		}

		return false;
	}

	private List<String> createParamList(int size) {
		List<String> params = new ArrayList<>();

		for (int i = 0; i < size; i++) {
			params.add(JVMTypes.OBJECT);
		}

		return params;
	}

}
