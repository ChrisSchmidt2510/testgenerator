package org.testgen.agent.classdata.modification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.modification.helper.CodeArrayModificator;
import org.testgen.agent.classdata.modification.helper.ExceptionHandler;
import org.testgen.agent.classdata.modification.helper.ExceptionHandler.ExceptionHandlerModel;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.StackMapTable;
import javassist.bytecode.stackmap.MapMaker;

public class TestGenerationAdder {
	private static final String GENERATION_ENTRY_POINT_CLASSNAME = "org/testgen/runtime/generation/GenerationEntryPoint";
	private static final String GENERATION_ENTRY_POINT_METHOD_GENERATE = "generate";
	private static final String GENERATION_ENTRY_POINT_METHOD_GENERATE_DESC = "(Ljava/lang/Class;ZLjava/lang/String;Ljava/util/List;)V";

	private static final List<Integer> RETURN_OPCODES = Collections.unmodifiableList(Arrays.asList(//
			Opcode.ARETURN, Opcode.IRETURN, Opcode.DRETURN, Opcode.FRETURN, Opcode.LRETURN, Opcode.RETURN));

	private static final Logger LOGGER = LogManager.getLogger(TestGenerationAdder.class);

	private final ExceptionHandler exceptionHandler = new ExceptionHandler();

	private final CodeArrayModificator codeArrayModificator = new CodeArrayModificator();

	private final CtClass modifiedClass;

	private final CodeAttribute codeAttribute;
	private final CodeIterator iterator;
	private final ConstPool constantPool;
	private final boolean isStatic;

	private Bytecode testgenerationWithLocalVariable;
	private Bytecode testgeneration;

	public TestGenerationAdder(CtClass modifiedClass, CodeAttribute codeAttribute, boolean isStatic) {
		this.modifiedClass = modifiedClass;
		this.codeAttribute = codeAttribute;
		this.iterator = codeAttribute.iterator();
		this.constantPool = codeAttribute.getConstPool();
		this.isStatic = isStatic;

		initDefaultBytecodes();
	}

	public void addTestgenerationToMethod(MethodInfo method) throws BadBytecode {
		List<Instruction> instructions = Instructions.getAllInstructions(method);
		List<Instruction> returnInstructions = instructions.stream()
				.filter(inst -> RETURN_OPCODES.contains(inst.getOpcode())).collect(Collectors.toList());

		LOGGER.debug("Method before manipulation", () -> Instructions.printCodeArray(iterator, constantPool));

		for (int i = 0; i < returnInstructions.size(); i++) {
			Instruction instruction = returnInstructions.get(i);

			if (i + 1 == returnInstructions.size()) {

				boolean finallyExceptionHandler = addTestgenerationToExceptionHandler(instructions, //
						instruction);

				if (!finallyExceptionHandler && Opcode.RETURN == instruction.getOpcode()) {
					addExceptionHandlerToMethodWithoutReturn(instruction);
				} else if (!finallyExceptionHandler) {
					addExceptionHandlerToMethodsWithReturn(instruction);
				}

			} else {
				int beforeReturnInstructionSize = Instructions
						.getInstructionSize(Instructions.getBeforeInstruction(instructions, instruction)//
								, instruction);
				int bci = instruction.getCodeArrayIndex();
				int mod = codeArrayModificator.getModificator(bci);

				// EndIndex = codeArrayIndex
				// codeLength testgeneration.size +1 fuer return
				if (Opcode.RETURN == instruction.getOpcode()) {

					exceptionHandler.addExceptionHandler(bci + mod, testgeneration.getSize() + 1, null);

					iterator.insertAt(bci + mod, testgeneration.get());
				} else {
					exceptionHandler.addExceptionHandler(bci - beforeReturnInstructionSize + mod,
							testgeneration.getSize() + 1 + beforeReturnInstructionSize, null);

					// - cause the size of the instruction before the return
					iterator.insertAt(bci + mod - beforeReturnInstructionSize, testgeneration.get());
				}

				codeArrayModificator.addCodeArrayModificator(bci, testgeneration.getSize());
			}
		}

		codeAttribute.computeMaxStack();

		LOGGER.debug("Method after manipulation", () -> Instructions.printCodeArray(iterator, constantPool));

		StackMapTable stackMapTable = MapMaker.make(ClassPool.getDefault(), method);
		codeAttribute.setAttribute(stackMapTable);
	}

