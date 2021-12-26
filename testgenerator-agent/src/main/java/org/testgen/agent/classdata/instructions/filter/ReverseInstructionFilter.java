package org.testgen.agent.classdata.instructions.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;

import javassist.bytecode.BootstrapMethodsAttribute.BootstrapMethod;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class ReverseInstructionFilter extends InstructionFilter {

	private static final List<Integer> POP_OPCODES = Collections.unmodifiableList(Arrays.asList(
			// remove the first entry in the operand-stack for aconst_null instruction
			Opcode.ACONST_NULL, Opcode.NEW, Opcode.GETSTATIC, Opcode.PUTSTATIC, //
			// pop the element, before the dup instruction the first and second-instructions
			// should be of the same type
			// TODO add putstatic opcode to filter For InstructionCallerIntern
			Opcode.DUP, Opcode.LDC, Opcode.LDC_W, Opcode.LDC2_W));

	public ReverseInstructionFilter(ClassFile classFile, List<Instruction> instructions) {
		super(classFile, instructions);
	}

	public final Instruction filterForAloadInstruction(Instruction instruction) {
		logger.debug("searching for instruction-caller of " + instruction);

		calledLoadInstructions.clear();

		LinkedList<String> operandStack = new LinkedList<>();

		return filterForInstructionCallerIntern(instruction, operandStack);
	}

	public List<Instruction> filterForCalledLoadInstructions(Instruction instruction) {
		logger.debug("searching for arguments of invoke instruction " + instruction);

		calledLoadInstructions.clear();

		LinkedList<String> operandStack = new LinkedList<>();

		if (Instructions.isInvokeInstruction(instruction)) {
			String returnType = Instructions.getReturnType(instruction.getType());

			if (!Primitives.JVM_VOID.equals(returnType))
				operandStack.add(returnType);
		}

		filterForInstructionCallerIntern(instruction, operandStack);

		return calledLoadInstructions;
	}

	/**
	 * filtering throw the instructions of a method and returns the caller of the
	 * searchInstruction. To reach this goal, a operandStack is build, in reverse
	 * order to the normal operand stack the jvm is using.
	 * 
	 * @param currentInstruction
	 * @param numberOfParameters
	 * @return the caller-instruction of the searchInstruction
	 */
	private Instruction filterForInstructionCallerIntern(Instruction instruction, LinkedList<String> operandStack) {
		logger.debug("current-instruction " + instruction + "\n" + //
				"current operandStack: " + operandStack + "\n");

		int opcode = instruction.getOpcode();

		if (Instructions.isLoadInstruction(instruction)) {
			operandStack.pop();
			calledLoadInstructions.add(instruction);

		} else if (POP_OPCODES.contains(opcode)) {
			operandStack.pop();

		} else if (Opcode.GETFIELD == opcode) {
			operandStack.pop();
			operandStack.push(instruction.getClassRef());

		} else if (Opcode.PUTFIELD == opcode) {
			operandStack.push(instruction.getClassRef());
			operandStack.push(Descriptor.toClassName(instruction.getType()));

		} else if (Instructions.isInvokeInstruction(instruction)) {
			// update the instruction to the instruction, who starts the invoke-instruction
			instruction = filterForInstructionBeforeInvokeInstruction(instruction, operandStack);

		} else if (Opcode.INVOKEDYNAMIC == opcode) {

			instruction = filterForInstructionBeforeInvokeDynamicInstruction(instruction, operandStack);

		} else if (Instructions.isPrimitiveMathOperation(instruction)) {
			String type = operandStack.peek();
			// push the same type to the operandStack, cause the primitive-math-operations
			// need always the primitive type 2 times for execution of the instruction
			operandStack.push(type);

		} else if (Instructions.isOneItemComparison(instruction)) {
			if (Opcode.IFNULL == opcode || Opcode.IFNONNULL == opcode) {
				// can't know which type gets popped, so the most common type is pushed
				operandStack.push(JavaTypes.OBJECT);
			} else {
				// all other one-item-comparisons like IFEQ popping an int
				operandStack.push(Primitives.JAVA_INT);
			}

		} else if (Instructions.isTwoItemComparison(instruction)) {
			if (Opcode.IF_ACMPEQ == opcode || Opcode.IF_ACMPNE == opcode) {
				// can't know which types gets popped, so the most common types are pushed
				operandStack.push(JavaTypes.OBJECT);
				operandStack.push(JavaTypes.OBJECT);
			} else {
				// all other two-item-comparisons like IF_ICMPEQ popping int's
				operandStack.push(Primitives.JAVA_INT);
				operandStack.push(Primitives.JAVA_INT);
			}
		} else if (Instructions.isPrimitiveCast(instruction)) {
			operandStack.pop();

			String dataType = Instructions.getPrimitiveCastType(instruction);
			operandStack.push(dataType);

		} else if (Instructions.isArrayStoreInstruction(instruction)) {
			// can't know which type gets inserted in the array, so the most common type is
			// pushed
			operandStack.push(JavaTypes.OBJECT);
			// array-index
			operandStack.push(Primitives.JAVA_INT);
			// can't know which type the array has, so the most common type is pushed
			operandStack.push(JavaTypes.OBJECT_ARRAY);

		} else if (Instructions.isArrayLoadInstruction(instruction)) {
			// array-index
			operandStack.push(Primitives.JAVA_INT);
			// can't know which type the array has, so the most common type is pushed
			operandStack.push(JavaTypes.OBJECT_ARRAY);

		} else if (Opcode.ANEWARRAY == opcode || Opcode.NEWARRAY == opcode) {
			// remove the type of the array from the stack
			operandStack.pop();
			// length of the array
			operandStack.push(Primitives.JAVA_INT);

		} else if (Opcode.MULTIANEWARRAY == opcode) {
			// remove arrayref
			operandStack.pop();

			for (int i = 0; i < instruction.getArrayDimensions(); i++) {
				operandStack.push(Primitives.JAVA_INT);
			}

		} else if (Opcode.DUP_X1 == opcode) {
			operandStack.remove(2);

		} else if (Opcode.DUP_X2 == opcode) {
			String type = operandStack.peek();
			operandStack.remove(isDoubleOrLong(type) ? 2 : 3);

		} else if (Opcode.DUP2 == opcode) {
			String type = operandStack.peek();

			if (isDoubleOrLong(type)) {
				operandStack.pop();

			} else {
				operandStack.remove(2);
				operandStack.remove(3);
			}

		} else if (Opcode.DUP2_X1 == opcode) {
			String type = operandStack.peek();

			if (isDoubleOrLong(type)) {
				operandStack.remove(2);

			} else {
				operandStack.remove(3);
				operandStack.remove(4);
			}

		} else if (Opcode.DUP2_X2 == opcode) {
			// Form 4
			if (isDoubleOrLong(operandStack.peek()) && isDoubleOrLong(operandStack.get(1))) {
				operandStack.remove(2);

				// Form 3
			} else if (isDoubleOrLong(operandStack.get(2))) {
				operandStack.remove(3);
				operandStack.remove(4);

				// Form 2
			} else if (isDoubleOrLong(operandStack.peek())) {
				operandStack.remove(3);

				// Form 1
			} else {
				operandStack.remove(4);
				operandStack.remove(5);
			}

		} else if (Opcode.INSTANCEOF == opcode) {
			operandStack.pop();
			operandStack.push(JVMTypes.OBJECT);

		}

		Instruction beforeInstruction = Instructions.getBeforeInstruction(instructions, instruction);

		if (operandStack.isEmpty()) {
			return instruction;
		} else if (beforeInstruction != null) {
			return filterForInstructionCallerIntern(beforeInstruction, operandStack);
		}

		throw new NoSuchElementException("No matching caller-instruction found");
	}

	/**
	 * Filtering for the call-instruction of the method-invocation. If the
	 * invoke-instruction has a return-type, it pops the return-type from the
	 * committed operand Stack. In order to get the call-instruction a new
	 * operandStack is created and the parameter of the invoke-instruction getting
	 * pushed. After that the method
	 * {@link ReverseInstructionFilter#filterForInstructionCallerIntern(Instruction, Instruction, Deque)}
	 * gets called. <br>
	 * Exclusion: if the invoke-instruction has the Opcode INVOKE_STATIC and the
	 * method has no parameters, the invokeInstruction gets returned
	 * 
	 * @param invokeInstruction
	 * @param operandStack
	 * @return
	 */
	private Instruction filterForInstructionBeforeInvokeInstruction(Instruction invokeInstruction,
			Deque<String> operandStack) {

		String returnType = Instructions.getReturnType(invokeInstruction.getType());

		if (!Primitives.JVM_VOID.equals(returnType)) {
			operandStack.pop();
		}

		List<String> methodParams = Instructions.getMethodParams(invokeInstruction.getType());
		if (methodParams.isEmpty() && Opcode.INVOKESTATIC == invokeInstruction.getOpcode()) {
			return invokeInstruction;
		}

		LinkedList<String> methodOperandStack = new LinkedList<>();

		if (Opcode.INVOKESTATIC != invokeInstruction.getOpcode()) {
			methodOperandStack.add(invokeInstruction.getClassRef());
		}

		for (String param : methodParams) {
			methodOperandStack.push(Descriptor.toClassName(param));
		}

		return filterForInstructionCallerIntern(Instructions.getBeforeInstruction(instructions, invokeInstruction),
				methodOperandStack);
	}

	private Instruction filterForInstructionBeforeInvokeDynamicInstruction(Instruction invokeDynamicInstruction,
			Deque<String> operandStack) {
		operandStack.pop();

		BootstrapMethod bootstrapMethod = getBootstrapMethodForInvokeDynamicInstruction(invokeDynamicInstruction);

		int methodRefIndex = constantPool.getMethodHandleIndex(bootstrapMethod.methodRef);

		String name = constantPool.getMethodrefName(methodRefIndex);
		String type = constantPool.getMethodrefClassName(methodRefIndex);

		if ((LAMBDAMETAFACTORY_METHOD_METAFACTORY.equals(name) || LAMBDAMETAFACTORY_METHOD_ALT_METAFACTORY.equals(name))
				&& LAMBDAMETAFACTORY_CLASSNAME.equals(type)) {
			String typedLambdaDesc = constantPool
					.getUtf8Info(constantPool.getMethodTypeInfo(bootstrapMethod.arguments[2]));
			List<String> lambdaOriginalParameters = Instructions.getMethodParams(typedLambdaDesc);

			String lambdaImplDesc = constantPool
					.getMethodrefType(constantPool.getMethodHandleIndex(bootstrapMethod.arguments[1]));
			List<String> lambdaImplParameters = Instructions.getMethodParams(lambdaImplDesc);

			List<String> additionalParams = lambdaImplParameters.stream()
					.filter(param -> !lambdaOriginalParameters.contains(param)).collect(Collectors.toList());

			LinkedList<String> invokeDynamicOperandStack = new LinkedList<>();
			additionalParams.forEach(invokeDynamicOperandStack::push);

			return filterForInstructionCallerIntern(
					Instructions.getBeforeInstruction(instructions, invokeDynamicInstruction),
					invokeDynamicOperandStack);

		} else
			logger.error("can't process invokedynamic instruction " + invokeDynamicInstruction);

		return invokeDynamicInstruction;
	}

}
