package org.testgen.agent.classdata.instructions.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.FragmentDate;
import org.testgen.agent.classdata.testclasses.Person;
import org.testgen.agent.classdata.testclasses.Switch;
import org.testgen.agent.classdata.testclasses.Value;

import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class ReverseInstructionFilterTest extends TestHelper {

	@Test
	public void testFilterForInstructionCallerWithSimplestSetter() throws NotFoundException, BadBytecode {
		init(Adresse.class, "setStrasse", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has only 1 putField-instruction so-> get(0)
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(0, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallerWithOpcodeDup() throws NotFoundException, BadBytecode {
		init(FragmentDate.class, "addMonths", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

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
		assertTrue(aloadInstruction.getCodeArrayIndex() == 11 || aloadInstruction.getCodeArrayIndex() == 10);
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());

	}

	@Test
	public void testFilterForInstructionCallWithOpcodeAconstNull() throws NotFoundException, BadBytecode {
		init(FragmentDate.class, "setDate", "(Ljava/util/Date;)V", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(4, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithOpcodeNew() throws NotFoundException, BadBytecode {
		init(FragmentDate.class, "setDate", "(Ljava/util/Date;)V", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));
		// for this test we need the put-field-instruction at index 54
		Instruction searchInstruction = filteredInstructions.get(Opcode.PUTFIELD).stream()
				.filter(inst -> inst.getCodeArrayIndex() == 54).findAny().orElse(null);

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter.filterForAloadInstruction(searchInstruction);

		assertEquals(41, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithObjectCreationForInterface() throws NotFoundException, BadBytecode {
		init(Person.class, MethodInfo.nameInit, Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(4, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithSetFieldWithConstant() throws NotFoundException, BadBytecode {
		init(Value.class, MethodInfo.nameInit, "(Ljava/lang/Integer;Ljava/util/Calendar;)V",
				Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(8, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithIfElseBranch() throws NotFoundException, BadBytecode {
		init(Value.class, "setValue", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(0, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterForInstructionCallWithIfBranch() throws NotFoundException, BadBytecode {
		init(Value.class, "setValueID", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// this method has multiple putField-instructions, but only the first is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(4, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterWithPrimitveCasts() throws NotFoundException, BadBytecode {
		init(Value.class, "setSmallValue", Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));
		
		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				// for this method only the first aload-instruction is for
				// this test interesting
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(0, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterWithArrayCreation() throws NotFoundException, BadBytecode {
		init(Switch.class, MethodInfo.nameInit, Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));
		
		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(1));

		assertEquals(27, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	@Test
	public void testFilterWithArrayMultiMemberCreation() throws NotFoundException, BadBytecode {
		init(Switch.class, MethodInfo.nameInit, Arrays.asList(Opcode.PUTFIELD));

		System.out.print(Instructions.printCodeArray(codeAttribute.iterator(), constantPool));

		ReverseInstructionFilter filter = createInstructionFilter();

		Instruction aloadInstruction = filter
				.filterForAloadInstruction(filteredInstructions.get(Opcode.PUTFIELD).get(0));

		assertEquals(4, aloadInstruction.getCodeArrayIndex());
		assertEquals(Opcode.ALOAD_0, aloadInstruction.getOpcode());
	}

	private ReverseInstructionFilter createInstructionFilter() {
		return new ReverseInstructionFilter(classFile, instructions);
	}
}
