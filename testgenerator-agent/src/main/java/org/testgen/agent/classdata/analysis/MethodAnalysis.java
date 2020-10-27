package org.testgen.agent.classdata.analysis;

import java.util.List;

import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.core.Wrapper;

@FunctionalInterface
public interface MethodAnalysis {

	boolean analyse(String descriptor, List<Instruction> instructions, Wrapper<FieldData> fieldWrapper);
}
