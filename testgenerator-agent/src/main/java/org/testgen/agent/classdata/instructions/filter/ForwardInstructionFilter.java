package org.testgen.agent.classdata.instructions.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;

import javassist.bytecode.BootstrapMethodsAttribute.BootstrapMethod;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Opcode;

public class ForwardInstructionFilter extends InstructionFilter {

	public ForwardInstructionFilter(ClassFile classFile, List<Instruction> instructions) {
		super(classFile, instructions);
	}

	public Instruction filterForUseOfGetFieldInstruction(Instruction instruction) {
		if (Opcode.GETFIELD != instruction.getOpcode())
			throw new IllegalArgumentException(String.format("Instruction %s has no Opcode GETFIELD", instruction));

		OperandStack operandStack = new OperandStack();
		operandStack.push(JavaTypes.OBJECT, null);

		Predicate<OperandStack> breakCondition = stack -> !stack.contains(instruction.getType(), instruction);

		return filterForInstructionCallerIntern(instruction, operandStack, breakCondition);
	}

	private Instruction filterForInstructionCallerIntern(Instruction instruction, OperandStack operandStack,
			Predicate<OperandStack> breakCondition) {
		ListIterator<Instruction> iterator = instructions.listIterator(instructions.indexOf(instruction) + 1);

		// implNote:
		// - all opcodes that don't change the depth of the operand stack are ignored
		// e.g all cast opcodes

		// problems to solve:
		// - how to solve problems with branch opcodes for e.g all if-opcodes or
		// -> all if-opcodes arent supported, only short if will be supported
		// to filter these stackmaptable is needed
		// switch-opcodes
		// - how to solve problems with catch-clauses or finally blocks
		// - how to merge the result of multiple codepaths?
		// -> multiple codepaths are not supported, only 1 one statement can be filtered
		int opcode = instruction.getOpcode();

		if (Instructions.isLoadInstruction(instruction)) {
			calledLoadInstructions.add(instruction);

			pushLoadInstructionOnStack(instruction, operandStack);

		} else if (Opcode.GETFIELD == opcode) {
			operandStack.pop();
			operandStack.push(instruction.getType(), instruction);

		} else if (Opcode.PUTFIELD == opcode) {
			operandStack.pop();
			operandStack.pop();

		} else if (Opcode.ACONST_NULL == opcode)
			operandStack.push(JavaTypes.OBJECT, instruction);

		else if (Opcode.NEW == opcode)
			operandStack.push(instruction.getClassRef(), instruction);

		else if (Opcode.GETSTATIC == opcode)
			operandStack.push(instruction.getType(), instruction);

		else if (Opcode.PUTSTATIC == opcode)
			operandStack.pop();

		else if (Opcode.DUP == opcode)
			operandStack.push(operandStack.peek(), instruction);

		else if (Opcode.DUP_X1 == opcode)
			// copy the top value of the stack two values down
			operandStack.add(2, operandStack.peek(), instruction);

		else if (Opcode.DUP_X2 == opcode)
			// if the second item on the stack is a double or long copy to the third else
			// fourth position on the operand stack
			operandStack.add(isDoubleOrLong(operandStack.get(1)) ? 2 : 3, operandStack.peek(), instruction);

		else if (Opcode.DUP2 == opcode)
			if (isDoubleOrLong(operandStack.peek()))
				operandStack.push(operandStack.peek(), instruction);

			else {
				operandStack.add(2, operandStack.peek(), instruction);
				operandStack.add(3, operandStack.get(1), instruction);
			}

		else if (Opcode.DUP2_X1 == opcode)
			if (isDoubleOrLong(operandStack.peek()))
				operandStack.add(2, operandStack.peek(), instruction);

			else {
				operandStack.add(3, operandStack.peek(), instruction);
				operandStack.add(4, operandStack.get(1), instruction);
			}

		else if (Opcode.DUP2_X2 == opcode)
			// Form 4
			if (isDoubleOrLong(operandStack.peek()) && isDoubleOrLong(operandStack.get(1))) {
				operandStack.add(2, operandStack.peek(), instruction);

				// Form 3
			} else if (isDoubleOrLong(operandStack.get(2))) {
				operandStack.add(3, operandStack.peek(), instruction);
				operandStack.add(4, operandStack.get(1), instruction);

				// Form 2
			} else if (isDoubleOrLong(operandStack.peek()))
				operandStack.add(3, operandStack.peek(), instruction);

			// Form 1
			else {
				operandStack.add(4, operandStack.peek(), instruction);
				operandStack.add(5, operandStack.get(1), instruction);
			}

		else if (Opcode.LDC == opcode || Opcode.LDC_W == opcode || Opcode.LDC2_W == opcode)
			operandStack.push(instruction.getType(), instruction);

		else if (Instructions.isPrimitiveMathOperation(instruction))
			// remove on of the two top variables, because the result of this math operation
			// is pushed back onto the Stack
			operandStack.pop();

		else if (Instructions.isOneItemComparison(instruction))
			operandStack.pop();

		else if (Instructions.isTwoItemComparison(instruction)) {
			operandStack.pop();
			operandStack.pop();
		}

		else if (Instructions.isArrayStoreInstruction(instruction)) {
			operandStack.pop();
			operandStack.pop();
			operandStack.pop();
		}

		else if (Instructions.isArrayLoadInstruction(instruction)) {
			operandStack.pop();
			operandStack.pop();

			operandStack.push(getArrayComponentType(opcode), instruction);
		}

		else if (Opcode.NEWARRAY == opcode || Opcode.ANEWARRAY == opcode) {
			operandStack.pop();

			operandStack.push(instruction.getType(), instruction);
		}

		else if (Opcode.MULTIANEWARRAY == opcode) {
			String arrayType = instruction.getType();

			for (int i = 0; i < instruction.getArrayDimensions(); i++) {
				operandStack.pop();
			}

			operandStack.push(arrayType, instruction);
		}

		else if (Opcode.INSTANCEOF == opcode)
			operandStack.push(Primitives.JAVA_INT, instruction);

		else if (Instructions.isInvokeInstruction(instruction))
			filterForInvokeInstruction(instruction, operandStack);

		else if (Opcode.INVOKEDYNAMIC == opcode)
			filterForInvokeDynamicInstruction(instruction, operandStack);

		if (operandStack.isEmpty() || breakCondition.test(operandStack))
			return instruction;

		else if (iterator.hasNext())
			return filterForInstructionCallerIntern(iterator.next(), operandStack, breakCondition);

		throw new NoSuchElementException("No matching caller-instruction found");
	}

