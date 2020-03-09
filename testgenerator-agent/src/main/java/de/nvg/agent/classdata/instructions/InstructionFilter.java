package de.nvg.agent.classdata.instructions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import de.nvg.agent.classdata.modification.helper.CodeArrayModificator;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.logging.config.Level;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.Opcode;

public class InstructionFilter {
	private final List<Instruction> instructions;
	private final LocalVariableAttribute table;
	/**
	 * If the InstructionFilter is used in a Modification context of the filtered
	 * method the codeArrayModifcator is needed, because the indexes in the
	 * {@link LocalVariableAttribute} are also getting updated
	 */
	private final CodeArrayModificator codeArrayModificator;
	/**
	 * Key: className Value: number of needed allocations.
	 */
	private final Map<String, Integer> neededAloadInstructions = new HashMap<>();

	private boolean loggedLocalVariableTable = false;

	private static final Logger LOGGER = LogManager.getLogger(InstructionFilter.class);

	public InstructionFilter(List<Instruction> instructions, LocalVariableAttribute table,
			CodeArrayModificator codeArrayModificator) {
		this.instructions = instructions;
		this.table = table;
		this.codeArrayModificator = codeArrayModificator;
	}

	public final Instruction filterForMatchingAloadInstruction(Instruction searchInstruction) {
		neededAloadInstructions.put(Descriptor.toClassName(searchInstruction.getType()), 1);

		return filterForMatchingAloadInstructionIntern(searchInstruction,
				Instructions.getBeforeInstruction(instructions, searchInstruction), 0);
	}

	private final Instruction filterForMatchingAloadInstructionIntern(Instruction searchInstruction,
			Instruction currentInstruction, int numberOfParameters) {

		LOGGER.debug("search-Instruction " + searchInstruction + "\n" + //
				"current-Instruction " + currentInstruction + "\n" + //
				"needed Aload-Instructions: " + neededAloadInstructions + "\n" + //
				"methodParameter to fill: " + numberOfParameters);

		int searchInstCodeArrayIndex = searchInstruction.getCodeArrayIndex();
		Instruction beforeInstruction = Instructions.getBeforeInstruction(instructions, currentInstruction);

		if (Opcode.PUTFIELD == searchInstruction.getOpcode()
				&& searchInstruction.getClassRef().equals(Descriptor.toClassName(searchInstruction.getType()))) {
			return filterForMatchingAloadInstructionIntern(searchInstruction, currentInstruction, 1);
		}

		if (Instructions.isAloadInstruction(currentInstruction)) {

			String className = getDescriptorOfLoadInstruction(currentInstruction, searchInstCodeArrayIndex);

			if (searchInstruction.getClassRef().equals(className)
					&& neededAloadInstructions.getOrDefault(className, 0) == 0 && numberOfParameters == 0) {
				return currentInstruction;
			} else if (numberOfParameters == 0) {
				updateNeededAloadInstructions(className, false);
			}

		} else if (Opcode.DUP == currentInstruction.getOpcode()) {
			Instruction instruction = filterForMatchingAloadInstructionIntern(searchInstruction, beforeInstruction,
					numberOfParameters);

			if (beforeInstruction == instruction) {
				return currentInstruction;
			}
		}

		else if (Opcode.GETFIELD == currentInstruction.getOpcode() && //
				currentInstruction.getClassRef().equals(
						getDescriptorOfLoadInstruction(beforeInstruction, currentInstruction.getCodeArrayIndex()))) {
			updateNeededAloadInstructions(getDescriptorOfLoadInstruction(beforeInstruction, //
					currentInstruction.getCodeArrayIndex()), true);

			if (numberOfParameters != 0) {
				return filterForMatchingAloadInstructionIntern(currentInstruction, beforeInstruction,
						numberOfParameters--);
			}

			if (Instructions.isInvokeInstruction(searchInstruction)) {
				return currentInstruction;
			}
		}

		else if (Opcode.NEW == currentInstruction.getOpcode()
				&& currentInstruction.getClassRef().equals(searchInstruction.getClassRef())) {
			// auslagern in function
			if (neededAloadInstructions.getOrDefault(currentInstruction.getClassRef(), 0) == 0
					&& numberOfParameters == 0) {
				return currentInstruction;
			} else {
				updateNeededAloadInstructions(currentInstruction.getClassRef(), false);

				if (numberOfParameters != 0) {
					numberOfParameters--;
				}
			}

			if (Opcode.INVOKESPECIAL == searchInstruction.getOpcode()) {
				return currentInstruction;
			}
		}

		else if (Instructions.isInvokeInstruction(currentInstruction)) {
			int numOfParameters = getNumberOfMethodParameters(currentInstruction.getType());

			if (!(numOfParameters == 0 && Opcode.INVOKESTATIC == currentInstruction.getOpcode())) {

				Instruction invokeInstructionCaller = filterForMatchingAloadInstructionIntern(currentInstruction,
						beforeInstruction, numOfParameters);

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
						Instructions.getBeforeInstruction(instructions, invokeInstructionCaller), numberOfParameters);
			}
		}

		if (numberOfParameters != 0) {
			numberOfParameters--;
		}

		if (numberOfParameters == 0 && Opcode.INVOKESTATIC == searchInstruction.getOpcode()) {
			return currentInstruction;
		}

		Instruction instruction = filterForMatchingAloadInstructionIntern(searchInstruction, beforeInstruction,
				numberOfParameters);

		if ((neededAloadInstructions.getOrDefault(searchInstruction.getClassRef(), 0) == 0 && //
				Instructions.isAloadInstruction(instruction)) || //
				(Instructions.isInvokeInstruction(searchInstruction) && //
						numberOfParameters == 0)) {
			return instruction;
		}

		throw new NoSuchElementException(
				"No matching load-Instruction for search-Instruction " + searchInstruction + " found");
	}

	private void updateNeededAloadInstructions(String className, boolean increase) {
		if (increase) {
			if (neededAloadInstructions.containsKey(className)) {
				neededAloadInstructions.put(className, neededAloadInstructions.get(className) + 1);
			} else {
				neededAloadInstructions.put(className, 1);
			}

		} else {
			if (neededAloadInstructions.containsKey(className)) {
				neededAloadInstructions.put(className, neededAloadInstructions.get(className) - 1);
			} else {
				throw new IllegalArgumentException("For the Class " + className + " is no aload-Instruction needed");
			}
		}
	}

	private static int getNumberOfMethodParameters(String methodDescriptor) {
		String descriptor = Descriptor.getParamDescriptor(methodDescriptor);
		return Descriptor.numOfParameters(descriptor);
	}

	private String getDescriptorOfLoadInstruction(Instruction instruction, int codeArrayIndex) {
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
		if (LOGGER.isLevelActive(Level.DEBUG) && !loggedLocalVariableTable) {

			loggedLocalVariableTable = true;
			LOGGER.debug("LocalVariableTable");
			LOGGER.debug("Start\tLength\tSlot\tName\tSignature");

			for (int i = 0; i < table.tableLength(); i++) {
				LOGGER.debug(table.startPc(i) + "\t" + table.codeLength(i) + "\t" //
						+ table.index(i) + "\t" + table.variableName(i) + "\t"//
						+ table.signature(i));
			}
		}

		if (codeArrayModificator != null) {
			codeArrayIndex += codeArrayModificator.getModificator(codeArrayIndex);
		}

		LOGGER.debug("Searching for LocalVariable in Slot: " + slot + " and at CodeArrayIndex: " + codeArrayIndex);

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
