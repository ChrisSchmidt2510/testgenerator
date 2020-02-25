package de.nvg.agent.classdata;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.InstructionPrinter;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.Opcode;

public class Instructions {

	private static final List<Integer> LOAD_OPCODES = Collections.unmodifiableList(Arrays.asList(Opcode.ILOAD,
			Opcode.ILOAD_0, Opcode.ILOAD_1, Opcode.ILOAD_2, Opcode.ILOAD_3, Opcode.FLOAD, Opcode.FLOAD_0,
			Opcode.FLOAD_1, Opcode.FLOAD_2, Opcode.FLOAD_3, Opcode.DLOAD, Opcode.DLOAD_0, Opcode.DLOAD_1,
			Opcode.DLOAD_2, Opcode.DLOAD_3, Opcode.LLOAD, Opcode.LLOAD_0, Opcode.LLOAD_1, Opcode.LLOAD_2,
			Opcode.LLOAD_3, Opcode.ALOAD, Opcode.ALOAD_0, Opcode.ALOAD_1, Opcode.ALOAD_2, Opcode.ALOAD_3));

	public static List<Instruction> getAllInstructions(MethodInfo methodInfo) throws BadBytecode {
		List<Instruction> instructions = new ArrayList<>();

		CodeIterator iterator = methodInfo.getCodeAttribute().iterator();
		ConstPool constantPool = methodInfo.getConstPool();

		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);

			switch (opcode) {
			case Opcode.PUTFIELD:
			case Opcode.GETFIELD:
				int cpIndex = iterator.s16bitAt(index + 1);

				Instruction instructionField = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).withType(constantPool.getFieldrefType(cpIndex))//
						.withName(constantPool.getFieldrefName(cpIndex))
						.withClassRef(constantPool.getFieldrefClassName(cpIndex)).build();
				;

				instructions.add(instructionField);
				break;

			case Opcode.INVOKEINTERFACE:
			case Opcode.INVOKESTATIC:
			case Opcode.INVOKESPECIAL:
				cpIndex = iterator.s16bitAt(index + 1);

				Instruction instructionMethod = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).withType(constantPool.getMethodrefType(cpIndex))
						.withName(constantPool.getMethodrefName(cpIndex))
						.withClassRef(constantPool.getMethodrefClassName(cpIndex)).build();

				instructions.add(instructionMethod);
				break;

			case Opcode.ALOAD:
			case Opcode.ILOAD:
			case Opcode.DLOAD:
			case Opcode.FLOAD:
			case Opcode.LLOAD:
				int localVariableIndex = iterator.signedByteAt(index + 1);

				Instruction instructionLoad = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).withLocalVariableIndex(localVariableIndex).build();

				instructions.add(instructionLoad);
				break;
			case Opcode.GOTO:
			case Opcode.IF_ACMPEQ:
			case Opcode.IF_ACMPNE:
			case Opcode.IF_ICMPEQ:
			case Opcode.IF_ICMPGE:
			case Opcode.IF_ICMPGT:
			case Opcode.IF_ICMPLE:
			case Opcode.IF_ICMPLT:
			case Opcode.IF_ICMPNE:
			case Opcode.IFEQ:
			case Opcode.IFGE:
			case Opcode.IFGT:
			case Opcode.IFLE:
			case Opcode.IFLT:
			case Opcode.IFNE:
			case Opcode.IFNONNULL:
			case Opcode.IFNULL:
				int branchbyte = iterator.s16bitAt(index + 1);

				Instruction instructionBranch = new Instruction.Builder().withCodeArrayIndex(index).withOpcode(opcode)
						.withOffset(branchbyte).build();

				instructions.add(instructionBranch);
				break;

			default:
				Instruction defaultInstruction = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).build();

				instructions.add(defaultInstruction);
				break;
			}
		}

		return Collections.unmodifiableList(instructions);
	}

	/**
	 * 
	 * @param opcodes
	 * @return Map<Opcode, List<Instruction>>
	 */
	public static Map<Integer, List<Instruction>> getFilteredInstructions(List<Instruction> instructions,
			List<Integer> opcodes) {

		return instructions.stream().filter(inst -> opcodes.contains(inst.getOpcode()))
				.collect(Collectors.groupingBy(Instruction::getOpcode, LinkedHashMap::new, //
						Collectors.toList()));
	}

	public static Instruction filterOpcode(List<Instruction> instructions, int maxIndex, int opcode) {
		for (int i = maxIndex; i >= 0; i--) {
			Instruction instruction = instructions.get(i);

			if (opcode == instruction.getOpcode()) {
				return instruction;
			}
		}
		throw new NoSuchElementException("The opcode " + Mnemonic.OPCODE[opcode] + " is not in codearray");
	}

	public static Instruction filterForAload0Opcode(List<Instruction> instructions, int maxIndex) {
		int counterNeededAloadInstruction = 0;
		for (int i = maxIndex; i >= 0; i--) {
			Instruction instruction = instructions.get(i);

			if (Opcode.ALOAD_0 == instruction.getOpcode() || (Opcode.DUP == instruction.getOpcode()
					&& Opcode.ALOAD_0 == Instructions.getBeforeInstruction(instructions, instruction).getOpcode())) {
				if (counterNeededAloadInstruction == 0) {
					return instruction;
				} else {
					counterNeededAloadInstruction--;
				}

			} else if (Opcode.GETFIELD == instruction.getOpcode()) {
				counterNeededAloadInstruction++;
			}
		}
		throw new NoSuchElementException("The opcode " + Mnemonic.OPCODE[Opcode.ALOAD_0] + " is not in codearray");
	}

	public static final void showCodeArray(PrintStream stream, CodeIterator iterator, ConstPool constantPool) {
		iterator.begin();

		while (iterator.hasNext()) {
			int pos;
			try {
				pos = iterator.next();
			} catch (BadBytecode e) {
				throw new RuntimeException(e);
			}

			stream.println(pos + ": " + InstructionPrinter.instructionString(iterator, pos, constantPool));
		}
	}

	public static boolean isLoadInstruction(Instruction instruction) {
		return LOAD_OPCODES.contains(instruction.getOpcode());
	}

	public static boolean isConstant(int modifier) {
		return Modifier.isFinal(modifier) && Modifier.isStatic(modifier);
	}

	public static Instruction getBeforeInstruction(List<Instruction> instructions, Instruction instruction) {
		return instructions.get(instructions.indexOf(instruction) - 1);
	}

	public static int getInstructionSize(Instruction instruction, Instruction followingInstruction) {
		return followingInstruction.getCodeArrayIndex() - instruction.getCodeArrayIndex();
	}

}
