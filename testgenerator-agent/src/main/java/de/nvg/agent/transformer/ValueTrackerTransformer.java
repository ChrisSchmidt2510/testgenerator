package de.nvg.agent.transformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.Wrapper;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import de.nvg.agent.AgentException;
import de.nvg.agent.classdata.analysis.signature.SignatureParser;
import de.nvg.agent.classdata.analysis.signature.SignatureParserException;
import de.nvg.agent.classdata.constants.JVMTypes;
import de.nvg.agent.classdata.constants.Primitives;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.agent.classdata.modification.BytecodeUtils;
import de.nvg.agent.classdata.modification.SignatureAdder;
import de.nvg.agent.classdata.modification.TestGenerationAdder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.LocalVariableTypeAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class ValueTrackerTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ValueTrackerTransformer.class);

	private static final String OBJECT_VALUE_TRACKER_CLASSNAME = "de/nvg/valuetracker/ObjectValueTracker";
	private static final String OBJECT_VALUE_TRACKER = "Lde/nvg/valuetracker/ObjectValueTracker;";
	private static final String OBJECT_VALUE_TRACKER_METHOD_TRACK = "track";
	private static final String OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC = "(Ljava/lang/Object;Ljava/lang/String;Lde/nvg/valuetracker/blueprint/Type;)V";
	private static final String OBJECT_VALUE_TRACKER_METHOD_ENABLE_FIELD_TRACKING = "enableFieldTracking";
	private static final String OBJECT_VALUE_TRACKER_METHOD_ENABLE_PROXY_TRACKING = "enableProxyTracking";
	private static final String OBJECT_VALUE_TRACKER_METHOD_GET_INSTANCE_DESC = "()" + OBJECT_VALUE_TRACKER;

	private static final String VALUE_STORAGE_CLASSNAME = "de/nvg/valuetracker/storage/ValueStorage";
	private static final String VALUE_STORAGE_METHOD_GET_INSTANCE_DESC = "()Lde/nvg/valuetracker/storage/ValueStorage;";
	private static final String VALUE_STORAGE_METHOD_PUSH_NEW_TESTDATA = "pushNewTestData";

	private static final String BASIC_TYPE_CLASSNAME = "org/testgen/runtime/classdata/model/descriptor/BasicType";
	private static final String BASIC_TYPE_METHOD_OF = "of";
	private static final String BASIC_TYPE_METHOD_OF_DESC = "(Ljava/lang/Class;)Lorg/testgen/runtime/classdata/model/descriptor/BasicType;";

	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_VOID_DESC = "()V";

	private static final String TYPE_CLASSNAME = "de/nvg/valuetracker/blueprint/Type";
	private static final String TYPE = "Lde/nvg/valuetracker/blueprint/Type;";
	private static final String TYPE_FIELDNAME_TESTOBJECT = "TESTOBJECT";
	private static final String TYPE_FIELDNAME_METHODPARAMETER = "METHOD_PARAMETER";

	private CodeAttribute codeAttribute;
	private CodeIterator iterator;
	private ConstPool constantPool;

	private SignatureAdder signatureAdder;

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if (TestgeneratorConfig.getClassName().equals(className)) {
			final ClassPool pool = ClassPool.getDefault();

			CtClass loadingClass = null;
			try (ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer)) {
				loadingClass = pool.makeClass(stream);

				reTransformMethodForObservObjectData(loadingClass);

				return loadingClass.toBytecode();

			} catch (Throwable e) {
				LOGGER.error(e);

				throw new AgentException("Es ist ein Fehler bei der Transfomation aufgetreten", e);
			} finally {
				if (loadingClass != null)
					loadingClass.detach();
			}

		}
		return classfileBuffer;
	}

	private void reTransformMethodForObservObjectData(CtClass classToLoad)
			throws IOException, CannotCompileException, NotFoundException, BadBytecode {

		MethodInfo methodInfo = classToLoad
				.getMethod(TestgeneratorConfig.getMethodName(), TestgeneratorConfig.getMethodDescriptor())
				.getMethodInfo();

		codeAttribute = methodInfo.getCodeAttribute();
		constantPool = codeAttribute.getConstPool();
		iterator = codeAttribute.iterator();
		signatureAdder = new SignatureAdder(constantPool);

		ClassFile classFile = classToLoad.getClassFile();

		if (!classFile.getFields().stream()
				.anyMatch(field -> TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE.equals(field.getName()))) {
			FieldInfo methodTypeTable = new FieldInfo(constantPool,
					TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE, JVMTypes.LIST);
			methodTypeTable.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.STATIC);
			classFile.addField(methodTypeTable);
		}

		addValueTrackingToMethod(classToLoad, methodInfo);

		TestGenerationAdder testGeneration = new TestGenerationAdder(classToLoad, codeAttribute);
		testGeneration.addTestgenerationToMethod(methodInfo);
	}

	private void addValueTrackingToMethod(CtClass classToLoad, MethodInfo methodInfo) throws BadBytecode {
		// if a method is not static the first argument to a method is this
		int lowestParameterIndex = Modifier.isStatic(methodInfo.getAccessFlags()) ? 0 : 1;

		int parameterCount = Descriptor.numOfParameters(methodInfo.getDescriptor());

		LocalVariableAttribute table = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

		LocalVariableTypeAttribute typeTable = (LocalVariableTypeAttribute) codeAttribute
				.getAttribute(LocalVariableTypeAttribute.tag);

		LOGGER.debug("Method before manipulation",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		int maxLocals = codeAttribute.getMaxLocals();
		int valueTrackerLocalIndex = maxLocals++;
		codeAttribute.setMaxLocals(maxLocals);

		Bytecode valueTracking = new Bytecode(constantPool);

		valueTracking.addNew(JVMTypes.ARRAYLIST_CLASSNAME);
		valueTracking.addOpcode(Opcode.DUP);
		valueTracking.addInvokespecial(JVMTypes.ARRAYLIST_CLASSNAME, MethodInfo.nameInit, METHOD_VOID_DESC);
		valueTracking.addPutstatic(classToLoad, TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE, JVMTypes.LIST);

		valueTracking.addInvokestatic(VALUE_STORAGE_CLASSNAME, METHOD_GET_INSTANCE,
				VALUE_STORAGE_METHOD_GET_INSTANCE_DESC);
		valueTracking.addInvokevirtual(VALUE_STORAGE_CLASSNAME, VALUE_STORAGE_METHOD_PUSH_NEW_TESTDATA,
				METHOD_VOID_DESC);

		valueTracking.addInvokestatic(OBJECT_VALUE_TRACKER_CLASSNAME, METHOD_GET_INSTANCE,
				OBJECT_VALUE_TRACKER_METHOD_GET_INSTANCE_DESC);
		valueTracking.addAstore(valueTrackerLocalIndex);

		for (int i = lowestParameterIndex; i <= parameterCount; i++) {
			String variableName = table.variableName(i);
			String descriptor = table.descriptor(i);

			addTypeToMethodParamTable(classToLoad, valueTracking, descriptor, typeTable, i);

			valueTracking.addAload(valueTrackerLocalIndex);
			BytecodeUtils.addLoad(valueTracking, i, descriptor);
			if (Primitives.isPrimitiveDataType(descriptor))
				BytecodeUtils.addBoxingForPrimitiveDataType(valueTracking, descriptor);

			valueTracking.addLdc(variableName);
			valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_METHODPARAMETER, TYPE);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
					OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);
		}

		valueTracking.addAload(valueTrackerLocalIndex);
		valueTracking.addAload(0);
		valueTracking.addLdc(createNameForTestobject(classToLoad.getName()));
		valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_TESTOBJECT, TYPE);
		valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
				OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);

		valueTracking.addAload(valueTrackerLocalIndex);
		valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME,
				OBJECT_VALUE_TRACKER_METHOD_ENABLE_PROXY_TRACKING, //
				METHOD_VOID_DESC);

		if (TestgeneratorConfig.traceReadFieldAccess()) {
			valueTracking.addAload(valueTrackerLocalIndex);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME,
					OBJECT_VALUE_TRACKER_METHOD_ENABLE_FIELD_TRACKING, METHOD_VOID_DESC);
		}

		iterator.insert(0, valueTracking.get());
	}

	private void addTypeToMethodParamTable(CtClass classToLoad, Bytecode code, String descriptor,
			LocalVariableTypeAttribute typeTable, int loadIndex) {
		code.addGetstatic(classToLoad, TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE, JVMTypes.LIST);

		boolean addedSignature = false;

		if (typeTable != null) {
			String signature = getSignatureOfTypeTable(typeTable, loadIndex);

			if (signature != null) {
				SignatureData signatureData = null;
				try {
					signatureData = SignatureParser.parse(signature);
				} catch (SignatureParserException e) {
					LOGGER.error(e);
				}

				if (signatureData != null) {
					Wrapper<Integer> localVariableCounter = new Wrapper<>(codeAttribute.getMaxLocals());
					int localVariableSignature = signatureAdder.add(code, signatureData, localVariableCounter);
					codeAttribute.setMaxLocals(localVariableCounter.getValue());

					code.addAload(localVariableSignature);

					addedSignature = true;
				}
			}
		}

		if (!addedSignature) {
			BytecodeUtils.addClassInfoToBytecode(code, constantPool, Descriptor.toClassName(descriptor));
			code.addInvokestatic(BASIC_TYPE_CLASSNAME, BASIC_TYPE_METHOD_OF, BASIC_TYPE_METHOD_OF_DESC);
		}

		code.addInvokeinterface(JVMTypes.LIST_CLASSNAME, JVMTypes.COLLECTION_METHOD_ADD,
				JVMTypes.COLLECTION_METHOD_ADD_DESC, 2);
		code.addOpcode(Opcode.POP);
	}

	private String getSignatureOfTypeTable(LocalVariableTypeAttribute typeTable, int slot) {
		for (int i = 0; i < typeTable.tableLength(); i++) {
			if (typeTable.index(i) == slot) {
				return typeTable.signature(i);
			}
		}

		return null;
	}

	private String createNameForTestobject(String className) {
		String newClassName = className.contains("$") ? className.substring(className.lastIndexOf("$") + 1)
				: className.substring(className.lastIndexOf('.') + 1);

		newClassName = newClassName.replace(newClassName.charAt(0), Character.toLowerCase(newClassName.charAt(0)));

		return newClassName;
	}
}
