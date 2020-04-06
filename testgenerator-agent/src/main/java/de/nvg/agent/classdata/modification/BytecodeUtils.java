package de.nvg.agent.classdata.modification;

import de.nvg.testgenerator.classdata.constants.JVMTypes;
import de.nvg.testgenerator.classdata.constants.Primitives;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;

public final class BytecodeUtils {

	private BytecodeUtils() {
	}

	public static void addClassInfoToBytecode(Bytecode code, ConstPool constantPool, String descriptor) {
		if (Primitives.PRIMTIVE_JAVA_TYPES.contains(descriptor)) {
			String className = null;

			if (Primitives.JAVA_BOOLEAN.equals(descriptor)) {
				className = JVMTypes.BOOLEAN_CLASSNAME;
			} else if (Primitives.JAVA_BYTE.equals(descriptor)) {
				className = JVMTypes.BYTE_CLASSNAME;
			} else if (Primitives.JAVA_CHAR.equals(descriptor)) {
				className = JVMTypes.CHAR_CLASSNAME;
			} else if (Primitives.JAVA_SHORT.equals(descriptor)) {
				className = JVMTypes.SHORT_CLASSNAME;
			} else if (Primitives.JAVA_INT.equals(descriptor)) {
				className = JVMTypes.INTEGER_CLASSNAME;
			} else if (Primitives.JAVA_FLOAT.equals(descriptor)) {
				className = JVMTypes.FLOAT_CLASSNAME;
			} else if (Primitives.JAVA_LONG.equals(descriptor)) {
				className = JVMTypes.LONG_CLASSNAME;
			} else if (Primitives.JAVA_DOUBLE.equals(descriptor)) {
				className = JVMTypes.DOUBLE_CLASSNAME;
			}

			code.addGetstatic(className, JVMTypes.WRAPPER_CLASSES_FIELD_TYPE, JVMTypes.CLASS);
		} else {
			code.addLdc(constantPool.addClassInfo(descriptor));
		}
	}

	public static String cnvDescriptorToJvmName(String descriptor) {
		return descriptor.substring(1, descriptor.length() - 1);
	}

}
