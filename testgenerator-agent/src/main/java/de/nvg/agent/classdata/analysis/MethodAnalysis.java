package de.nvg.agent.classdata.analysis;

import java.util.List;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.testgenerator.Wrapper;

public interface MethodAnalysis {

	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper);
}
