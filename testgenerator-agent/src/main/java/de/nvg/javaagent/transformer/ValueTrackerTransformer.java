package de.nvg.javaagent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.nvg.javaagent.AgentException;
import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.Instructions;
import de.nvg.javaagent.classdata.modification.helper.ExceptionHandler;
import de.nvg.javaagent.classdata.modification.helper.ExceptionHandler.ExceptionHandlerModel;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.properties.AgentProperties;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.StackMapTable;
import javassist.bytecode.stackmap.MapMaker;

public class ValueTrackerTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ValueTrackerTransformer.class);

	private static final List<Integer> RETURN_OPCODES = Collections.unmodifiableList(Arrays.asList(//
			Opcode.ARETURN, Opcode.IRETURN, Opcode.DRETURN, Opcode.FRETURN, Opcode.LRETURN, Opcode.RETURN));

	private static final String OBJECT_VALUE_TRACKER_CLASSNAME = "de/nvg/valuetracker/ObjectValueTracker";
	private static final String OBJECT_VALUE_TRACKER = "Lde/nvg/valuetracker/ObjectValueTracker;";
	private static final String OBJECT_VALUE_TRACKER_METHOD_TRACK = "track";
	private static final String OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC = "(Ljava/lang/Object;Ljava/lang/String;Lde/nvg/valuetracker/blueprint/Type;)V";
	private static final String OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS = "enableGetterCallsTracking";
	private static final String OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS_DESC = "()V";

	private static final String TYPE_CLASSNAME = "de/nvg/valuetracker/blueprint/Type";
	private static final String TYPE = "Lde/nvg/valuetracker/blueprint/Type;";
	private static final String TYPE_FIELDNAME_TESTOBJECT = "TESTOBJECT";
	private static final String TYPE_FIELDNAME_METHODPARAMETER = "METHOD_PARAMETER";

	private static final String TEST_GENERATOR_CLASSNAME = "de/nvg/testgenerator/generation/Testgenerator";
	private static final String TEST_GENERATOR_METHOD_GENERATE = "generate";
	private static final String TEST_GENERATOR_METHOD_GENERATE_DESC = "(Ljava/lang/String;Ljava/lang/String;)V";

	private static final String FIELD_NAME = "valueTracker";

	private final AgentProperties properties = AgentProperties.getInstance();

	private final ExceptionHandler exceptionHandler = new ExceptionHandler();

	private CodeAttribute codeAttribute;
	private CodeIterator iterator;
	private ConstPool constantPool;

	private Bytecode testgenerationWithLocalVariable;
	private Bytecode testgeneration;

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if (properties.getClassName().equals(className)) {
			final ClassPool pool = ClassPool.getDefault();
			try {
				CtClass classToLoad = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

				byte[] bytecode = reTransformMethodForObservObjectData(classToLoad);

				try (FileOutputStream fos = new FileOutputStream(
						new File("D:\\" + className.substring(className.lastIndexOf('/')) + ".class"))) {
					fos.write(bytecode);
				}

				return bytecode;

			} catch (Exception e) {
				LOGGER.error(e);
				throw new AgentException("Es ist ein Fehler bei der Transfomation aufgetreten", e);
			}

		}
		return classfileBuffer;
	}

	private byte[] reTransformMethodForObservObjectData(CtClass classToLoad)
			throws IOException, CannotCompileException, NotFoundException, BadBytecode {

		classToLoad.addField(CtField.make("de.nvg.valuetracker.ObjectValueTracker " + FIELD_NAME + ";", classToLoad),
				"new de.nvg.valuetracker.ObjectValueTracker();");

		CtMethod method = classToLoad.getMethod(properties.getMethod(), properties.getMethodDescriptor());

		MethodInfo methodInfo = method.getMethodInfo();

		codeAttribute = methodInfo.getCodeAttribute();
		constantPool = codeAttribute.getConstPool();
		iterator = codeAttribute.iterator();

		initDefaultBytecodes();

		addValueTrackingToMethod(classToLoad, methodInfo, iterator);
		addTestgenerationToMethod(methodInfo);

		byte[] bytecode = classToLoad.toBytecode();

		classToLoad.detach();

		return bytecode;
	}

	private void addValueTrackingToMethod(CtClass classToLoad, MethodInfo methodInfo, CodeIterator iterator)
			throws BadBytecode {
		// if a method is not static the first argument to a method is this
		int lowestParameterIndex = Modifier.isStatic(methodInfo.getAccessFlags()) ? 0 : 1;

		int parameterCount = Descriptor.numOfParameters(methodInfo.getDescriptor());

		LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute()
				.getAttribute(LocalVariableAttribute.tag);

		LOGGER.debug("Method before manipulation",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		Bytecode valueTracking = new Bytecode(constantPool);

		for (int i = lowestParameterIndex; i <= parameterCount; i++) {
			String variableName = table.variableName(i);

			valueTracking.addAload(0);
			valueTracking.addGetfield(classToLoad, FIELD_NAME, OBJECT_VALUE_TRACKER);
			valueTracking.addAload(i);
			valueTracking.addLdc(variableName);
			valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_METHODPARAMETER, TYPE);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
					OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);
		}

		valueTracking.addAload(0);
		valueTracking.addGetfield(classToLoad, FIELD_NAME, OBJECT_VALUE_TRACKER);
		valueTracking.addAload(0);
		valueTracking.addLdc(createNameForTestobject(classToLoad.getName()));
		valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_TESTOBJECT, TYPE);
		valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
				OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);

		valueTracking.addAload(0);
		valueTracking.addGetfield(classToLoad, FIELD_NAME, OBJECT_VALUE_TRACKER);
		valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS,
				OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS_DESC);

		iterator.insert(0, valueTracking.get());
	}

	private void addTestgenerationToMethod(MethodInfo method) throws BadBytecode {
		List<Instruction> instructions = Instructions.getAllInstructions(method);
		List<Instruction> returnInstructions = instructions.stream()
				.filter(inst -> RETURN_OPCODES.contains(inst.getOpcode())).collect(Collectors.toList());

		LOGGER.debug("Method before manipulation",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		int codeArrayModificator = 0;

		for (int i = 0; i < returnInstructions.size(); i++) {
			Instruction instruction = returnInstructions.get(i);

			if (i + 1 == returnInstructions.size()) {
				if (Opcode.RETURN == instruction.getOpcode()) {
					addExceptionHandlerToMethodWithoutReturn(instruction, codeArrayModificator);
				} else {
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

					// -1 cause load-instruction
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

	private String createNameForTestobject(String className) {
		String newClassName = className.substring(className.lastIndexOf(".") + 1);

		newClassName = newClassName.replace(newClassName.charAt(0), Character.toLowerCase(newClassName.charAt(0)));

		return newClassName;
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
