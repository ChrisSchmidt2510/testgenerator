package de.nvg.javaagent.transformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import de.nvg.javaagent.AgentException;
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
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ValueTrackerTransformer implements ClassFileTransformer {
	private AgentProperties properties = AgentProperties.getInstance();

	private Logger LOGGER = LogManager.getLogger(ValueTrackerTransformer.class);

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if (properties.getClassName().equals(className)) {
			final ClassPool pool = ClassPool.getDefault();
			try {
				CtClass classToLoad = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

				return reTransformMethodForObservObjectData(classToLoad);

			} catch (IOException | CannotCompileException | NotFoundException e) {
				LOGGER.error(e);
				throw new AgentException("Es ist ein Fehler bei der Transfomation aufgetreten", e);
			}

		}
		return classfileBuffer;
	}

	private byte[] reTransformMethodForObservObjectData(CtClass classToLoad)
			throws IOException, CannotCompileException, NotFoundException {

		classToLoad.addField(CtField.make("de.nvg.valuetracker.ObjectValueTracker valueTracker;", classToLoad),
				"new de.nvg.valuetracker.ObjectValueTracker();");

		CtMethod method = classToLoad.getMethod(properties.getMethod(), properties.getMethodDescriptor());

		MethodInfo methodInfo = method.getMethodInfo();
		LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute()
				.getAttribute(LocalVariableAttribute.tag);

		// if a method is not static the first argument to a method is this
		int lowestParameterIndex = Modifier.isStatic(method.getModifiers()) ? 0 : 1;

		int parameterCount = countMethodParameters(properties.getMethodDescriptor());

		method.insertAfter("de.nvg.testgenerator.generation.Testgenerator.generate(\"" + properties.getClassName()
				+ "\",\"" + properties.getMethod() + "\");");

		method.insertBefore("valueTracker.enableGetterCallsTracking();");
		for (int i = parameterCount; i >= lowestParameterIndex; i--) {
			String variableName = table.variableName(i);
			method.insertBefore("valueTracker.track(" + variableName + ", \"" + variableName + "\");");
		}

		byte[] bytecode = classToLoad.toBytecode();

		classToLoad.detach();

		return bytecode;
	}

	private static int countMethodParameters(String methodDescriptor) {

		String descriptor = methodDescriptor;

		descriptor = descriptor.substring(0, descriptor.indexOf(")"));
		if (descriptor.contains(";")) {
			int counter = 0;
			do {
				descriptor = descriptor.substring(descriptor.indexOf(";") + 1);
				counter++;
			} while (descriptor.contains(";"));
			return counter;
		}
		return 0;
	}

}
