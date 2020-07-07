package de.nvg.agent.classdata.modification;

import org.testgen.core.classdata.constants.JVMTypes;
import org.testgen.core.classdata.constants.JavaTypes;
import org.testgen.core.classdata.constants.Primitives;

import javassist.bytecode.Bytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Opcode;

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

			if (Primitives.PRIMTIVE_JAVA_TYPES.contains(descriptor)) {
				if (Primitives.JAVA_BOOLEAN.equals(descriptor))
					builder.append(Primitives.JVM_BOOLEAN);
				else if (Primitives.JAVA_BYTE.equals(descriptor))
					builder.append(Primitives.JVM_BYTE);
				else if (Primitives.JAVA_CHAR.equals(descriptor))
					builder.append(Primitives.JVM_CHAR);
				else if (Primitives.JAVA_SHORT.equals(descriptor))
					builder.append(Primitives.JVM_SHORT);
				else if (Primitives.JAVA_INT.equals(descriptor))
					builder.append(Primitives.JVM_INT);
				else if (Primitives.JAVA_FLOAT.equals(descriptor))
					builder.append(Primitives.JVM_FLOAT);
				else if (Primitives.JAVA_DOUBLE.equals(descriptor))
					builder.append(Primitives.JVM_DOUBLE);
				else if (Primitives.JAVA_LONG.equals(descriptor)) {
					builder.append(Primitives.JVM_LONG);
				}
			} else {
				builder.append("L" + descriptor + ";");
			}

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

	public static void addLoad(Bytecode code, int index, String dataType) {
		if (Primitives.isPrimitiveDataType(dataType)) {
			switch (dataType) {
			case Primitives.JVM_BOOLEAN:
			case Primitives.JVM_BYTE:
			case Primitives.JVM_CHAR:
			case Primitives.JVM_SHORT:
			case Primitives.JVM_INT:
				code.addIload(index);
				break;
			case Primitives.JVM_FLOAT:
				code.addFload(index);
				break;
			case Primitives.JVM_DOUBLE:
				code.addDload(index);
				break;
			case Primitives.JVM_LONG:
				code.addLload(index);
				break;
			}
		} else
			code.addAload(index);
	}

	public static void addBoxingForPrimitiveDataType(Bytecode code, String dataType) {
		if (Primitives.JVM_BOOLEAN.equals(dataType)) {
			code.addInvokestatic(JVMTypes.BOOLEAN_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.BOOLEAN_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_BYTE.equals(dataType)) {
			code.addInvokestatic(JVMTypes.BYTE_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.BYTE_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_CHAR.equals(dataType)) {
			code.addInvokestatic(JVMTypes.CHAR_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.CHARACTER_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_SHORT.equals(dataType)) {
			code.addInvokestatic(JVMTypes.SHORT_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.SHORT_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_INT.equals(dataType)) {
			code.addInvokestatic(JVMTypes.INTEGER_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.INTEGER_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_FLOAT.equals(dataType)) {
			code.addInvokestatic(JVMTypes.FLOAT_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.FLOAT_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_DOUBLE.equals(dataType)) {
			code.addInvokestatic(JVMTypes.DOUBLE_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.DOUBLE_METHOD_VALUE_OF_DESC);
		} else if (Primitives.JVM_LONG.equals(dataType)) {
			code.addInvokestatic(JVMTypes.LONG_CLASSNAME, JVMTypes.WRAPPER_METHOD_VALUE_OF,
					JVMTypes.LONG_METHOD_VALUE_OF_DESC);
		}
	}

	public static void fillWithNOP(Bytecode code, int length) {
		for (int i = 0; i < length; i++) {
			code.add(Opcode.NOP);
		}
	}

}
