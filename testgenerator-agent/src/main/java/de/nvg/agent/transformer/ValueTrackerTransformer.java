package de.nvg.agent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.Wrapper;
import org.testgen.core.classdata.constants.JVMTypes;
import org.testgen.core.classdata.constants.Primitives;
import org.testgen.core.logging.LogManager;
import org.testgen.core.logging.Logger;
import org.testgen.core.properties.AgentProperties;

import de.nvg.agent.AgentException;
import de.nvg.agent.classdata.analysis.signature.SignatureParser;
import de.nvg.agent.classdata.analysis.signature.SignatureParserException;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.agent.classdata.modification.BytecodeUtils;
import de.nvg.agent.classdata.modification.SignatureAdder;
import de.nvg.agent.classdata.modification.TestGenerationAdder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.LocalVariableTypeAttribute;
import javassist.bytecode.MethodInfo;

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

	private static final String METHOD_GET_INSTANCE = "getInstance";
	private static final String METHOD_VOID_DESC = "()V";

	private static final String TYPE_CLASSNAME = "de/nvg/valuetracker/blueprint/Type";
	private static final String TYPE = "Lde/nvg/valuetracker/blueprint/Type;";
	private static final String TYPE_FIELDNAME_TESTOBJECT = "TESTOBJECT";
	private static final String TYPE_FIELDNAME_METHODPARAMETER = "METHOD_PARAMETER";

	private final AgentProperties properties = AgentProperties.getInstance();

	private CodeAttribute codeAttribute;
	private CodeIterator iterator;
	private ConstPool constantPool;

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

		CtField methodSignature = CtField
				.make("java.util.Map " + TestgeneratorConstants.FIELDNAME_METHOD_SIGNATURE + ";", classToLoad);
		methodSignature.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
		classToLoad.addField(methodSignature, "new java.util.HashMap();");

		MethodInfo methodInfo = classToLoad.getMethod(properties.getMethod(), properties.getMethodDescriptor())
				.getMethodInfo();

		codeAttribute = methodInfo.getCodeAttribute();
		constantPool = codeAttribute.getConstPool();
		iterator = codeAttribute.iterator();

		classToLoad.getClassFile().addField(new FieldInfo(constantPool,
				TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER, OBJECT_VALUE_TRACKER));

		SignatureAdder signatureAdder = new SignatureAdder(constantPool);

		addValueTrackingToMethod(classToLoad, methodInfo, signatureAdder);

		TestGenerationAdder testGeneration = new TestGenerationAdder(codeAttribute);
		testGeneration.addTestgenerationToMethod(methodInfo);

		byte[] bytecode = classToLoad.toBytecode();

		classToLoad.detach();

		return bytecode;
	}

	private void addValueTrackingToMethod(CtClass classToLoad, MethodInfo methodInfo, SignatureAdder signatureAdder)
			throws BadBytecode {
		// if a method is not static the first argument to a method is this
		int lowestParameterIndex = Modifier.isStatic(methodInfo.getAccessFlags()) ? 0 : 1;

		int parameterCount = Descriptor.numOfParameters(methodInfo.getDescriptor());

		LocalVariableAttribute table = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

		LocalVariableTypeAttribute typeTable = (LocalVariableTypeAttribute) codeAttribute
				.getAttribute(LocalVariableTypeAttribute.tag);

		LOGGER.debug("Method before manipulation",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		Bytecode valueTracking = new Bytecode(constantPool);

		valueTracking.addInvokestatic(VALUE_STORAGE_CLASSNAME, METHOD_GET_INSTANCE,
				VALUE_STORAGE_METHOD_GET_INSTANCE_DESC);
		valueTracking.addInvokevirtual(VALUE_STORAGE_CLASSNAME, VALUE_STORAGE_METHOD_PUSH_NEW_TESTDATA,
				METHOD_VOID_DESC);

		valueTracking.addAload(0);
		valueTracking.addInvokestatic(OBJECT_VALUE_TRACKER_CLASSNAME, METHOD_GET_INSTANCE,
				OBJECT_VALUE_TRACKER_METHOD_GET_INSTANCE_DESC);
		valueTracking.addPutfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
				OBJECT_VALUE_TRACKER);

		for (int i = lowestParameterIndex; i <= parameterCount; i++) {
			String variableName = table.variableName(i);
			String descriptor = table.descriptor(i);

			if (typeTable != null) {

				String signature = getSignatureofLocalVariableTypeTable(typeTable, i);
				addSignatureToMethodSignature(classToLoad, signatureAdder, valueTracking, signature, i);
			}

			valueTracking.addAload(0);
			valueTracking.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
					OBJECT_VALUE_TRACKER);
			BytecodeUtils.addLoad(valueTracking, i, descriptor);
			if (Primitives.isPrimitiveDataType(descriptor))
				BytecodeUtils.addBoxingForPrimitiveDataType(valueTracking, descriptor);

			valueTracking.addLdc(variableName);
			valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_METHODPARAMETER, TYPE);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
					OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);
		}

		valueTracking.addAload(0);
		valueTracking.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
				OBJECT_VALUE_TRACKER);
		valueTracking.addAload(0);
		valueTracking.addLdc(createNameForTestobject(classToLoad.getName()));
		valueTracking.addGetstatic(TYPE_CLASSNAME, TYPE_FIELDNAME_TESTOBJECT, TYPE);
		valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME, OBJECT_VALUE_TRACKER_METHOD_TRACK,
				OBJECT_VALUE_TRACKER_METHOD_TRACK_DESC);

		valueTracking.addAload(0);
		valueTracking.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
				OBJECT_VALUE_TRACKER);
		valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME,
				OBJECT_VALUE_TRACKER_METHOD_ENABLE_PROXY_TRACKING, //
				METHOD_VOID_DESC);

		if (properties.isTraceReadFieldAccess()) {
			valueTracking.addAload(0);
			valueTracking.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
					OBJECT_VALUE_TRACKER);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME,
					OBJECT_VALUE_TRACKER_METHOD_ENABLE_FIELD_TRACKING, METHOD_VOID_DESC);
		}

		iterator.insert(0, valueTracking.get());
	}

	private void addSignatureToMethodSignature(CtClass classToLoad, SignatureAdder signatureAdder, Bytecode code,
			String signature, int parameterIndex) {
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

				code.addAload(0);
				code.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_METHOD_SIGNATURE, JVMTypes.MAP);
				code.addIconst(parameterIndex);
				code.addInvokestatic(JVMTypes.INTEGER_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
						JVMTypes.INTEGER_METHOD_VALUE_OF_DESC);
				code.addAload(localVariableSignature);
				code.addInvokeinterface(JVMTypes.MAP_CLASSNAME, JVMTypes.MAP_METHOD_PUT, JVMTypes.MAP_METHOD_PUT_DESC,
						3);

			}
		}
	}

	private String getSignatureofLocalVariableTypeTable(LocalVariableTypeAttribute typeTable, int slot) {
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
