package de.nvg.agent.classdata.modification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.AgentProperties;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.modification.helper.ExceptionHandler;
import de.nvg.agent.classdata.modification.helper.ExceptionHandler.ExceptionHandlerModel;
import javassist.ClassPool;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.StackMapTable;
import javassist.bytecode.stackmap.MapMaker;

public class TestGenerationAdder {
	private static final String TEST_GENERATOR_CLASSNAME = "de/nvg/testgenerator/generation/Testgenerator";
	private static final String TEST_GENERATOR_METHOD_GENERATE = "generate";
	private static final String TEST_GENERATOR_METHOD_GENERATE_DESC = "(Ljava/lang/String;Ljava/lang/String;)V";

	private static final List<Integer> RETURN_OPCODES = Collections.unmodifiableList(Arrays.asList(//
			Opcode.ARETURN, Opcode.IRETURN, Opcode.DRETURN, Opcode.FRETURN, Opcode.LRETURN, Opcode.RETURN));

	private static final Logger LOGGER = LogManager.getLogger(TestGenerationAdder.class);

	private final AgentProperties properties = AgentProperties.getInstance();

	private final ExceptionHandler exceptionHandler = new ExceptionHandler();

	private final CodeAttribute codeAttribute;
	private final CodeIterator iterator;
	private final ConstPool constantPool;

	private Bytecode testgenerationWithLocalVariable;
	private Bytecode testgeneration;

	public TestGenerationAdder(CodeAttribute codeAttribute) {
		this.codeAttribute = codeAttribute;
		this.iterator = codeAttribute.iterator();
		this.constantPool = codeAttribute.getConstPool();

		initDefaultBytecodes();
	}