	/**
	 * Reads through the exceptionTable of the method and if there is already a
	 * finally entry in the table, then the generation bytecodes are added.
	 * 
	 * @param instructions
	 * @param instruction
	 * @return true if this method has already a finally entry in his exceptionTable
	 * @throws BadBytecode
	 */
	private boolean addTestgenerationToExceptionHandler(List<Instruction> instructions, Instruction instruction)
			throws BadBytecode {
		ExceptionTable exceptionTable = codeAttribute.getExceptionTable();

		for (int a = 0; a < exceptionTable.size(); a++) {
			// entry is a finally block
			if (exceptionTable.catchType(a) == 0) {
				int handlerStart = exceptionTable.handlerPc(a)
						- codeArrayModificator.getModificator(exceptionTable.handlerPc(a));
				Instruction astore = instructions.stream().filter(inst -> handlerStart == inst.getCodeArrayIndex())//
						.findAny().orElse(null);
				int indexAstore = instructions.indexOf(astore);

				int instructionLength = instructions.get(indexAstore + 1).getCodeArrayIndex() - handlerStart;

				iterator.insert(exceptionTable.handlerPc(a) + instructionLength, testgeneration.get());

				codeArrayModificator.addCodeArrayModificator(exceptionTable.handlerPc(a) + instructionLength,
						testgeneration.getSize());

				if (Opcode.RETURN == instruction.getOpcode()) {
					Instruction athrow = instructions.stream().skip(indexAstore)
							.filter(inst -> Opcode.ATHROW == inst.getOpcode()).findAny().orElse(null);

					int athrowIndex = athrow.getCodeArrayIndex();

					iterator.insertAt(athrowIndex + codeArrayModificator.getModificator(athrowIndex) + 1,
							testgeneration.get());
				} else {
					int instructionBeforeSize = Instructions.getInstructionSize(
							Instructions.getBeforeInstruction(instructions, instruction), instruction);

					int bci = instruction.getCodeArrayIndex();
					iterator.insertAt(instruction.getCodeArrayIndex() + codeArrayModificator.getModificator(bci)
							- instructionBeforeSize, testgeneration.get());
				}

				return true;
			}

		}
		return false;
	}

	private void addExceptionHandlerToMethodsWithReturn(Instruction instruction) throws BadBytecode {
		int maxLocals = codeAttribute.getMaxLocals();

		int bci = instruction.getCodeArrayIndex();

		exceptionHandler.addExceptionHandler(bci + codeArrayModificator.getModificator(bci), 0, null);

		iterator.insertAt(bci + codeArrayModificator.getModificator(bci), testgenerationWithLocalVariable.get());

		codeArrayModificator.addCodeArrayModificator(bci, testgenerationWithLocalVariable.getSize());

		Bytecode exceptionHandling = new Bytecode(constantPool);
		exceptionHandling.addAstore(maxLocals);
		BytecodeUtils.addClassInfoToBytecode(exceptionHandling, Descriptor.toJvmName(modifiedClass));
		exceptionHandling.addIconst(isStatic ? 1 : 0);
		exceptionHandling.addLdc(TestgeneratorConfig.getMethodName());
		exceptionHandling.addGetstatic(modifiedClass, TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE,
				JVMTypes.LIST);
		exceptionHandling.addInvokestatic(GENERATION_ENTRY_POINT_CLASSNAME, GENERATION_ENTRY_POINT_METHOD_GENERATE,
				GENERATION_ENTRY_POINT_METHOD_GENERATE_DESC);

		exceptionHandling.addAload(maxLocals);
		exceptionHandling.addOpcode(Opcode.ATHROW);

		iterator.insert(bci + codeArrayModificator.getModificator(bci) + 1, exceptionHandling.get());

		for (ExceptionHandlerModel handler : exceptionHandler.getExceptionHandlers()) {
			codeAttribute.getExceptionTable().add(handler.startIndex, handler.endIndex,
					// type = 0 cause finally block
					bci + codeArrayModificator.getModificator(bci) + 1, 0);
		}

		codeAttribute.setMaxLocals(codeAttribute.getMaxLocals() + 2);
	}

