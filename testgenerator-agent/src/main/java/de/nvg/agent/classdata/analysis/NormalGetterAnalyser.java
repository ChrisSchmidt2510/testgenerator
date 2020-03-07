package de.nvg.agent.classdata.analysis;

import java.util.List;
import java.util.Map;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.testgenerator.MapBuilder;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.constants.Primitives;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class NormalGetterAnalyser implements MethodAnalysis {

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

	private final String className;

	public NormalGetterAnalyser(String className) {
		this.className = className;
	}

	@Override
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode() && className.equals(instruction.getClassRef())) {

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
