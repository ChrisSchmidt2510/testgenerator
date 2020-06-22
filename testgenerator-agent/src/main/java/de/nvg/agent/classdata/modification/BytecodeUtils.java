package de.nvg.agent.classdata.modification;

import org.testgen.core.classdata.constants.JVMTypes;
import org.testgen.core.classdata.constants.JavaTypes;
import org.testgen.core.classdata.constants.Primitives;

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
		} else if (JavaTypes.isArray(descriptor)) {

			int dimCounter = 0;
			while (descriptor.contains("[]")) {
				dimCounter++;
				descriptor = descriptor.substring(0, descriptor.length() - 2);
			}

			StringBuilder builder = new StringBuilder();
			while (dimCounter > 0) {
				builder.append("[");
				dimCounter--;
			}

			builder.append("L" + descriptor + ";");
			code.addLdc(constantPool.addClassInfo(builder.toString()));
		} else {
			code.addLdc(constantPool.addClassInfo(descriptor));
		}
	}

	public static String cnvDescriptorToJvmName(String descriptor) {
		return isArrayType(descriptor) ? descriptor : descriptor.substring(1, descriptor.length() - 1);
	}

	public static boolean isArrayType(String dataType) {
		return dataType.startsWith("[");
	}

}
