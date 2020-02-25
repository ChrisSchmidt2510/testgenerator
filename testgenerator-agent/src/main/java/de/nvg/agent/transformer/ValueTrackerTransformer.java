package de.nvg.agent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import de.nvg.agent.AgentException;
import de.nvg.agent.classdata.Instructions;
import de.nvg.agent.classdata.modification.TestGenerationAdder;
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

	private static final String FIELD_NAME = "valueTracker";

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

		classToLoad.addField(CtField.make("de.nvg.valuetracker.ObjectValueTracker " + FIELD_NAME + ";", classToLoad),
				"new de.nvg.valuetracker.ObjectValueTracker();");

		CtMethod method = classToLoad.getMethod(properties.getMethod(), properties.getMethodDescriptor());

		MethodInfo methodInfo = method.getMethodInfo();

		codeAttribute = methodInfo.getCodeAttribute();
		constantPool = codeAttribute.getConstPool();
		iterator = codeAttribute.iterator();

		addValueTrackingToMethod(classToLoad, methodInfo);

		TestGenerationAdder testGeneration = new TestGenerationAdder(codeAttribute);
		testGeneration.addTestgenerationToMethod(methodInfo);

		byte[] bytecode = classToLoad.toBytecode();

		classToLoad.detach();

		return bytecode;
	}

	private void addValueTrackingToMethod(CtClass classToLoad, MethodInfo methodInfo) throws BadBytecode {
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

		if (properties.isTraceGetterCalls()) {
			valueTracking.addAload(0);
			valueTracking.addGetfield(classToLoad, FIELD_NAME, OBJECT_VALUE_TRACKER);
			valueTracking.addInvokevirtual(OBJECT_VALUE_TRACKER_CLASSNAME,
					OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS,
					OBJECT_VALUE_TRACKER_METHOD_ENABLE_GETTER_CALLS_DESC);
		}

		iterator.insert(0, valueTracking.get());
	}

	private String createNameForTestobject(String className) {
		String newClassName = className.substring(className.lastIndexOf(".") + 1);

		newClassName = newClassName.replace(newClassName.charAt(0), Character.toLowerCase(newClassName.charAt(0)));

		return newClassName;
	}
}