	private void filterForInvokeInstruction(Instruction instruction, OperandStack operandStack) {
		List<String> methodParams = Instructions.getMethodParams(instruction.getType());

		methodParams.forEach(e -> operandStack.pop());

		if (Opcode.INVOKESTATIC != instruction.getOpcode())
			operandStack.pop();

		String returnType = Instructions.getReturnType(instruction.getType());

		if (!Primitives.JVM_VOID.equals(returnType))
			operandStack.push(returnType, instruction);
	}

	private void filterForInvokeDynamicInstruction(Instruction instruction, OperandStack operandStack) {

		BootstrapMethod bootstrapMethod = getBootstrapMethodForInvokeDynamicInstruction(instruction);

		int methodRefIndex = constantPool.getMethodHandleIndex(bootstrapMethod.methodRef);

		String name = constantPool.getMethodrefName(methodRefIndex);
		String type = constantPool.getMethodrefClassName(methodRefIndex);

		if ((LAMBDAMETAFACTORY_METHOD_METAFACTORY.equals(name) || LAMBDAMETAFACTORY_METHOD_ALT_METAFACTORY.equals(name))
				&& LAMBDAMETAFACTORY_CLASSNAME.equals(type)) {

			String lambdaImplDesc = constantPool
					.getMethodrefType(constantPool.getMethodHandleIndex(bootstrapMethod.arguments[1]));
			List<String> lambdaImplParameters = Instructions.getMethodParams(lambdaImplDesc);

			lambdaImplParameters.forEach(e -> operandStack.pop());

			operandStack.push(Instructions.getReturnType(instruction.getType()), instruction);

		} else
			logger.error("can't process invokedynamic instruction " + instruction);
	}

