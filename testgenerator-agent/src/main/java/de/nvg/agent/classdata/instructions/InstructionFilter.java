package de.nvg.agent.classdata.instructions;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import de.nvg.testgenerator.classdata.constants.JavaTypes;
import de.nvg.testgenerator.classdata.constants.Primitives;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class InstructionFilter {

	private static final Logger LOGGER = LogManager.getLogger(InstructionFilter.class);

	private final List<Instruction> instructions;

	public InstructionFilter(List<Instruction> instructions) {
		this.instructions = simplifyInstructionTree(instructions);
	}

	public final Instruction filterForAloadInstruction(Instruction instruction) {
		LOGGER.debug("searching for instruction-caller of " + instruction);

		Stack<String> operandStack = new Stack<>();
		operandStack.push(instruction.getClassRef());
		operandStack.push(Descriptor.toClassName(instruction.getType()));

		return filterForInstructionCallerIntern(instruction,
				Instructions.getBeforeInstruction(instructions, instruction), operandStack);
	}

	/**
	 * filtering throw the instructions of a method and returns the caller of the
	 * searchInstruction. To reach this goal, a operandStack is build, in reserve
	 * order to the normal operandstack the jvm is using.
	 * 
	 * @param searchInstruction
	 * @param currentInstruction
	 * @param numberOfParameters
	 * @return the caller-instruction of the searchInstruction
	 */
	private Instruction filterForInstructionCallerIntern(Instruction searchInstruction, Instruction instruction,
			Stack<String> operandStack) {
		LOGGER.debug("search-instruction " + searchInstruction + "\n" + //
				"current-instruction " + instruction + "\n" + //
				"current operandStack: " + operandStack + "\n");

		if (Instructions.isLoadInstruction(instruction)) {
			operandStack.pop();

			// remove the first entry in the operand-stack for aconst_null instruction
		} else if (Opcode.ACONST_NULL == instruction.getOpcode() || //
		// pop the element, before the dup instruction the first and second-instructions
		// should be of the same type
				Opcode.DUP == instruction.getOpcode() || //
				Opcode.NEW == instruction.getOpcode() || Opcode.GETSTATIC == instruction.getOpcode()) {

			operandStack.pop();

		} else if (Opcode.GETFIELD == instruction.getOpcode()) {
			operandStack.pop();
			operandStack.push(instruction.getClassRef());

		} else if (Instructions.isInvokeInstruction(instruction)) {
			// update the instruction to the instruction, who starts the invoke-instruction
			instruction = filterForInstructionBeforeInvokeInstruction(searchInstruction, instruction, operandStack);
		} else if (Instructions.isPrimitiveMathOperation(instruction)) {
			String type = operandStack.peek();
			// push the same type to the operandStack, cause the primitive-math-operations
			// need always the primitive type 2 times for execution of the instruction
			operandStack.push(type);
		} else if (Instructions.isOneItemComparison(instruction)) {
			if (Opcode.IFNULL == instruction.getOpcode() || Opcode.IFNONNULL == instruction.getOpcode()) {
				// can't know which type gets popped, so a the most common type is pushed
				operandStack.push(JavaTypes.OBJECT);
			} else {
				// all other one-item-comparisions like IFEQ popping an int
				operandStack.push(Primitives.JAVA_INT);
			}
		} else if (Instructions.isTwoItemComparison(instruction)) {
			if (Opcode.IF_ACMPEQ == instruction.getOpcode() || Opcode.IF_ACMPNE == instruction.getOpcode()) {
				// can't know which types gets popped, so a the most common types are pushed
				operandStack.push(JavaTypes.OBJECT);
				operandStack.push(JavaTypes.OBJECT);
			} else {
				// all other two-item-comparisions like IF_ICMPEQ popping ints
				operandStack.push(Primitives.JAVA_INT);
				operandStack.push(Primitives.JAVA_INT);
			}
		}

		Instruction beforeInstruction = Instructions.getBeforeInstruction(instructions, instruction);

		if (operandStack.isEmpty()) {
			return instruction;
		} else if (beforeInstruction != null) {
			return filterForInstructionCallerIntern(searchInstruction, beforeInstruction, operandStack);
		}

		throw new NoSuchElementException(
				"No matching caller-instruction for search-instruction " + searchInstruction + " found");
	}

	private Instruction filterForInstructionBeforeInvokeInstruction(Instruction searchInstruction,
			Instruction invokeInstruction, Stack<String> operandStack) {
		List<String> methodParams = Instructions.getMethodParams(invokeInstruction.getType());
		// reverse the list cause the lifo-principle ->last methodparameter gets pushed
		// first
		Collections.reverse(methodParams);

		String returnType = Instructions.getReturnType(invokeInstruction.getType());

		if (!Primitives.JVM_VOID.equals(returnType)) {
			operandStack.pop();
		}

		Stack<String> methodOperandStack = new Stack<>();

		if (Opcode.INVOKESTATIC != invokeInstruction.getOpcode()) {
			methodOperandStack.add(invokeInstruction.getClassRef());
		}

		for (String param : methodParams) {
			methodOperandStack.push(Descriptor.toClassName(param));
		}

		return filterForInstructionCallerIntern(invokeInstruction,
				Instructions.getBeforeInstruction(instructions, invokeInstruction), methodOperandStack);
	}

	/**
	 * Modifies the instruction-set of a method in the case that a if-else-cascade
	 * exists to manipulate the value of a field. Then will the else-instructions
	 * excluded from the modified instructionsset. This is necessary to enable the
	 * reverse filtering of the correct aload-instruction of a field.
	 * 
	 * @param instructions
	 * @return a modified-instructionsset without else-branch bytecodes, if a field
	 *         gets manipulated directly after the last instruction of the
	 *         else-branch
	 */
	private List<Instruction> simplifyInstructionTree(List<Instruction> instructions) {
		Optional<Instruction> branchOptional = instructions.stream()
				.filter(inst -> Instructions.isOneItemComparison(inst) || Instructions.isTwoItemComparison(inst))
				.findAny();

		if (branchOptional.isPresent()) {
			Instruction branchInstruction = branchOptional.get();

			int ifEnd = branchInstruction.getCodeArrayIndex() + branchInstruction.getOffset();
			Optional<Instruction> gotoOptional = instructions.stream()
					.filter(inst -> Opcode.GOTO == inst.getOpcode() && ifEnd > inst.getCodeArrayIndex())//
					.findAny();

			if (gotoOptional.isPresent()) {
				Instruction gotoInstruction = gotoOptional.get();

				int branchEnd = gotoInstruction.getCodeArrayIndex() + gotoInstruction.getOffset();

				if (instructions.stream().anyMatch(
						inst -> Opcode.PUTFIELD == inst.getOpcode() && branchEnd == inst.getCodeArrayIndex())) {
					List<Instruction> modifiedInstructionSet = instructions.stream()
							.filter(inst -> gotoInstruction.getCodeArrayIndex() > inst.getCodeArrayIndex()
									|| inst.getCodeArrayIndex() >= branchEnd)
							.collect(Collectors.toList());

					return modifiedInstructionSet;
				}
			}
		}

		return instructions;
	}

}
