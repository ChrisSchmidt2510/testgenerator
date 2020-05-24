package de.nvg.agent.classdata.analysis;

import java.util.List;

import org.testgen.core.Wrapper;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.FieldData;

@FunctionalInterface
public interface MethodAnalysis {

	boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper);
}
