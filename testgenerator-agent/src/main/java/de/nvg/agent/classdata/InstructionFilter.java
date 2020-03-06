package de.nvg.agent.classdata;

import java.util.List;
import java.util.NoSuchElementException;

import de.nvg.testgenerator.Wrapper;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.Opcode;

public class InstructionFilter {
	private final List<Instruction> instructions;
	private final LocalVariableAttribute table;

	public InstructionFilter(List<Instruction> instructions, LocalVariableAttribute table) {
		this.instructions = instructions;
		this.table = table;
	}

	public final Instruction filterForMatchingAloadInstructionIntern(Instruction searchInstruction,
			Instruction currentInstruction, Wrapper<Integer> neededAloadInstructions, int numberOfParameters) {

		int searchInstCodeArrayIndex = searchInstruction.getCodeArrayIndex();
		Instruction beforeInstruction = Instructions.getBeforeInstruction(instructions, currentInstruction);

		if (Opcode.PUTFIELD == searchInstruction.getOpcode()
				&& searchInstruction.getClassRef().equals(Descriptor.toClassName(searchInstruction.getType()))) {
			return filterForMatchingAloadInstructionIntern(searchInstruction, currentInstruction,
					neededAloadInstructions, 1);
		}

		if (Instructions.isAloadInstruction(currentInstruction) && //
				(searchInstruction.getClassRef()
						.equals(getDescriptorOfLoadInstruction(currentInstruction, searchInstCodeArrayIndex))
						|| numberOfParameters != 0)) {

			if (neededAloadInstructions.getValue() == 0 && numberOfParameters == 0) {
				return currentInstruction;
			} else if (numberOfParameters == 0) {
				neededAloadInstructions.setValue(neededAloadInstructions.getValue() - 1);
			}

		} else if (Opcode.DUP == currentInstruction.getOpcode()) {
			Instruction instruction = filterForMatchingAloadInstructionIntern(searchInstruction, beforeInstruction,
					neededAloadInstructions, numberOfParameters);

			if (beforeInstruction == instruction) {
				return currentInstruction;
			}
		}

		else if (Opcode.GETFIELD == currentInstruction.getOpcode() && //
				currentInstruction.getClassRef().equals(
						getDescriptorOfLoadInstruction(beforeInstruction, currentInstruction.getCodeArrayIndex()))) {
			neededAloadInstructions.setValue(neededAloadInstructions.getValue() + 1);

			if (numberOfParameters != 0) {
				return filterForMatchingAloadInstructionIntern(currentInstruction, beforeInstruction, new Wrapper<>(1), //
						numberOfParameters--);
			}

			if (Instructions.isInvokeInstruction(searchInstruction)) {
				return currentInstruction;
			}
		}

		else if (Opcode.NEW == currentInstruction.getOpcode()
				&& currentInstruction.getClassRef().equals(searchInstruction.getClassRef())) {
			// auslagern in function
			if (neededAloadInstructions.getValue() == 0 && numberOfParameters == 0) {
				return currentInstruction;
			} else {
				neededAloadInstructions.setValue(neededAloadInstructions.getValue() - 1);

				if (numberOfParameters != 0) {
					numberOfParameters--;
				}
			}
		}

		else if (Instructions.isInvokeInstruction(currentInstruction)) {
			int numOfParameters = getNumberOfMethodParameters(currentInstruction.getType());

			if (!(numOfParameters == 0 && Opcode.INVOKESTATIC == currentInstruction.getOpcode())) {
				Wrapper<Integer> neededAloadInstructionsForInvocation = new Wrapper<>(0);

				Instruction invokeInstructionCaller = filterForMatchingAloadInstructionIntern(currentInstruction,
						beforeInstruction, neededAloadInstructionsForInvocation, numOfParameters);

				if (numberOfParameters != 0) {
					numberOfParameters--;
				}

				if (Opcode.INVOKESTATIC == searchInstruction.getOpcode()) {
					return invokeInstructionCaller;
				} else if (Instructions.isInvokeInstruction(searchInstruction)) {
					String returnType = Descriptor
							.toClassName(Instructions.getReturnType(currentInstruction.getType()));

					if (returnType.equals(searchInstruction.getClassRef())) {
						return invokeInstructionCaller;
					}
				}

				return filterForMatchingAloadInstructionIntern(searchInstruction,
						Instructions.getBeforeInstruction(instructions, invokeInstructionCaller),
						neededAloadInstructionsForInvocation, numberOfParameters);
			}
		}

		if (numberOfParameters != 0) {
			numberOfParameters--;
		}

		if (numberOfParameters == 0 && Opcode.INVOKESTATIC == searchInstruction.getOpcode()) {
			return currentInstruction;
		}

		Instruction instruction = filterForMatchingAloadInstructionIntern(searchInstruction, beforeInstruction,
				neededAloadInstructions, numberOfParameters);

		if ((neededAloadInstructions.getValue() == 0 && //
				Instructions.isAloadInstruction(instruction)) || //
				(Instructions.isInvokeInstruction(searchInstruction) && //
						numberOfParameters == 0)) {
			return instruction;
		}

		throw new NoSuchElementException(
				"No matching load-Instruction for search-Instruction " + searchInstruction + " found");
	}

	private static int getNumberOfMethodParameters(String methodDescriptor) {
		String descriptor = Descriptor.getParamDescriptor(methodDescriptor);
		return Descriptor.numOfParameters(descriptor);
	}

	public String getDescriptorOfLoadInstruction(Instruction instruction, int codeArrayIndex) {
		switch (instruction.getOpcode()) {
		case Opcode.ALOAD_0:
			return getDescriptorOfLocalVariable(0, codeArrayIndex);
		case Opcode.ALOAD_1:
			return getDescriptorOfLocalVariable(1, codeArrayIndex);
		case Opcode.ALOAD_2:
			return getDescriptorOfLocalVariable(2, codeArrayIndex);
		case Opcode.ALOAD_3:
			return getDescriptorOfLocalVariable(3, codeArrayIndex);
		case Opcode.ALOAD:
			return getDescriptorOfLocalVariable(instruction.getLocalVariableIndex(), codeArrayIndex);
		}

		throw new IllegalArgumentException("Invalid aload instruction" + instruction);
	}

	private String getDescriptorOfLocalVariable(int slot, int codeArrayIndex) {
		for (int i = 0; i < table.tableLength(); i++) {
			int startIndex = table.startPc(i);
			if (table.index(i) == slot && startIndex < codeArrayIndex
					&& startIndex + table.codeLength(i) > codeArrayIndex) {
				return Descriptor.toClassName(table.signature(i));
			}
		}

		throw new IllegalArgumentException("No valid entry in the LocalVariableTable");
	}

}