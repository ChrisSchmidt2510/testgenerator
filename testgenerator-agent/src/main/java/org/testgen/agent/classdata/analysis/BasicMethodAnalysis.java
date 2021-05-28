package org.testgen.agent.classdata.analysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public abstract class BasicMethodAnalysis implements MethodAnalysis2 {

	protected ClassData classData;
	protected ClassFile classFile;

	@Override
	public void setClassData(ClassData classData) {
		this.classData = classData;
	}

	@Override
	public void setClassFile(ClassFile classFile) {
		this.classFile = classFile;
	}

	protected boolean isMethodAccessible(int accessFlags) {
		return !AccessFlag.isPrivate(accessFlags) && (AccessFlag.SYNTHETIC & accessFlags) == 0;
	}

	protected boolean isTypeCollection(String descriptor) {
		try {
			CtClass clazz = Descriptor.toCtClass(descriptor, ClassPool.getDefault());

			return isCollection(clazz);
		} catch (NotFoundException e) {
			return false;
		}

	}

	private boolean isCollection(CtClass ctClass) throws NotFoundException {
		if (ctClass.isInterface())
			return JavaTypes.COLLECTION_LIST.contains(ctClass.getName());

		if (Arrays.stream(ctClass.getInterfaces())
				.anyMatch(inter -> JavaTypes.COLLECTION_LIST.contains(inter.getName())))
			return true;

		CtClass superClass = ctClass.getSuperclass();

		if (superClass != null && !JavaTypes.OBJECT.equals(superClass.getName()))
			return isCollection(superClass);

		return false;
	}

	protected Set<String> getImplementedCollections(String descriptor) {
		try {
			CtClass clazz = Descriptor.toCtClass(descriptor, ClassPool.getDefault());

			return getImplementedCollections(clazz);

		} catch (NotFoundException e) {
			return Collections.emptySet();
		}
	}

	private Set<String> getImplementedCollections(CtClass clazz) throws NotFoundException {
		if (clazz.isInterface() && JavaTypes.COLLECTION_LIST.contains(clazz.getName())) {
			return Collections
					.singleton(JavaTypes.COLLECTION_LIST.get(JavaTypes.COLLECTION_LIST.indexOf(clazz.getName())));
		}

		Set<String> implementedCollections = Arrays.stream(clazz.getInterfaces())
				.filter(inter -> JavaTypes.COLLECTION_LIST.contains(inter.getName())).map(CtClass::getName)
				.collect(Collectors.toSet());

		CtClass superclass = clazz.getSuperclass();

		if (superclass != null && !JavaTypes.OBJECT.equals(superclass.getName())) {
			getImplementedCollections(superclass).forEach(implementedCollections::add);
		}

		return implementedCollections;
	}

	protected boolean areAllMethodParametersUsed(List<Instruction> calledLoadInstructions,
			List<String> methodParameters, boolean isStaticMethod) {
		// if its a static method local variable index starts with 0
		int methodVariableIndex = isStaticMethod ? 0 : 1;

		for (String methodParam : methodParameters) {

			Predicate<Instruction> filter = null;

			switch (methodParam) {
			case Primitives.JVM_BOOLEAN:
			case Primitives.JVM_BYTE:
			case Primitives.JVM_CHAR:
			case Primitives.JVM_SHORT:
			case Primitives.JVM_INT:
				filter = getIntegerLoadingInstruction(methodVariableIndex);
				break;

			case Primitives.JVM_FLOAT:
				filter = getFloatLoadingInstruction(methodVariableIndex);
				break;

			case Primitives.JVM_DOUBLE:
				filter = getDoubleLoadingInstruction(methodVariableIndex);
				break;

			case Primitives.JVM_LONG:
				filter = getLongLoadingInstruction(methodVariableIndex);
				break;

			default:
				filter = getReferenceLoadingInstruction(methodVariableIndex);
				break;
			}

			if (calledLoadInstructions.stream().noneMatch(filter))
				return false;
		}

		return true;
	}

	private Predicate<Instruction> getIntegerLoadingInstruction(int methodVariableIndex) {
		switch (methodVariableIndex) {
		case 0:
			return inst -> Opcode.ILOAD_0 == inst.getOpcode();

		case 1:
			return inst -> Opcode.ILOAD_1 == inst.getOpcode();

		case 2:
			return inst -> Opcode.ILOAD_2 == inst.getOpcode();

		case 3:
			return inst -> Opcode.ILOAD_3 == inst.getOpcode();

		default:
			return inst -> Opcode.ILOAD == inst.getOpcode() && methodVariableIndex == inst.getLocalVariableIndex();
		}
	}

	private Predicate<Instruction> getFloatLoadingInstruction(int methodVariableIndex) {
		switch (methodVariableIndex) {
		case 0:
			return inst -> Opcode.FLOAD_0 == inst.getOpcode();

		case 1:
			return inst -> Opcode.FLOAD_1 == inst.getOpcode();

		case 2:
			return inst -> Opcode.FLOAD_2 == inst.getOpcode();

		case 3:
			return inst -> Opcode.FLOAD_3 == inst.getOpcode();

		default:
			return inst -> Opcode.FLOAD == inst.getOpcode() && methodVariableIndex == inst.getLocalVariableIndex();
		}
	}

	private Predicate<Instruction> getDoubleLoadingInstruction(int methodVariableIndex) {
		switch (methodVariableIndex) {
		case 0:
			return inst -> Opcode.DLOAD_0 == inst.getOpcode();

		case 1:
			return inst -> Opcode.DLOAD_1 == inst.getOpcode();

		case 2:
			return inst -> Opcode.DLOAD_2 == inst.getOpcode();

		case 3:
			return inst -> Opcode.DLOAD_3 == inst.getOpcode();

		default:
			return inst -> Opcode.DLOAD == inst.getOpcode() && methodVariableIndex == inst.getLocalVariableIndex();
		}
	}

	private Predicate<Instruction> getLongLoadingInstruction(int methodVariableIndex) {
		switch (methodVariableIndex) {
		case 0:
			return inst -> Opcode.LLOAD_0 == inst.getOpcode();

		case 1:
			return inst -> Opcode.LLOAD_1 == inst.getOpcode();

		case 2:
			return inst -> Opcode.LLOAD_2 == inst.getOpcode();

		case 3:
			return inst -> Opcode.LLOAD_3 == inst.getOpcode();

		default:
			return inst -> Opcode.LLOAD == inst.getOpcode() && methodVariableIndex == inst.getLocalVariableIndex();
		}
	}

	private Predicate<Instruction> getReferenceLoadingInstruction(int methodVariableIndex) {
		switch (methodVariableIndex) {
		case 0:
			return inst -> Opcode.ALOAD_0 == inst.getOpcode();

		case 1:
			return inst -> Opcode.ALOAD_1 == inst.getOpcode();

		case 2:
			return inst -> Opcode.ALOAD_2 == inst.getOpcode();

		case 3:
			return inst -> Opcode.ALOAD_3 == inst.getOpcode();

		default:
			return inst -> Opcode.ALOAD == inst.getOpcode() && methodVariableIndex == inst.getLocalVariableIndex();
		}
	}

	protected void addAnalysisResult(MethodInfo method, MethodType methodType, FieldData fieldData) {
		MethodData methodData = new MethodData(method.getName(), method.getDescriptor(), methodType,
				Modifier.isStatic(method.getAccessFlags()));

		classData.addMethod(methodData, fieldData);
	}

}
