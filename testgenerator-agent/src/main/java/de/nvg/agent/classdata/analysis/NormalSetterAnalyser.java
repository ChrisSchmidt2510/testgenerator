package de.nvg.agent.classdata.analysis;

import java.util.List;

import org.testgen.core.Wrapper;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.FieldData;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class NormalSetterAnalyser implements MethodAnalysis {

	@Override
	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {

		for (int index = 0; index < instructions.size(); index++) {
			Instruction instruction = instructions.get(index);

			if (Opcode.PUTFIELD == instruction.getOpcode()) {

				List<String> parameters = Instructions.getMethodParams(descriptor);

				if (!parameters.isEmpty() && instructions.size() < 25 && //
						AnalysisHelper.isDescriptorEqual(instructions, index, //
								instruction.getType(), parameters, new Wrapper<>())) {

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
