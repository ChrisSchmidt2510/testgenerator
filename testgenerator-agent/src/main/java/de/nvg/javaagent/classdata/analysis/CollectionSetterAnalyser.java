package de.nvg.javaagent.classdata.analysis;

import java.util.List;
import java.util.stream.Collectors;

import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.constants.JVMTypes;
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
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
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

						List<String> params = AnalysisHelper.getMethodParams(descriptor);

						List<String> genericTypes = AnalysisHelper.getGenericTypesFromSignature(field.getSignature());

						if (checkParameterAndMethod(params, genericTypes, index)) {
							fieldWrapper.setValue(field);

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean checkParameterAndMethod(List<String> methodParameter, List<String> collectionGenericType,
			int instructionIndex) {

		if (methodParameter.equals(collectionGenericType) && instructions.size() < 25) {

			if (Opcode.INVOKEINTERFACE == instructions.get(instructionIndex + 2).getOpcode()) {

				Instruction bytecodeInvokeInterface = instructions.get(instructionIndex + 2);

				List<String> interfaceParams = AnalysisHelper.getMethodParams(bytecodeInvokeInterface.getType());

				List<String> methodNames = JVMTypes.COLLECTION_ADD_METHODS
						.get(Descriptor.of(bytecodeInvokeInterface.getClassRef()));

				if (Opcode.ALOAD_1 == instructions.get(instructionIndex + 1).getOpcode() && methodNames != null
						&& methodNames.contains(bytecodeInvokeInterface.getName()) && interfaceParams.size() == 1
						// TODO Anzahl Argumente anpassen
						&& JVMTypes.OBJECT.equals(interfaceParams.get(0))) {

					return true;
				}
			}
		}

		return false;
	}

}
