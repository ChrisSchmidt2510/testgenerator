package de.nvg.javaagent.classdata.analysis;

import java.util.List;

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

	public CollectionSetterAnalyser(List<FieldData> fields) {
		this.fields = fields;
	}

	@Override
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		for (int index = 0; index < instructions.size(); index++) {
			Instruction instruction = instructions.get(index);

			if (Opcode.GETFIELD == instruction.getOpcode()
					&& JVMTypes.COLLECTION_TYPES.contains(instruction.getType())) {

				FieldData field = AnalysisHelper.getField(fields, instruction.getName(),
						Descriptor.toClassName(instruction.getType()));

				List<String> params = AnalysisHelper.getMethodParams(descriptor);

				List<String> genericTypes = AnalysisHelper.getGenericTypesFromSignature(field.getSignature());

				if (params.size() == 1 && genericTypes.size() == 1 && params.get(0).equals(genericTypes.get(0))
						&& instructions.size() < 25) {

					Instruction bytecodeInvokeInterface = instructions.get(index + 2);

					List<String> interfaceParams = AnalysisHelper.getMethodParams(bytecodeInvokeInterface.getType());

					if (Opcode.ALOAD_1 == instructions.get(index + 1).getOpcode()
							&& Opcode.INVOKEINTERFACE == bytecodeInvokeInterface.getOpcode()
							&& JVMTypes.COLLECTION_METHOD_ADD.equals(bytecodeInvokeInterface.getName())
							&& interfaceParams.size() == 1 && JVMTypes.OBJECT.equals(interfaceParams.get(0))) {

						fieldWrapper.setValue(field);
						return true;
					}
				}
			}
		}

		return false;
	}

}
