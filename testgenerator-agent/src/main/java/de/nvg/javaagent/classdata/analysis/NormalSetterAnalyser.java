package de.nvg.javaagent.classdata.analysis;

import java.util.List;

import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.testgenerator.Wrapper;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class NormalSetterAnalyser implements MethodAnalysis {

	@Override
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {

		for (int index = 0; index < instructions.size(); index++) {
			Instruction instruction = instructions.get(index);

			if (Opcode.PUTFIELD == instruction.getOpcode()) {

				List<String> parameters = AnalysisHelper.getMethodParams(descriptor);

				if (AnalysisHelper.isDescriptorEqual(instructions, index, instruction.getType(), parameters,
						new Wrapper<>()) && instructions.size() < 25) {

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
