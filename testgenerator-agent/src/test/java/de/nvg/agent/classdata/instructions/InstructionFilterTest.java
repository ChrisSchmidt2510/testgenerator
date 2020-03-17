package de.nvg.agent.classdata.instructions;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.nvg.agent.classdata.TestHelper;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class InstructionFilterTest extends TestHelper {

	@Test
	public void testFilterForInstructionCallerWithSimplestSetter() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Adresse", "setStrasse", Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has only 1 putField-instruction so-> get(0)
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		Assert.assertEquals(0, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallerWithOpcodeDup() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.FragmentDate", "addMonths", Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		// aload-instruction has the index 10 or 11 cause, if the test gets compiled
		// with the java compiler from the jdk,
		// the dup instruction becomes a aload0 instruction and the codearrayindex
		// becomes 10
		// if the test-gets compiled with the eclipse-java-compiler the codeArrayIndex
		// is 11
		Assert.assertTrue(aloadInstruction.getCodeArrayIndex() == 11 || aloadInstruction.getCodeArrayIndex() == 10);
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());

	}

	@Test
	public void testFilterForInstructionCallWithOpcodeAconstNull() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.FragmentDate", "setDate", "(Ljava/util/Date;)V",
				Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		Assert.assertEquals(4, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithOpcodeNew() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.FragmentDate", "setDate", "(Ljava/util/Date;)V",
				Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);
		// for this test we need the put-field-instruction at index 54
		Instruction searchInstruction = filteredInstructions.get(Opcode.PUTFIELD).stream()
				.filter(inst -> inst.getCodeArrayIndex() == 54).findAny().orElse(null);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter.filterForAloadInstruction(searchInstruction);

		Assert.assertEquals(41, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithObjectCreationForInterface() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Person", MethodInfo.nameInit, Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		Assert.assertEquals(4, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithSetFieldWithConstant() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Value", MethodInfo.nameInit, Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		Assert.assertEquals(8, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithIfElseBranch() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Value", "setValue", Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		Assert.assertEquals(0, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithIfBranch() throws NotFoundException, BadBytecode {
		init("de.nvg.agent.classdata.testclasses.Value", "setValueID", Arrays.asList(Opcode.PUTFIELD));

		Instructions.showCodeArray(System.out, codeAttribute.iterator(), constantPool);

		InstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		Assert.assertEquals(4, aloadInstruction.getCodeArrayIndex());
		Assert.assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	private InstructionFilter createInstructionFilter() {
		return new InstructionFilter(instructions);
	}
}
