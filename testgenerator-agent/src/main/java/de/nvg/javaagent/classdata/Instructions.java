package de.nvg.javaagent.classdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
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
						.withName(constantPool.getFieldrefName(cpIndex)).build();

				instructions.add(instructionField);
				break;

			case Opcode.INVOKEINTERFACE:
			case Opcode.INVOKESTATIC:
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
		throw new IllegalArgumentException("The opcode " + Mnemonic.OPCODE[opcode] + " is not in codearray");
	}

	/**
	 * only for testcases!!! writes the data of the codearray in the console
	 * 
	 * @param iterator
	 * @throws BadBytecode
	 */
	@Deprecated
	public static final void showCodeArray(CodeIterator iterator, ConstPool constantPool) throws BadBytecode {
		iterator.begin();

		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);

			if (Opcode.NEW == opcode) {
				int cpIndex = iterator.s16bitAt(index + 1);
				String classInfo = constantPool.getClassInfo(cpIndex);
				System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " " + classInfo);
			} else if (Opcode.INVOKESPECIAL == opcode || Opcode.INVOKEVIRTUAL == opcode) {
				int cpIndex = iterator.s16bitAt(index + 1);

				System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " Klasse: "
						+ constantPool.getMethodrefClassName(cpIndex) + " name: "
						+ constantPool.getMethodrefName(cpIndex) + " Descriptor: "
						+ constantPool.getMethodrefType(cpIndex));
			} else if (Opcode.INVOKEDYNAMIC == opcode) {
				int cpIndex = iterator.s16bitAt(index + 1);

				System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " Bootstrapindex: "
						+ constantPool.getInvokeDynamicBootstrap(cpIndex) + " Name: "
						+ constantPool.getUtf8Info(
								constantPool.getNameAndTypeName(constantPool.getInvokeDynamicNameAndType(cpIndex)))
						+ " Descriptor: "
						+ constantPool.getUtf8Info(constantPool
								.getNameAndTypeDescriptor(constantPool.getInvokeDynamicNameAndType(cpIndex)))
						+ " Type: " + constantPool.getInvokeDynamicType(cpIndex));

			} else if (Opcode.LDC == opcode) {
				int cpIndex = iterator.signedByteAt(index + 1);
				try {
					System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " "
							+ constantPool.getLdcValue(cpIndex));
				} catch (ClassCastException e) {
					System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " "
							+ constantPool.getClassInfo(cpIndex));
				}
			} else if (Opcode.LDC_W == opcode) {
				int cpIndex = iterator.s16bitAt(index + 1);
				System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " "
						+ constantPool.getStringInfo(cpIndex));
			} else if (Opcode.PUTFIELD == opcode || Opcode.GETFIELD == opcode) {
				int cpIndex = iterator.s16bitAt(index + 1);
				System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode] + " FieldName: "
						+ constantPool.getFieldrefName(cpIndex) + " FieldType: "
						+ constantPool.getFieldrefType(cpIndex));
			} else {
				System.out.println("Index: " + index + " Opcode: " + Mnemonic.OPCODE[opcode]);
			}
		}
	}

	public static boolean isLoadInstruction(Instruction instruction) {
		return LOAD_OPCODES.contains(instruction.getOpcode());
	}

	public static boolean isConstant(int modifier) {
		return Modifier.isFinal(modifier) && Modifier.isStatic(modifier);
	}

}