	static void pushLoadInstructionOnStack(Instruction instruction, OperandStack operandStack) {

		switch (instruction.getOpcode()) {
		case Opcode.ALOAD:
		case Opcode.ALOAD_0:
		case Opcode.ALOAD_1:
		case Opcode.ALOAD_2:
		case Opcode.ALOAD_3:
			// reference is pushed to the stack, so reference gets pushed onto the stack
			operandStack.push(JavaTypes.OBJECT, instruction);
			break;

		case Opcode.ILOAD:
		case Opcode.ILOAD_0:
		case Opcode.ILOAD_1:
		case Opcode.ILOAD_2:
		case Opcode.ILOAD_3:
		case Opcode.ICONST_0:
		case Opcode.ICONST_1:
		case Opcode.ICONST_2:
		case Opcode.ICONST_3:
		case Opcode.ICONST_4:
		case Opcode.ICONST_5:
		case Opcode.ICONST_M1:
		case Opcode.BIPUSH:
		case Opcode.SIPUSH:
			operandStack.push(Primitives.JAVA_INT, instruction);
			break;

		case Opcode.FLOAD:
		case Opcode.FLOAD_0:
		case Opcode.FLOAD_1:
		case Opcode.FLOAD_2:
		case Opcode.FLOAD_3:
		case Opcode.FCONST_0:
		case Opcode.FCONST_1:
		case Opcode.FCONST_2:
			operandStack.push(Primitives.JAVA_FLOAT, instruction);
			break;

		case Opcode.DLOAD:
		case Opcode.DLOAD_0:
		case Opcode.DLOAD_1:
		case Opcode.DLOAD_2:
		case Opcode.DLOAD_3:
		case Opcode.DCONST_0:
		case Opcode.DCONST_1:
			operandStack.push(Primitives.JAVA_DOUBLE, instruction);
			break;

		case Opcode.LLOAD:
		case Opcode.LLOAD_0:
		case Opcode.LLOAD_1:
		case Opcode.LLOAD_2:
		case Opcode.LLOAD_3:
		case Opcode.LCONST_0:
		case Opcode.LCONST_1:
			operandStack.push(Primitives.JAVA_LONG, instruction);
			break;
		}

	}

	static String getArrayComponentType(int opcode) {
		switch (opcode) {
		case Opcode.IALOAD:
			return Primitives.JAVA_INT;

		case Opcode.LALOAD:
			return Primitives.JAVA_LONG;

		case Opcode.FALOAD:
			return Primitives.JAVA_FLOAT;

		case Opcode.DALOAD:
			return Primitives.JAVA_DOUBLE;

		case Opcode.AALOAD:
			return JavaTypes.OBJECT;

		case Opcode.BALOAD:
			return Primitives.JAVA_BYTE;

		case Opcode.CALOAD:
			return Primitives.JAVA_CHAR;

		case Opcode.SALOAD:
			return Primitives.JAVA_SHORT;
		default:
			throw new IllegalArgumentException("invalid array load instruction");
		}
	}

	static boolean isDoubleOrLong(String type) {
		return Primitives.JAVA_DOUBLE.equals(type) || Primitives.JVM_DOUBLE.equals(type)
				|| Primitives.JAVA_LONG.equals(type) || Primitives.JVM_LONG.equals(type);
	}

	static class OperandStack {
		private LinkedList<StackEntry> stack = new LinkedList<>();

		public void push(String type, Instruction instruction) {
			stack.offer(new StackEntry(type, instruction));
		}

		public String peek() {
			return stack.peek().type;
		}

		public void pop() {
			stack.pollLast();
		}

		public boolean isEmpty() {
			return stack.isEmpty();
		}

		public String get(int index) {
			return stack.get(index).type;
		}

		public void add(int index, String type, Instruction instruction) {
			stack.add(index, new StackEntry(type, instruction));
		}

		public boolean contains(String type, Instruction instruction) {
			StackEntry testEntry = new StackEntry(type, instruction);

			return stack.stream().anyMatch(el -> el.equals(testEntry));
		}
	}

	private static class StackEntry {
		public final String type;
		public final Instruction instruction;

		StackEntry(String type, Instruction instruction) {
			this.type = type;
			this.instruction = instruction;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hash(instruction, type);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof StackEntry))
				return false;
			StackEntry other = (StackEntry) obj;

			return Objects.equals(instruction, other.instruction) && Objects.equals(type, other.type);
		}

	}

}
