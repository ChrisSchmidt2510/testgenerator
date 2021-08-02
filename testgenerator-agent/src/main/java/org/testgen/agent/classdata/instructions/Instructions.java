package org.testgen.agent.classdata.instructions;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction.Builder;
import org.testgen.core.MapBuilder;

import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.InstructionPrinter;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.Opcode;

public final class Instructions {

	private static final List<Integer> ALOAD_OPCODES = Collections
			.unmodifiableList(Arrays.asList(Opcode.ALOAD, Opcode.ALOAD_0, Opcode.ALOAD_1, //
					Opcode.ALOAD_2, Opcode.ALOAD_3));
	private static final List<Integer> ARRAY_LOAD_OPCODES = Collections.unmodifiableList(Arrays.asList(//
			Opcode.IALOAD, Opcode.LALOAD, Opcode.FALOAD, Opcode.DALOAD, Opcode.AALOAD, //
			Opcode.BALOAD, Opcode.CALOAD, Opcode.SALOAD));

	private static final List<Integer> ARRAY_STORE_OPCODES = Collections.unmodifiableList(Arrays.asList(//
			Opcode.IASTORE, Opcode.LASTORE, Opcode.LASTORE, Opcode.FASTORE, Opcode.DASTORE, Opcode.AASTORE, //
			Opcode.BASTORE, Opcode.CASTORE, Opcode.SASTORE));

	private static final List<Integer> PRIMITIVE_LOAD_OPCODES = Collections.unmodifiableList(Arrays.asList(//
			Opcode.ILOAD, Opcode.ILOAD_0, Opcode.ILOAD_1, Opcode.ILOAD_2, Opcode.ILOAD_3, Opcode.ICONST_0,
			Opcode.ICONST_1, Opcode.ICONST_2, Opcode.ICONST_3, Opcode.ICONST_4, Opcode.ICONST_5, Opcode.ICONST_M1,
			Opcode.BIPUSH, Opcode.SIPUSH, //
			Opcode.FLOAD, Opcode.FLOAD_0, Opcode.FLOAD_1, Opcode.FLOAD_2, Opcode.FLOAD_3, Opcode.FCONST_0,
			Opcode.FCONST_1, Opcode.FCONST_2, //
			Opcode.DLOAD, Opcode.DLOAD_0, Opcode.DLOAD_1, Opcode.DLOAD_2, Opcode.DLOAD_3, //
			Opcode.DCONST_0, Opcode.DCONST_1, //
			Opcode.LLOAD, Opcode.LLOAD_0, Opcode.LLOAD_1, Opcode.LLOAD_2, Opcode.LLOAD_3, Opcode.LCONST_0,
			Opcode.LCONST_1));

	private static final List<Integer> ONE_ITEM_COMPARISONS = Collections
			.unmodifiableList(Arrays.asList(Opcode.IFNULL, Opcode.IFNONNULL, //
					Opcode.IFEQ, Opcode.IFNE, Opcode.IFLT, Opcode.IFGE, Opcode.IFGT, Opcode.IFLE));

	private static final List<Integer> TWO_ITEM_COMPARISONS = Collections
			.unmodifiableList(Arrays.asList(Opcode.IF_ACMPEQ, Opcode.IF_ACMPNE, //
					Opcode.IF_ICMPEQ, Opcode.IF_ICMPNE, Opcode.IF_ICMPLT, Opcode.IF_ICMPGE, Opcode.IF_ICMPGT,
					Opcode.IF_ICMPLE));

	private static final List<Integer> MATH_OPERATIONS = Collections.unmodifiableList(Arrays.asList(//
			Opcode.IADD, Opcode.IREM, Opcode.IMUL, Opcode.IDIV, //
			Opcode.FADD, Opcode.FREM, Opcode.FMUL, Opcode.FDIV, //
			Opcode.DADD, Opcode.DREM, Opcode.DMUL, Opcode.DDIV, //
			Opcode.LADD, Opcode.LREM, Opcode.LMUL, Opcode.LDIV));

