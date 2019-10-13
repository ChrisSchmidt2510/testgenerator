package de.nvg.javaagent.classdata.analysis;

import java.util.List;
import java.util.Map;

import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.testgenerator.MapBuilder;
import de.nvg.testgenerator.Wrapper;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class NormalGetterAnalyser implements MethodAnalysis {

	private static final Map<String, Integer> PRIMITIVE_RETURN_OPCODES = //
			MapBuilder.<String, Integer>hashMapBuilder()//
					.add("I", Opcode.IRETURN).add("F", Opcode.FRETURN)//
					.add("D", Opcode.DRETURN).add("L", Opcode.LRETURN)//
					.add("[", Opcode.ARETURN).toUnmodifiableMap();

	@Override
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode()) {

				Integer returnOpcode = PRIMITIVE_RETURN_OPCODES.get(instruction.getType());
				int opcode = instructions.get(i + 1).getOpcode();

				if ((returnOpcode != null && returnOpcode.equals(opcode)) || Opcode.ARETURN == opcode) {

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
