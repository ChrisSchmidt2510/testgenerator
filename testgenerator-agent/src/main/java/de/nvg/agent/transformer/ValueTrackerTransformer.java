package de.nvg.agent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import de.nvg.agent.AgentException;
import de.nvg.agent.classdata.analysis.signature.SignatureParser;
import de.nvg.agent.classdata.analysis.signature.SignatureParserException;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.agent.classdata.modification.SignatureAdder;
import de.nvg.agent.classdata.modification.TestGenerationAdder;
import de.nvg.testgenerator.TestgeneratorConstants;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.constants.JVMTypes;
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
import javassist.bytecode.LocalVariableTypeAttribute;
import javassist.bytecode.MethodInfo;

public class ValueTrackerTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ValueTrackerTransformer.class);

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

		classToLoad.addField(
				CtField.make("de.nvg.valuetracker.ObjectValueTracker "
						+ TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER + ";", classToLoad),
				"new de.nvg.valuetracker.ObjectValueTracker();");

		CtField methodSignature = CtField
				.make("java.util.Map " + TestgeneratorConstants.FIELDNAME_METHOD_SIGNATURE + ";", classToLoad);
		methodSignature.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
		classToLoad.addField(methodSignature, "new java.util.HashMap();");

		CtMethod method = classToLoad.getMethod(properties.getMethod(), properties.getMethodDescriptor());

		MethodInfo methodInfo = method.getMethodInfo();

		codeAttribute = methodInfo.getCodeAttribute();
		constantPool = codeAttribute.getConstPool();
		iterator = codeAttribute.iterator();

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

		for (int i = lowestParameterIndex; i <= parameterCount; i++) {
			String variableName = table.variableName(i);

			if (typeTable != null) {

				String signature = getSignatureofLocalVariableTypeTable(typeTable, i);
				addSignatureToMethodSignature(classToLoad, signatureAdder, valueTracking, signature, i);
			}

			valueTracking.addAload(0);
			valueTracking.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
					OBJECT_VALUE_TRACKER);
			valueTracking.addAload(i);
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

		if (properties.isTraceReadFieldAccess()) {
			valueTracking.addAload(0);
			valueTracking.addGetfield(classToLoad, TestgeneratorConstants.FIELDNAME_OBJECT_VALUE_TRACKER,
					OBJECT_VALUE_TRACKER);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME,
					OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS,
					OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS_DESC);
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
				code.addInvokestatic(JVMTypes.INTEGER_CLASSNAME, JVMTypes.INTEGER_METHOD_VALUE_OF,
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
		String newClassName = className.substring(className.lastIndexOf(".") + 1);

		newClassName = newClassName.replace(newClassName.charAt(0), Character.toLowerCase(newClassName.charAt(0)));

		return newClassName;
	}
}