	private static final List<Integer> INVOKE_OPCODES = Collections
			.unmodifiableList(Arrays.asList(Opcode.INVOKEVIRTUAL, Opcode.INVOKESPECIAL, //
					Opcode.INVOKEINTERFACE, Opcode.INVOKESTATIC));

	private static final Map<List<Integer>, String> PRIMITVE_CASTS = MapBuilder.<List<Integer>, String>hashMapBuilder()
			.add(Arrays.asList(Opcode.D2F, Opcode.D2I, Opcode.D2L), Primitives.JAVA_DOUBLE)//
			.add(Arrays.asList(Opcode.I2B, Opcode.I2C, Opcode.I2D, //
					Opcode.I2F, Opcode.I2L, Opcode.I2S), Primitives.JAVA_INT)
			.add(Arrays.asList(Opcode.L2D, Opcode.L2F, Opcode.L2I), Primitives.JAVA_LONG)
			.add(Arrays.asList(Opcode.F2D, Opcode.F2I, Opcode.F2L), Primitives.JAVA_FLOAT).toUnmodifiableMap();

	private Instructions() {
	}

	public static List<Instruction> getAllInstructions(MethodInfo methodInfo) throws BadBytecode {
		List<Instruction> instructions = new ArrayList<>();

		CodeIterator iterator = methodInfo.getCodeAttribute().iterator();
		ConstPool constantPool = methodInfo.getConstPool();

		while (iterator.hasNext()) {
			int index = iterator.next();
			int opcode = iterator.byteAt(index);

			switch (opcode) {
			case Opcode.GETFIELD:
			case Opcode.GETSTATIC:
			case Opcode.PUTFIELD:
			case Opcode.PUTSTATIC:
				int cpIndex = iterator.s16bitAt(index + 1);

				Instruction instructionField = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).withType(constantPool.getFieldrefType(cpIndex))//
						.withName(constantPool.getFieldrefName(cpIndex))
						.withClassRef(constantPool.getFieldrefClassName(cpIndex)).build();

				instructions.add(instructionField);
				break;

			case Opcode.INVOKEINTERFACE:
			case Opcode.INVOKESTATIC:
			case Opcode.INVOKESPECIAL:
			case Opcode.INVOKEVIRTUAL:
				cpIndex = iterator.s16bitAt(index + 1);

				Instruction instructionMethod = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).withType(constantPool.getMethodrefType(cpIndex))
						.withName(constantPool.getMethodrefName(cpIndex))
						.withClassRef(constantPool.getMethodrefClassName(cpIndex)).build();

				instructions.add(instructionMethod);
				break;

			case Opcode.INVOKEDYNAMIC:
				cpIndex = iterator.s16bitAt(index + 1);

				Instruction invokeDynamicInstruction = new Instruction.Builder().withCodeArrayIndex(index)
						.withOpcode(opcode).withBootstrapMethodIndex(constantPool.getInvokeDynamicBootstrap(cpIndex))
						.withType(constantPool.getInvokeDynamicType(cpIndex)).build();

				instructions.add(invokeDynamicInstruction);
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

			case Opcode.NEW:
			case Opcode.CHECKCAST:
			case Opcode.INSTANCEOF:
				cpIndex = iterator.s16bitAt(index + 1);

				Instruction instruction = new Instruction.Builder().withCodeArrayIndex(index)
						.withClassRef(Descriptor.toClassName(constantPool.getClassInfoByDescriptor(cpIndex)))
						.withOpcode(opcode).build();

				instructions.add(instruction);
				break;

			case Opcode.LDC:
				Builder instructionBuilder = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode);

				Instruction ldcInstruction = ldc(instructionBuilder, constantPool, iterator.byteAt(index + 1))//
						.build();

				instructions.add(ldcInstruction);
				break;
			case Opcode.LDC_W:
			case Opcode.LDC2_W:

