package de.nvg.agent.classdata.modification;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.Instructions;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;

public class TestHelper {
	ClassFile classFile;
	CtClass ctClass;
	ConstPool constantPool;
	List<Instruction> instructions;
	Map<Integer, List<Instruction>> filteredInstructions;
	CodeAttribute codeAttribute;

	public TestHelper init(String className, String methodName, String methodDiscriptor, List<Integer> opcodes)
			throws NotFoundException, BadBytecode {
		ClassPool classPool = ClassPool.getDefault();

		ctClass = classPool.get(className);

		classFile = ctClass.getClassFile();

		MethodInfo methodInfo = null;

		if (methodDiscriptor == null) {
			methodInfo = classFile.getMethod(methodName);
		} else {
			methodInfo = classFile.getMethods().stream().filter(
					method -> method.getName().equals(methodName) && method.getDescriptor().equals(methodDiscriptor))
					.findAny().orElseThrow(() -> new NoSuchElementException());
		}

		constantPool = methodInfo.getConstPool();

		codeAttribute = methodInfo.getCodeAttribute();

		instructions = Instructions.getAllInstructions(methodInfo);

		filteredInstructions = Instructions.getFilteredInstructions(instructions, opcodes);

		// TODO CS
//		Logger.getInstance().setLevel(Level.TRACE);

		return this;
	}

	public TestHelper init(String className, String methodName, List<Integer> opcodes)
			throws NotFoundException, BadBytecode {
		return init(className, methodName, null, opcodes);
	}

	public TestHelper init(String className, String methodName) throws NotFoundException, BadBytecode {
		return init(className, methodName, null, null);
	}

}
