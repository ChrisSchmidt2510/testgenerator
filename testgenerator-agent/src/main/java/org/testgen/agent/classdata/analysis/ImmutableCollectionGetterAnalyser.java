package org.testgen.agent.classdata.analysis;

import java.util.List;

import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.core.Wrapper;

import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class ImmutableCollectionGetterAnalyser implements MethodAnalysis {

	@Override
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode()) {

				Instruction invokeStatic = instructions.get(i + 1);

				if (JVMTypes.COLLECTION_TYPES.contains(instruction.getType())
						&& Opcode.INVOKESTATIC == invokeStatic.getOpcode()
						&& JavaTypes.COLLECTIONS.equals(invokeStatic.getClassRef())
						&& invokeStatic.getName().startsWith("unmodifiable") && instructions.size() >= i + 2
						&& Opcode.ARETURN == instructions.get(i + 2).getOpcode()) {

					FieldData field = new FieldData.Builder().withName(instruction.getName())
							.withDataType(Descriptor.toClassName(instruction.getType())).build();

					fieldWrapper.setValue(field);

					return true;
				}
			}
		}

		return false;
	}

}