	public void addTestgenerationToMethod(MethodInfo method) throws BadBytecode {
		List<Instruction> instructions = Instructions.getAllInstructions(method);
		List<Instruction> returnInstructions = instructions.stream()
				.filter(inst -> RETURN_OPCODES.contains(inst.getOpcode())).collect(Collectors.toList());

		LOGGER.debug("Method before manipulation",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		int codeArrayModificator = 0;

		for (int i = 0; i < returnInstructions.size(); i++) {
			Instruction instruction = returnInstructions.get(i);

			if (i + 1 == returnInstructions.size()) {

				boolean finallyExceptionHandler = addTestgenerationToExceptionHandler(instructions, instruction,
						codeArrayModificator);

				if (!finallyExceptionHandler && Opcode.RETURN == instruction.getOpcode()) {
					addExceptionHandlerToMethodWithoutReturn(instruction, codeArrayModificator);
				} else if (!finallyExceptionHandler) {
					addExceptionHandlerToMethodsWithReturn(instruction, codeArrayModificator);
				}

			} else {

				int beforeReturnInstructionSize = Instructions
						.getInstructionSize(Instructions.getBeforeInstruction(instructions, instruction)//
								, instruction);
				// EndIndex = codeArrayIndex
				// codeLength testgeneration.size +1 fuer return
				if (Opcode.RETURN == instruction.getOpcode()) {
					exceptionHandler.addExceptionHandler(instruction.getCodeArrayIndex(), testgeneration.getSize() + 1,
							null);

					iterator.insertAt(instruction.getCodeArrayIndex() + codeArrayModificator, testgeneration.get());
				} else {
					exceptionHandler.addExceptionHandler(instruction.getCodeArrayIndex() - beforeReturnInstructionSize,
							testgeneration.getSize() + 1 + beforeReturnInstructionSize, null);

					// - cause the size of the instruction before the return
					iterator.insertAt(
							instruction.getCodeArrayIndex() + codeArrayModificator - beforeReturnInstructionSize,
							testgeneration.get());
				}

				codeArrayModificator += testgeneration.getSize();
			}
		}

		codeAttribute.computeMaxStack();

		LOGGER.debug("Method after manipulation", stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		StackMapTable stackMapTable = MapMaker.make(ClassPool.getDefault(), method);
		codeAttribute.setAttribute(stackMapTable);
	}

	private boolean addTestgenerationToExceptionHandler(List<Instruction> instructions, Instruction instruction,
			int codeArrayModificator) throws BadBytecode {
		ExceptionTable exceptionTable = codeAttribute.getExceptionTable();

		for (int a = 0; a < exceptionTable.size(); a++) {
			// entry is a finally block
			if (exceptionTable.catchType(a) == 0) {
				int handlerStart = exceptionTable.handlerPc(a) - codeArrayModificator;
				Instruction astore = instructions.stream().filter(inst -> handlerStart == inst.getCodeArrayIndex())//
						.findAny().orElse(null);
				int indexAstore = instructions.indexOf(astore);

				int instructionLength = instructions.get(indexAstore + 1).getCodeArrayIndex() - handlerStart;

				iterator.insert(exceptionTable.handlerPc(a) + instructionLength, testgeneration.get());

				codeArrayModificator = testgeneration.getSize();

				if (Opcode.RETURN == instruction.getOpcode()) {
					Instruction athrow = instructions.stream().skip(indexAstore)
							.filter(inst -> Opcode.ATHROW == inst.getOpcode()).findAny().orElse(null);

					iterator.insertAt(athrow.getCodeArrayIndex() + codeArrayModificator + 1, testgeneration.get());
				} else {
					int instructionBeforeSize = Instructions.getInstructionSize(
							Instructions.getBeforeInstruction(instructions, instruction), instruction);

					iterator.insertAt(instruction.getCodeArrayIndex() + codeArrayModificator - instructionBeforeSize,
							testgeneration.get());
				}

				return true;
			}

		}
		return false;
	}

	private void addExceptionHandlerToMethodsWithReturn(Instruction instruction, int codeArrayModificator)
			throws BadBytecode {
		int maxLocals = codeAttribute.getMaxLocals();

		exceptionHandler.addExceptionHandler(instruction.getCodeArrayIndex() + codeArrayModificator, 0, null);

		iterator.insertAt(instruction.getCodeArrayIndex() + codeArrayModificator,
				testgenerationWithLocalVariable.get());

		codeArrayModificator += testgenerationWithLocalVariable.getSize();

		Bytecode exceptionHandling = new Bytecode(constantPool);
		exceptionHandling.addAstore(maxLocals);
		exceptionHandling.addLdc(properties.getClassName());
		exceptionHandling.addLdc(properties.getMethod());
		exceptionHandling.addInvokestatic(TEST_GENERATOR_CLASSNAME, TEST_GENERATOR_METHOD_GENERATE,
				TEST_GENERATOR_METHOD_GENERATE_DESC);

		exceptionHandling.addAload(maxLocals);
		exceptionHandling.addOpcode(Opcode.ATHROW);

		iterator.insert(instruction.getCodeArrayIndex() + codeArrayModificator + 1, exceptionHandling.get());

		for (ExceptionHandlerModel handler : exceptionHandler.getExceptionHandlers()) {
			codeAttribute.getExceptionTable().add(handler.startIndex, handler.endIndex,
					// type = 0 cause finally block
					instruction.getCodeArrayIndex() + codeArrayModificator + 1, 0);
		}

		codeAttribute.setMaxLocals(codeAttribute.getMaxLocals() + 2);
	}

	private void addExceptionHandlerToMethodWithoutReturn(Instruction instruction, int codeArrayModificator)
			throws BadBytecode {
		int maxLocals = codeAttribute.getMaxLocals();

		Bytecode exceptionHandling = new Bytecode(constantPool);
		exceptionHandling.addAstore(maxLocals);
		exceptionHandling.addLdc(properties.getClassName());
		exceptionHandling.addLdc(properties.getMethod());
		exceptionHandling.addInvokestatic(TEST_GENERATOR_CLASSNAME, TEST_GENERATOR_METHOD_GENERATE,
				TEST_GENERATOR_METHOD_GENERATE_DESC);

		exceptionHandling.addAload(maxLocals);
		exceptionHandling.addOpcode(Opcode.ATHROW);

		int codeSizeExceptionHandler = exceptionHandling.getSize();

		exceptionHandling.addLdc(properties.getClassName());
		exceptionHandling.addLdc(properties.getMethod());
		exceptionHandling.addInvokestatic(TEST_GENERATOR_CLASSNAME, TEST_GENERATOR_METHOD_GENERATE,
				TEST_GENERATOR_METHOD_GENERATE_DESC);

		exceptionHandler.addExceptionHandler(instruction.getCodeArrayIndex() + codeArrayModificator, 0, null);

		iterator.insertAt(instruction.getCodeArrayIndex() + codeArrayModificator, exceptionHandling.get());

		Bytecode gotoBytes = new Bytecode(constantPool);
		gotoBytes.addOpcode(Opcode.GOTO);
		gotoBytes.addGap(2);
		gotoBytes.write16bit(1, codeSizeExceptionHandler + 3);

		iterator.insertAt(instruction.getCodeArrayIndex() + codeArrayModificator, gotoBytes.get());

		for (ExceptionHandlerModel handler : exceptionHandler.getExceptionHandlers()) {
			codeAttribute.getExceptionTable().add(handler.startIndex, handler.endIndex,
					// type = 0 cause finally block
					instruction.getCodeArrayIndex() + codeArrayModificator + 3, 0);
		}

		codeAttribute.setMaxLocals(codeAttribute.getMaxLocals() + 1);
	}

	private void initDefaultBytecodes() {
		int maxLocals = codeAttribute.getMaxLocals();

		testgenerationWithLocalVariable = new Bytecode(constantPool);
		testgenerationWithLocalVariable.addAstore(maxLocals + 1);
		testgenerationWithLocalVariable.addLdc(properties.getClassName());
		testgenerationWithLocalVariable.addLdc(properties.getMethod());
		testgenerationWithLocalVariable.addInvokestatic(TEST_GENERATOR_CLASSNAME, TEST_GENERATOR_METHOD_GENERATE,
				TEST_GENERATOR_METHOD_GENERATE_DESC);
		testgenerationWithLocalVariable.addAload(maxLocals + 1);

		testgeneration = new Bytecode(constantPool);
		testgeneration.addLdc(properties.getClassName());
		testgeneration.addLdc(properties.getMethod());
		testgeneration.addInvokestatic(TEST_GENERATOR_CLASSNAME, TEST_GENERATOR_METHOD_GENERATE,
				TEST_GENERATOR_METHOD_GENERATE_DESC);
	}
}