	private void addExceptionHandlerToMethodWithoutReturn(Instruction instruction) throws BadBytecode {
		int maxLocals = codeAttribute.getMaxLocals();

		Bytecode exceptionHandling = new Bytecode(constantPool);
		exceptionHandling.addAstore(maxLocals);
		BytecodeUtils.addClassInfoToBytecode(exceptionHandling, Descriptor.toJvmName(modifiedClass));
		exceptionHandling.addIconst(isStatic ? 1 : 0);
		exceptionHandling.addLdc(TestgeneratorConfig.getMethodName());
		exceptionHandling.addGetstatic(modifiedClass, TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE,
				JVMTypes.LIST);
		exceptionHandling.addInvokestatic(GENERATION_ENTRY_POINT_CLASSNAME, GENERATION_ENTRY_POINT_METHOD_GENERATE,
				GENERATION_ENTRY_POINT_METHOD_GENERATE_DESC);

		exceptionHandling.addAload(maxLocals);
		exceptionHandling.addOpcode(Opcode.ATHROW);

		int codeSizeExceptionHandler = exceptionHandling.getSize();

		BytecodeUtils.addClassInfoToBytecode(exceptionHandling, Descriptor.toJvmName(modifiedClass));
		exceptionHandling.addIconst(isStatic ? 1 : 0);
		exceptionHandling.addLdc(TestgeneratorConfig.getMethodName());
		exceptionHandling.addGetstatic(modifiedClass, TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE,
				JVMTypes.LIST);
		exceptionHandling.addInvokestatic(GENERATION_ENTRY_POINT_CLASSNAME, GENERATION_ENTRY_POINT_METHOD_GENERATE,
				GENERATION_ENTRY_POINT_METHOD_GENERATE_DESC);

		int bci = instruction.getCodeArrayIndex();

		exceptionHandler.addExceptionHandler(bci + codeArrayModificator.getModificator(bci), 0, null);

		iterator.insertAt(bci + codeArrayModificator.getModificator(bci), exceptionHandling.get());

		Bytecode gotoBytes = new Bytecode(constantPool);
		gotoBytes.addOpcode(Opcode.GOTO);
		gotoBytes.addGap(2);
		gotoBytes.write16bit(1, codeSizeExceptionHandler + 3);

		iterator.insertAt(bci + codeArrayModificator.getModificator(bci), gotoBytes.get());

		for (ExceptionHandlerModel handler : exceptionHandler.getExceptionHandlers()) {
			codeAttribute.getExceptionTable().add(handler.startIndex, handler.endIndex,
					// type = 0 cause finally block
					bci + codeArrayModificator.getModificator(bci) + 3, 0);
		}

		codeAttribute.setMaxLocals(codeAttribute.getMaxLocals() + 1);
	}

	private void initDefaultBytecodes() {
		int maxLocals = codeAttribute.getMaxLocals();

		testgenerationWithLocalVariable = new Bytecode(constantPool);
		testgenerationWithLocalVariable.addAstore(maxLocals + 1);
		BytecodeUtils.addClassInfoToBytecode(testgenerationWithLocalVariable, Descriptor.toJvmName(modifiedClass));
		testgenerationWithLocalVariable.addIconst(isStatic ? 1 : 0);
		testgenerationWithLocalVariable.addLdc(TestgeneratorConfig.getMethodName());
		testgenerationWithLocalVariable.addGetstatic(modifiedClass,
				TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE, JVMTypes.LIST);
		testgenerationWithLocalVariable.addInvokestatic(GENERATION_ENTRY_POINT_CLASSNAME,
				GENERATION_ENTRY_POINT_METHOD_GENERATE, GENERATION_ENTRY_POINT_METHOD_GENERATE_DESC);
		testgenerationWithLocalVariable.addAload(maxLocals + 1);

		testgeneration = new Bytecode(constantPool);
		BytecodeUtils.addClassInfoToBytecode(testgeneration, Descriptor.toJvmName(modifiedClass));
		testgeneration.addIconst(isStatic ? 1 : 0);
		testgeneration.addLdc(TestgeneratorConfig.getMethodName());
		testgeneration.addGetstatic(modifiedClass, TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE,
				JVMTypes.LIST);
		testgeneration.addInvokestatic(GENERATION_ENTRY_POINT_CLASSNAME, GENERATION_ENTRY_POINT_METHOD_GENERATE,
				GENERATION_ENTRY_POINT_METHOD_GENERATE_DESC);
	}
}
