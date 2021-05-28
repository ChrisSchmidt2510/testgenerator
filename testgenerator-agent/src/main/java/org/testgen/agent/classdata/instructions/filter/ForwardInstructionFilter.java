package org.testgen.agent.classdata.instructions.filter;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;

import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class ForwardInstructionFilter extends InstructionFilter {

	public ForwardInstructionFilter(ClassFile classFile, List<Instruction> instructions) {
		super(classFile, instructions);
	}

	@Override
	public List<Instruction> filterForCalledLoadInstructions(Instruction instruction) {
		calledLoadInstructions.clear();

		return calledLoadInstructions;
	}

	private Instruction filterForInstructionCallerIntern(Instruction instruction, LinkedList<String> operandStack) {
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
			operandStack.offer(instruction.getType());

		} else if (Opcode.PUTFIELD == opcode) {
			operandStack.pop();
			operandStack.pop();

		} else if (Opcode.ACONST_NULL == opcode)
			operandStack.offer(JavaTypes.OBJECT);

		else if (Opcode.NEW == opcode)
			operandStack.offer(instruction.getClassRef());

		else if (Opcode.GETSTATIC == opcode)
			operandStack.offer(instruction.getType());

		else if (Opcode.PUTSTATIC == opcode)
			operandStack.pop();

		else if (Opcode.DUP == opcode)
			operandStack.offer(operandStack.peek());

		else if (Opcode.DUP_X1 == opcode)
			// copy the top value of the stack two values down
			operandStack.add(2, operandStack.peek());

		else if (Opcode.DUP_X2 == opcode)
			// if the second item on the stack is a double or long copy to the third else
			// fourth position on the operand stack
			operandStack.add(isDoubleOrLong(operandStack.get(1)) ? 2 : 3, operandStack.peek());

		else if (Opcode.DUP2 == opcode)
			if (isDoubleOrLong(operandStack.peek()))
				operandStack.offer(operandStack.peek());

			else {
				operandStack.add(2, operandStack.peek());
				operandStack.add(3, operandStack.get(1));
			}

		else if (Opcode.DUP2_X1 == opcode)
			if (isDoubleOrLong(operandStack.peek()))
				operandStack.add(2, operandStack.peek());

			else {
				operandStack.add(3, operandStack.peek());
				operandStack.add(4, operandStack.get(1));
			}

		else if (Opcode.DUP2_X2 == opcode)
			// Form 4
			if (isDoubleOrLong(operandStack.peek()) && isDoubleOrLong(operandStack.get(1))) {
				operandStack.add(2, operandStack.peek());

				// Form 3
			} else if (isDoubleOrLong(operandStack.get(2))) {
				operandStack.add(3, operandStack.peek());
				operandStack.add(4, operandStack.get(1));

				// Form 2
			} else if (isDoubleOrLong(operandStack.peek()))
				operandStack.add(3, operandStack.peek());

			// Form 1
			else {
				operandStack.add(4, operandStack.peek());
				operandStack.add(5, operandStack.get(1));
			}

		else if (Opcode.LDC == opcode || Opcode.LDC_W == opcode || Opcode.LDC2_W == opcode)
			operandStack.offer(instruction.getType());

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

			operandStack.offer(getArrayComponentType(opcode));
		}

		else if (Opcode.NEWARRAY == opcode || Opcode.ANEWARRAY == opcode) {
			operandStack.pop();

			operandStack.offer(instruction.getType());
		}

		else if (Opcode.MULTIANEWARRAY == opcode) {
			String arrayType = instruction.getType();

			for (int i = 0; i < Descriptor.arrayDimension(arrayType); i++) {
				operandStack.pop();
			}

			operandStack.offer(arrayType);
		}

		if (operandStack.isEmpty())
			return instruction;

		else if (iterator.hasNext())
			return filterForInstructionCallerIntern(iterator.next(), operandStack);

		throw new NoSuchElementException("No matching caller-instruction found");
	}

	private void pushLoadInstructionOnStack(Instruction instruction, Deque<String> operandStack) {

		switch (instruction.getOpcode()) {
		case Opcode.ALOAD:
		case Opcode.ALOAD_0:
		case Opcode.ALOAD_1:
		case Opcode.ALOAD_2:
		case Opcode.ALOAD_3:
			// reference is pushed to the stack, so reference gets pushed onto the stack
			operandStack.offer(JavaTypes.OBJECT);
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
			operandStack.offer(Primitives.JAVA_INT);
			break;
		case Opcode.FLOAD:
		case Opcode.FLOAD_0:
		case Opcode.FLOAD_1:
		case Opcode.FLOAD_2:
		case Opcode.FLOAD_3:
		case Opcode.FCONST_0:
		case Opcode.FCONST_1:
		case Opcode.FCONST_2:
			operandStack.offer(Primitives.JAVA_FLOAT);
			break;
		case Opcode.DLOAD:
		case Opcode.DLOAD_0:
		case Opcode.DLOAD_1:
		case Opcode.DLOAD_2:
		case Opcode.DLOAD_3:
		case Opcode.DCONST_0:
		case Opcode.DCONST_1:
			operandStack.offer(Primitives.JAVA_DOUBLE);
			break;
		case Opcode.LLOAD:
		case Opcode.LLOAD_0:
		case Opcode.LLOAD_1:
		case Opcode.LLOAD_2:
		case Opcode.LLOAD_3:
		case Opcode.LCONST_0:
		case Opcode.LCONST_1:
			operandStack.offer(Primitives.JAVA_LONG);
			break;
		}

	}

	private String getArrayComponentType(int opcode) {
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

	private boolean isDoubleOrLong(String type) {
		return Primitives.JAVA_DOUBLE.equals(type) || Primitives.JVM_DOUBLE.equals(type)
				|| Primitives.JAVA_LONG.equals(type) || Primitives.JVM_LONG.equals(type);
	}

}
