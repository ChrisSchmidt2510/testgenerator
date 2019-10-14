package de.nvg.javaagent.classdata.analysis;

import java.util.List;

import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.testgenerator.Wrapper;

public interface MethodAnalysis {

	public boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper);
}