				Builder instructionBuilderLdcw = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode);

				Instruction ldcwInstruction = ldc(instructionBuilderLdcw, constantPool, iterator.u16bitAt(index + 1))//
						.build();

				instructions.add(ldcwInstruction);
				break;
			case Opcode.ANEWARRAY:
				cpIndex = iterator.s16bitAt(index + 1);
				Instruction newArrayInstruction = new Instruction.Builder().withCodeArrayIndex(index)//
						.withOpcode(opcode).withType(constantPool.getClassInfo(cpIndex)).build();

				instructions.add(newArrayInstruction);
				break;

			case Opcode.NEWARRAY:
				int arrayType = iterator.byteAt(index + 1);
				Instruction newPrimitiveArrayInstruction = new Instruction.Builder().withCodeArrayIndex(index)
						.withOpcode(opcode).withType(arrayType(arrayType)).build();

				instructions.add(newPrimitiveArrayInstruction);
				break;
			case Opcode.MULTIANEWARRAY:
				cpIndex = iterator.s16bitAt(index + 1);
				int dimensions = iterator.byteAt(index + 3);

				String multiDimArrayType = constantPool.getClassInfo(cpIndex);

				Instruction newMultiDimArray = new Instruction.Builder().withCodeArrayIndex(index).withOpcode(opcode)
						.withType(multiDimArrayType).withArrayDimensions(dimensions).build();

				instructions.add(newMultiDimArray);
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

	private static Builder ldc(Builder instructionBuilder, ConstPool constantPool, int index) {
		int tag = constantPool.getTag(index);
		switch (tag) {
		case ConstPool.CONST_String:
			return instructionBuilder.withConstantValue(constantPool.getStringInfo(index)).withType(JavaTypes.STRING);
		case ConstPool.CONST_Integer:
			return instructionBuilder.withConstantValue(Integer.toString(constantPool.getIntegerInfo(index)))
					.withType(Primitives.JAVA_INT);
		case ConstPool.CONST_Float:
			return instructionBuilder.withConstantValue(Float.toString(constantPool.getFloatInfo(index)))
					.withType(Primitives.JAVA_FLOAT);
		case ConstPool.CONST_Long:
			return instructionBuilder.withConstantValue(Long.toString(constantPool.getLongInfo(index)))
					.withType(Primitives.JAVA_LONG);
		case ConstPool.CONST_Double:
			return instructionBuilder.withConstantValue(Double.toString(constantPool.getDoubleInfo(index)))
					.withType(Primitives.JAVA_DOUBLE);
		case ConstPool.CONST_Class:
			return instructionBuilder.withConstantValue(constantPool.getClassInfo(index)).withType(JavaTypes.CLASS);
		default:
			throw new RuntimeException("bad LDC: " + tag);
		}

	}

	private static String arrayType(int type) {
		switch (type) {
		case Opcode.T_BOOLEAN:
			return Primitives.JAVA_BOOLEAN;
		case Opcode.T_CHAR:
			return Primitives.JAVA_CHAR;
		case Opcode.T_FLOAT:
			return Primitives.JAVA_FLOAT;
		case Opcode.T_DOUBLE:
			return Primitives.JAVA_DOUBLE;
		case Opcode.T_BYTE:
			return Primitives.JAVA_BYTE;
		case Opcode.T_SHORT:
			return Primitives.JAVA_SHORT;
		case Opcode.T_INT:
			return Primitives.JAVA_INT;
		case Opcode.T_LONG:
			return Primitives.JAVA_LONG;
		default:
			throw new IllegalArgumentException("invalid array type");
		}
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

	public static List<Instruction> getFilteredInstructions(List<Instruction> instructions, int opcode) {
		return instructions.stream().filter(inst -> inst.getOpcode() == opcode).collect(Collectors.toList());
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

	public static void showCodeArray(PrintStream stream, CodeIterator iterator, ConstPool constantPool) {
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

	public static boolean isAloadInstruction(Instruction instruction) {
		return ALOAD_OPCODES.contains(instruction.getOpcode());
	}

	public static boolean isInvokeInstruction(Instruction instruction) {
		return INVOKE_OPCODES.contains(instruction.getOpcode());
	}

	public static boolean isPrimitiveCast(Instruction instruction) {
		return PRIMITVE_CASTS.keySet().stream()//
				.anyMatch(list -> list.contains(instruction.getOpcode()));
	}

	public static boolean isArrayStoreInstruction(Instruction instruction) {
		return ARRAY_STORE_OPCODES.contains(instruction.getOpcode());
	}

	public static boolean isArrayLoadInstruction(Instruction instruction) {
		return ARRAY_LOAD_OPCODES.contains(instruction.getOpcode());
	}

	public static String getPrimitiveCastType(Instruction instruction) {
		return PRIMITVE_CASTS.entrySet().stream()
				.filter(returnType -> returnType.getKey().contains(instruction.getOpcode()))//
				.map(Entry::getValue).findAny().orElse(null);
	}

	public static boolean isConstant(int modifier) {
		return Modifier.isFinal(modifier) && Modifier.isStatic(modifier);
	}

	public static Instruction getBeforeInstruction(List<Instruction> instructions, Instruction instruction) {
		int index = instructions.indexOf(instruction);

		if (index == 0) {
			return null;
		}

		return instructions.get(index - 1);
	}

	public static int getInstructionSize(Instruction instruction, Instruction followingInstruction) {
		return followingInstruction.getCodeArrayIndex() - instruction.getCodeArrayIndex();
	}

	public static List<String> getMethodParams(String descriptor) {
		List<String> parameters = new ArrayList<>();

		String methodParameters = descriptor.substring(descriptor.indexOf('(') + 1, descriptor.indexOf(')'));

		while (methodParameters.length() > 0) {
			String parameter = nextDescriptorParameter(methodParameters);
			parameters.add(parameter);

			methodParameters = methodParameters.substring(methodParameters.indexOf(parameter) + parameter.length());
		}

		return parameters;
	}

	public static Instruction getInstructionByCodeArrayIndex(List<Instruction> instructions, int codeArrayIndex) {
		return instructions.stream().filter(inst -> inst.getCodeArrayIndex() == codeArrayIndex).findAny().orElseThrow(
				() -> new IllegalArgumentException(String.format("invalid codeArrayIndex %s", codeArrayIndex)));
	}

	private static String nextDescriptorParameter(String descriptor) {
		if ('[' == descriptor.charAt(0)) {
			return "[" + nextDescriptorParameter(descriptor.substring(1));
		} else if (Primitives.isPrimitiveJVMDataType(Character.toString(descriptor.charAt(0)))) {
			return Character.toString(descriptor.charAt(0));
		} else {
			int index = descriptor.indexOf(';') + 1;
			return descriptor.substring(0, index);
		}
	}

	public static String getReturnType(String methodDesc) {
		return methodDesc.substring(methodDesc.indexOf(')') + 1);
	}

	public static boolean isPrimitiveMathOperation(Instruction instruction) {
		return MATH_OPERATIONS.contains(instruction.getOpcode());
	}

	public static boolean isLoadInstruction(Instruction instruction) {
		return ALOAD_OPCODES.contains(instruction.getOpcode())
				|| PRIMITIVE_LOAD_OPCODES.contains(instruction.getOpcode());
	}

	public static boolean isOneItemComparison(Instruction instruction) {
		return ONE_ITEM_COMPARISONS.contains(instruction.getOpcode());
	}

	public static boolean isTwoItemComparison(Instruction instruction) {
		return TWO_ITEM_COMPARISONS.contains(instruction.getOpcode());
	}

	/** only used for debugging */
	@SuppressWarnings("unused")
	private static void printBytecode(Bytecode code) {
		CodeAttribute ca = new CodeAttribute(code.getConstPool(), code.getMaxStack(), code.getMaxLocals(), code.get(),
				code.getExceptionTable());

		showCodeArray(System.out, ca.iterator(), code.getConstPool());

	}

}
