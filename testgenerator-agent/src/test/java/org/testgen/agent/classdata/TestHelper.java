package org.testgen.agent.classdata;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;

public class TestHelper {
	protected ClassFile classFile;
	protected CtClass ctClass;
	protected MethodInfo methodInfo;
	protected ConstPool constantPool;
	protected List<Instruction> instructions;
	protected Map<Integer, List<Instruction>> filteredInstructions;
	protected CodeAttribute codeAttribute;

	public void init(Class<?> clazz, String methodName, String methodDiscriptor, List<Integer> opcodes)
			throws NotFoundException, BadBytecode {
		ClassPool classPool = ClassPool.getDefault();

		ctClass = classPool.get(clazz.getName());

		classFile = ctClass.getClassFile();

		if (methodName != null) {
			MethodInfo methodInfo = null;

			if (methodDiscriptor == null) {
				methodInfo = classFile.getMethod(methodName);
			} else {
				methodInfo = classFile.getMethods().stream()
						.filter(method -> method.getName().equals(methodName)
								&& method.getDescriptor().equals(methodDiscriptor))
						.findAny().orElseThrow(() -> new NoSuchElementException());
			}
			this.methodInfo = methodInfo;

			constantPool = methodInfo.getConstPool();

			codeAttribute = methodInfo.getCodeAttribute();

			instructions = Instructions.getAllInstructions(methodInfo);

			filteredInstructions = Instructions.getFilteredInstructions(instructions, opcodes);

		}
	}

	public void init(Class<?> clazz, String methodName, List<Integer> opcodes) throws NotFoundException, BadBytecode {
		init(clazz, methodName, null, opcodes);
	}

	public void init(Class<?> clazz, String methodName) throws NotFoundException, BadBytecode {
		init(clazz, methodName, null, null);
	}

	public void init(Class<?> clazz) throws NotFoundException, BadBytecode {
		init(clazz, null, null, null);
	}

}
