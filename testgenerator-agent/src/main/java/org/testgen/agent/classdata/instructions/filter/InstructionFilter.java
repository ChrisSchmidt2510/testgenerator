package org.testgen.agent.classdata.instructions.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.bytecode.BootstrapMethodsAttribute;
import javassist.bytecode.BootstrapMethodsAttribute.BootstrapMethod;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Opcode;

/**
 * Filtering through the instruction set of a single method respecting the
 * operand stack. All filtering options the implementation provides are
 * idempotent, so they return always the same result for the same parameters.
 * This filter doesn't support the full instruction set of the jvm. All kind of
 * control flow instructions are excluded e.g. switch or if instructions.
 * 
 * The short if expression <code>condition ? true : false</code> is a the only
 * type of control flow that is supported.
 *
 */
public abstract class InstructionFilter {

	protected static final String LAMBDAMETAFACTORY_METHOD_METAFACTORY = "metafactory";
	protected static final String LAMBDAMETAFACTORY_METHOD_ALT_METAFACTORY = "altMetafactory";
	protected static final String LAMBDAMETAFACTORY_CLASSNAME = "java.lang.invoke.LambdaMetafactory";

	protected final List<Instruction> instructions;
	protected final ClassFile classFile;
	protected final ConstPool constantPool;

	protected final List<Instruction> calledLoadInstructions = new ArrayList<>();

	protected final Logger logger = LogManager.getLogger(this.getClass());

	public InstructionFilter(ClassFile classFile, List<Instruction> instructions) {
		this.classFile = classFile;
		this.constantPool = classFile.getConstPool();
		this.instructions = simplifyInstructionTree(instructions);
	}

	/**
	 * Modifies the instruction-set of a method in the case that a if-else-cascade
	 * exists to manipulate the value of a field. Then will the else-instructions
	 * excluded from the modified instructions set. This is necessary to enable the
	 * reverse filtering of the correct aload-instruction of a field.
	 * 
	 * @param instructions
	 * @return a modified-instructions set without else-branch bytecodes, if a field
	 *         gets manipulated directly after the last instruction of the
	 *         else-branch
	 */
	private List<Instruction> simplifyInstructionTree(List<Instruction> instructions) {
		List<Instruction> branches = instructions.stream()
				.filter(inst -> Instructions.isOneItemComparison(inst) || Instructions.isTwoItemComparison(inst))
				.collect(Collectors.toList());

		List<Instruction> modifiedInstructions = new ArrayList<>(instructions);

		for (Instruction branchInstruction : branches) {

			int startElseBlockIndex = branchInstruction.getCodeArrayIndex() + branchInstruction.getOffset();

			Instruction startElseBlock = Instructions.getInstructionByCodeArrayIndex(instructions, startElseBlockIndex);

			if (Opcode.RETURN == startElseBlock.getOpcode())
				continue;

			Instruction gotoInstruction = Instructions.getBeforeInstruction(instructions, startElseBlock);

			if (Opcode.GOTO != gotoInstruction.getOpcode() && Opcode.GOTO_W != gotoInstruction.getOpcode())
				continue;

			int endIfElseCascade = gotoInstruction.getCodeArrayIndex() + gotoInstruction.getOffset();

			Instruction lastInstructionElseBlock = Instructions.getBeforeInstruction(instructions,
					Instructions.getInstructionByCodeArrayIndex(instructions, endIfElseCascade));

			Instruction lastInstructionIfBlock = Instructions.getBeforeInstruction(instructions, gotoInstruction);

			if (putsItemOnOperandStack(lastInstructionIfBlock) && putsItemOnOperandStack(lastInstructionElseBlock)) {

				List<Instruction> removedInstructions = instructions.stream()
						.filter(inst -> inst.getCodeArrayIndex() >= gotoInstruction.getCodeArrayIndex()
								&& inst.getCodeArrayIndex() < endIfElseCascade)
						.collect(Collectors.toList());

				logger.debug("removed Instructions");
				removedInstructions.forEach(inst -> logger.debug(inst.toString()));

				modifiedInstructions.removeAll(removedInstructions);

			}

		}

		return modifiedInstructions;
	}

	protected static boolean isDoubleOrLong(String type) {
		return Primitives.JAVA_DOUBLE.equals(type) || Primitives.JVM_DOUBLE.equals(type)
				|| Primitives.JAVA_LONG.equals(type) || Primitives.JVM_LONG.equals(type);
	}

	protected boolean putsItemOnOperandStack(Instruction instruction) {
		int opcode = instruction.getOpcode();

		return Instructions.isLoadInstruction(instruction) || Instructions.isPrimitiveConstantInstruction(instruction)
				|| Instructions.isArrayLoadInstruction(instruction) || Instructions.isPrimitiveCast(instruction)
				|| Opcode.CHECKCAST == opcode
				|| (Instructions.isInvokeInstruction(instruction)
						&& !Primitives.JVM_VOID.equals(Instructions.getReturnType(instruction.getType())))
				|| Opcode.INVOKEDYNAMIC == opcode || Opcode.GETFIELD == opcode || Opcode.GETSTATIC == opcode
				|| Opcode.LDC == opcode || Opcode.LDC2_W == opcode || Opcode.LDC_W == opcode || Opcode.DUP == opcode;
	}

	protected BootstrapMethod getBootstrapMethodForInvokeDynamicInstruction(Instruction instruction) {
		if (Opcode.INVOKEDYNAMIC != instruction.getOpcode())
			throw new IllegalArgumentException(
					String.format("Instruction %s has no InvokeDynamic opcode", instruction));

		BootstrapMethodsAttribute bootstrapMethodAttribute = (BootstrapMethodsAttribute) classFile
				.getAttribute(BootstrapMethodsAttribute.tag);

		return bootstrapMethodAttribute.getMethods()[instruction.getBootstrapMethodIndex()];
	}

	public List<Instruction> getCalledLoadInstructions() {
		return new ArrayList<>(calledLoadInstructions);
	}

}
