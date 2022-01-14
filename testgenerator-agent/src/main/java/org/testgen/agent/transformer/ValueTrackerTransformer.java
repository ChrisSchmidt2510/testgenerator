package org.testgen.agent.transformer;

import org.testgen.agent.AgentException;
import org.testgen.agent.classdata.analysis.signature.SignatureParser;
import org.testgen.agent.classdata.analysis.signature.SignatureParserException;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.modification.BytecodeUtils;
import org.testgen.agent.classdata.modification.SignatureAdder;
import org.testgen.agent.classdata.modification.TestGenerationAdder;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.Wrapper;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

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

public class ValueTrackerTransformer implements ClassTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ValueTrackerTransformer.class);

	private static final String OBJECT_VALUE_TRACKER_CLASSNAME = "org/testgen/runtime/valuetracker/ObjectValueTracker";
	private static final String OBJECT_VALUE_TRACKER = "Lorg/testgen/runtime/valuetracker/ObjectValueTracker;";
	private static final String OBJECT_VALUE_TRACKER_METHOD_TRACK = "track";
	private static final String OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC = "(Ljava/lang/Object;Ljava/lang/String;Lorg/testgen/runtime/valuetracker/blueprint/Type;)V";
	private static final String OBJECT_VALUE_TRACKER_METHOD_GET_INSTANCE_DESC = "()" + OBJECT_VALUE_TRACKER;

	private static final String VALUE_STORAGE_CLASSNAME = "org/testgen/runtime/valuetracker/storage/ValueStorage";
	private static final String VALUE_STORAGE_METHOD_GET_INSTANCE_DESC = "()Lorg/testgen/runtime/valuetracker/storage/ValueStorage;";
	private static final String VALUE_STORAGE_METHOD_PUSH_NEW_TESTDATA = "pushNewTestData";

	private static final String BASIC_TYPE_CLASSNAME = "org/testgen/runtime/classdata/model/descriptor/BasicType";
	private static final String BASIC_TYPE_METHOD_OF = "of";
	private static final String BASIC_TYPE_METHOD_OF_DESC = "(Ljava/lang/Class;)Lorg/testgen/runtime/classdata/model/descriptor/BasicType;";

	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_VOID_DESC = "()V";

	private static final String TYPE_CLASSNAME = "org/testgen/runtime/valuetracker/blueprint/Type";
	private static final String TYPE = "Lorg/testgen/runtime/valuetracker/blueprint/Type;";
	private static final String TYPE_FIELDNAME_TESTOBJECT = "TESTOBJECT";
	private static final String TYPE_FIELDNAME_METHODPARAMETER = "METHOD_PARAMETER";

	private static final String TESTGENERATOR_CONFIG_CLASSNAME = "org/testgen/config/TestgeneratorConfig";
	private static final String TESTGENERATOR_CONFIG_METHOD_SET_PROXY_TRACKING = "setProxyTracking";
	private static final String TESTGENERATOR_CONFIG_METHOD_SET_FIELD_TRACKING = "setFieldTracking";
	private static final String TESTGENERATOR_CONFIG_METHOD_DESC = "(Z)V";

	CodeAttribute codeAttribute;

	@Override
	public boolean modifyClassFile(String className, CtClass ctClass) {
		if (!TestgeneratorConfig.getClassName().equals(className))
			return false;

		try {
			MethodInfo methodInfo = ctClass
					.getMethod(TestgeneratorConfig.getMethodName(), TestgeneratorConfig.getMethodDescriptor())
					.getMethodInfo();

			if (methodInfo.isConstructor())
				throw new AgentException("constructors are currently not supported");

			if (methodInfo.isStaticInitializer())
				throw new AgentException("static initializers are currently not supported");

			int modifier = methodInfo.getAccessFlags();

			if (!Modifier.isPublic(modifier) && !Modifier.isPackage(modifier))
				throw new AgentException(methodInfo + " need to be public or package");

			if (Modifier.isAbstract(modifier))
				throw new AgentException(methodInfo + "can`t be abstract");

			return true;

		} catch (NotFoundException e) {
			throw new AgentException("Method not Found " + TestgeneratorConfig.getMethodName()
					+ TestgeneratorConfig.getMethodDescriptor(), e);
		}

	}

	@Override
	public void transformClassFile(String className, CtClass ctClass) {
		try {
			MethodInfo methodInfo = ctClass
					.getMethod(TestgeneratorConfig.getMethodName(), TestgeneratorConfig.getMethodDescriptor())
					.getMethodInfo();

			codeAttribute = methodInfo.getCodeAttribute();
			ConstPool constantPool = codeAttribute.getConstPool();

			ClassFile classFile = ctClass.getClassFile();

			if (!classFile.getFields().stream().anyMatch(
					field -> TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE.equals(field.getName()))) {
				FieldInfo methodTypeTable = new FieldInfo(constantPool,
						TestgeneratorConstants.FIELDNAME_METHOD_PARAMETER_TABLE, JVMTypes.LIST);
				methodTypeTable.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.STATIC);
				classFile.addField(methodTypeTable);
			}

			addValueTrackingToMethod(ctClass, methodInfo);

			TestGenerationAdder testGeneration = new TestGenerationAdder(ctClass, codeAttribute,
					Modifier.isStatic(methodInfo.getAccessFlags()));
			testGeneration.addTestgenerationToMethod(methodInfo);
		} catch (Exception e) {
			LOGGER.error("error while transforming class", e);
			throw new AgentException("error while transforming class", e);
		}

	}

	void addValueTrackingToMethod(CtClass classToLoad, MethodInfo methodInfo) throws BadBytecode {
		boolean isStatic = Modifier.isStatic(methodInfo.getAccessFlags());

		CodeIterator iterator = codeAttribute.iterator();
		ConstPool constantPool = methodInfo.getConstPool();

		// if a method is not static the first argument to a method is this
		int lowestParameterIndex = isStatic ? 0 : 1;

		int parameterCount = Descriptor.numOfParameters(methodInfo.getDescriptor());

		LocalVariableAttribute table = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

		LocalVariableTypeAttribute typeTable = (LocalVariableTypeAttribute) codeAttribute
				.getAttribute(LocalVariableTypeAttribute.tag);

		LOGGER.debug("Method before manipulation", () -> Instructions.printCodeArray(iterator, constantPool));

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

		if (!isStatic) {
			// if the method is not static methodparameters start at index 1 and so the
			// parametercount need to be extended by 1
			++parameterCount;
		}

		for (int i = lowestParameterIndex; i < parameterCount; i++) {
			String variableName = table.variableName(i);
			String descriptor = table.descriptor(i);

			addTypeToMethodParamTable(classToLoad, valueTracking, descriptor, typeTable, i);

			valueTracking.addAload(valueTrackerLocalIndex);
			BytecodeUtils.addLoad(valueTracking, i, descriptor);
			if (Primitives.isPrimitiveJVMDataType(descriptor)) {
				BytecodeUtils.addBoxingForPrimitiveDataType(valueTracking, descriptor);
			}

			valueTracking.addLdc(variableName);
			valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_METHODPARAMETER, TYPE);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
					OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);
		}

		if (!isStatic) {
			valueTracking.addAload(valueTrackerLocalIndex);
			valueTracking.addAload(0);
			valueTracking.addLdc(createNameForTestobject(classToLoad.getName()));
			valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_TESTOBJECT, TYPE);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
					OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);
		}

		valueTracking.addIconst(1);
		valueTracking.addInvokestatic(TESTGENERATOR_CONFIG_CLASSNAME, TESTGENERATOR_CONFIG_METHOD_SET_PROXY_TRACKING,
				TESTGENERATOR_CONFIG_METHOD_DESC);

		if (TestgeneratorConfig.traceReadFieldAccess()) {
			valueTracking.addIconst(1);
			valueTracking.addInvokestatic(TESTGENERATOR_CONFIG_CLASSNAME,
					TESTGENERATOR_CONFIG_METHOD_SET_FIELD_TRACKING, TESTGENERATOR_CONFIG_METHOD_DESC);
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
					LOGGER.error("error while parsing signature " + signature, e);
				}

				if (signatureData != null) {
					Wrapper<Integer> localVariableCounter = new Wrapper<>(codeAttribute.getMaxLocals());
					int localVariableSignature = SignatureAdder.add(code, signatureData, localVariableCounter);
					codeAttribute.setMaxLocals(localVariableCounter.getValue());

					code.addAload(localVariableSignature);

					addedSignature = true;
				}
			}
		}

		if (!addedSignature) {
			BytecodeUtils.addClassInfoToBytecode(code, Descriptor.toClassName(descriptor));
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
