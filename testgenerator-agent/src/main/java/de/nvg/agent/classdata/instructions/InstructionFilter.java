package de.nvg.agent.classdata.instructions;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import de.nvg.testgenerator.classdata.constants.Primitives;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class InstructionFilter {

	private static final Logger LOGGER = LogManager.getLogger(InstructionFilter.class);

	private final List<Instruction> instructions;

	public InstructionFilter(List<Instruction> instructions) {
		this.instructions = instructions;
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

		} else if (Opcode.ACONST_NULL == instruction.getOpcode()) {
			// remove the first entry in the operand-stack for aconst_null instruction
			operandStack.pop();
		} else if (Opcode.DUP == instruction.getOpcode()) {
			// pop the element, before the dup instruction the first and second-instructions
			// should be of the same type
			operandStack.pop();

		} else if (Opcode.NEW == instruction.getOpcode()) {
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

}
