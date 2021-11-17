package org.testgen.agent.classdata.analysis.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.testgen.agent.classdata.analysis.BasicMethodAnalysis;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.instructions.filter.ReverseInstructionFilter;
import org.testgen.agent.classdata.model.ConstructorData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.Opcode;

/**
 * This analyzer is created to analyze a constructor of a class. Goal of this
 * analyzer is to find the easiest way of creating an instance of a class.
 * 
 * The following things were analyzed:<br>
 * - direct field access e.g. <code> this.field = field</code><br>
 * - accessing fields with a setter e.g.<code> setField(field)</code><br>
 * - accessing fields with a subconstructor <code> this(field) </code><br>
 * <br>
 * This wont recognized:<br>
 * - fields that will initialized with null<br>
 * - fields of a superclass<br>
 * - subconstructor of a superclass<br>
 *
 */
public class ConstructorAnalyser extends BasicMethodAnalysis {

	private static final Logger LOGGER = LogManager.getLogger(ConstructorAnalyser.class);

	private static final Function<String, Boolean> REFERENCE_CHECK = type -> JVMTypes.isArrayType(type)
			|| JVMTypes.isReferenceType(type);
	private static final Function<String, Boolean> ILOAD_CHECK = type -> Primitives.isILoadPrimitive(type);
	private static final Function<String, Boolean> FLOAD_CHECK = type -> Primitives.JVM_FLOAT.equals(type);
	private static final Function<String, Boolean> DLOAD_CHECK = type -> Primitives.JVM_DOUBLE.equals(type);
	private static final Function<String, Boolean> LLOAD_CHECK = type -> Primitives.JVM_LONG.equals(type);

	private Map<MethodInfo, ConstructorData> analysisResults = new HashMap<>();

	@Override
	public boolean canAnalysisBeApplied(MethodInfo method) {
		return MethodInfo.nameInit.equals(method.getName()) && AccessFlag.isPublic(method.getAccessFlags())
				&& (!classData.hasDefaultConstructor()
						|| (classData.getConstructor() != null && classData.getConstructor().getConstructorElements()
								.size() > Descriptor.numOfParameters(method.getDescriptor())));
	}

	@Override
	public boolean hasAnalysisMatched(MethodInfo method, List<Instruction> instructions) {

		String descriptor = method.getDescriptor();

		if (Descriptor.numOfParameters(descriptor) == 0) {
			classData.setDefaultConstructor(true);
			return true;

		} else {
			Map<Integer, FieldData> constructorParameters = new LinkedHashMap<>();

			List<String> methodParameters = Instructions.getMethodParams(descriptor);

			Map<Integer, List<Instruction>> instructionsPerOpcode = Instructions.getFilteredInstructions(instructions,
					Arrays.asList(Opcode.PUTFIELD, Opcode.INVOKEVIRTUAL));

			ReverseInstructionFilter instructionFilter = new ReverseInstructionFilter(classFile, instructions);

			constructorParameters
					.putAll(processSuperConstructorCall(instructions, instructionFilter, methodParameters));

			List<Instruction> putFieldInstructions = instructionsPerOpcode.get(Opcode.PUTFIELD);

			if (putFieldInstructions != null && !putFieldInstructions.isEmpty())
				constructorParameters
						.putAll(processPutFieldInstructions(putFieldInstructions, instructionFilter, methodParameters));

			List<Instruction> invokeVirtualInstructions = instructionsPerOpcode.get(Opcode.INVOKEVIRTUAL);

			if (invokeVirtualInstructions != null && !invokeVirtualInstructions.isEmpty())
				constructorParameters.putAll(
						processInvokeInstructions(invokeVirtualInstructions, instructionFilter, methodParameters));

			ConstructorData constructorData = new ConstructorData(constructorParameters);
			classData.setConstructor(constructorData);

			analysisResults.put(method, constructorData);
		}

		return true;
	}

	@Override
	public void reset() {
		analysisResults.clear();
	}

	private Map<Integer, FieldData> processSuperConstructorCall(List<Instruction> instructions,
			ReverseInstructionFilter instructionFilter, List<String> methodParameters) {

		Map<Integer, FieldData> constructorParameters = new LinkedHashMap<>();

		Instruction superConstructorInst = instructions.stream()
				.filter(inst -> Opcode.INVOKESPECIAL == inst.getOpcode() && MethodInfo.nameInit.equals(inst.getName()))
				.findAny().orElseThrow(
						() -> new IllegalArgumentException("invalid Instructionset superconstructor call must exist"));

		if (classData.getName().equals(superConstructorInst.getClassRef())
				&& Descriptor.numOfParameters(superConstructorInst.getType()) > 0) {
			MethodInfo superConstructorMethod = classFile.getMethods().stream()
					.filter(meth -> MethodInfo.nameInit.equals(meth.getName())
							&& meth.getDescriptor().equals(superConstructorInst.getType()))
					.findAny().orElseThrow(() -> new IllegalArgumentException(
							String.format("constructor with signature %s must exist", superConstructorInst.getType())));

			try {
				hasAnalysisMatched(superConstructorMethod, Instructions.getAllInstructions(superConstructorMethod));
			} catch (BadBytecode e) {
				LOGGER.error("analysis of constructor failed", e);
			}

			List<Instruction> calledLoadInstructions = instructionFilter
					.filterForCalledLoadInstructions(superConstructorInst);
			removeAload0InstructionFromLoadInstructions(calledLoadInstructions);

			Collections.reverse(calledLoadInstructions);

			Map<Integer, FieldData> superConstructorFields = analysisResults.get(superConstructorMethod)
					.getConstructorElements();

			for (int i = 0; i < calledLoadInstructions.size(); i++) {
				Instruction loadInstruction = calledLoadInstructions.get(i);

				Optional<Integer> methodParameterIndex = isLoadInstructionAMethodParameter(loadInstruction,
						methodParameters);

				if (methodParameterIndex.isPresent())
					constructorParameters.put(methodParameterIndex.get(), superConstructorFields.get(i));
			}

		}

		return constructorParameters;
	}

	private Map<Integer, FieldData> processPutFieldInstructions(List<Instruction> putFieldInstructions,
			ReverseInstructionFilter instructionFilter, List<String> methodParameters) {
		Map<Integer, FieldData> constructorParameters = new LinkedHashMap<>();

		for (Instruction instruction : putFieldInstructions) {

			// inner classes got an instance of the outerclass as an constructor argument,
			// we ignore this because in the java code it doesn't exist
			if ((classData.isInnerClass()
					&& !classData.getOuterClass().equals(Descriptor.toClassName(instruction.getType())))
					|| !classData.isInnerClass()) {

				List<Instruction> calledLoadInstructions = instructionFilter
						.filterForCalledLoadInstructions(instruction);
				removeAload0InstructionFromLoadInstructions(calledLoadInstructions);

				for (Instruction loadInstruction : calledLoadInstructions) {
					Optional<Integer> methodParameterIndex = isLoadInstructionAMethodParameter(loadInstruction,
							methodParameters);

					if (methodParameterIndex.isPresent())
						constructorParameters.put(methodParameterIndex.get(), getField(instruction));

				}
			}

		}

		return constructorParameters;
	}

	private Map<Integer, FieldData> processInvokeInstructions(List<Instruction> invokeInstructions,
			ReverseInstructionFilter instructionFilter, List<String> methodParameters) {
		Map<Integer, FieldData> constructorParameters = new LinkedHashMap<>();

		for (Instruction invokeInstruction : invokeInstructions) {
			Entry<MethodData, FieldData> methodEntry = getMethod(invokeInstruction);

			if (methodEntry != null) {

				MethodType methodType = methodEntry.getKey().getMethodType();
				if (MethodType.REFERENCE_VALUE_SETTER != methodType && MethodType.COLLECTION_SETTER != methodType)
					continue;

				List<Instruction> calledLoadInstructions = instructionFilter
						.filterForCalledLoadInstructions(invokeInstruction);
				removeAload0InstructionFromLoadInstructions(calledLoadInstructions);

				for (Instruction loadInstructions : calledLoadInstructions) {

					Optional<Integer> methodParameterIndex = isLoadInstructionAMethodParameter(loadInstructions,
							methodParameters);

					if (methodParameterIndex.isPresent())
						constructorParameters.put(methodParameterIndex.get(), methodEntry.getValue());
				}

			}
		}

		return constructorParameters;
	}

	private Optional<Integer> isLoadInstructionAMethodParameter(Instruction loadInstruction,
			List<String> methodParameters) {
		switch (loadInstruction.getOpcode()) {
		case Opcode.ALOAD_1:
			return checkType(0, methodParameters, REFERENCE_CHECK);
		case Opcode.ALOAD_2:
			return checkType(1, methodParameters, REFERENCE_CHECK);
		case Opcode.ALOAD_3:
			return checkType(2, methodParameters, REFERENCE_CHECK);
		case Opcode.ALOAD:
			return checkType(loadInstruction.getLocalVariableIndex() - 1, methodParameters, REFERENCE_CHECK);

		case Opcode.ILOAD_1:
			return checkType(0, methodParameters, ILOAD_CHECK);
		case Opcode.ILOAD_2:
			return checkType(1, methodParameters, ILOAD_CHECK);
		case Opcode.ILOAD_3:
			return checkType(2, methodParameters, ILOAD_CHECK);
		case Opcode.ILOAD:
			return checkType(loadInstruction.getLocalVariableIndex() - 1, methodParameters, ILOAD_CHECK);

		case Opcode.FLOAD_1:
			return checkType(0, methodParameters, FLOAD_CHECK);
		case Opcode.FLOAD_2:
			return checkType(1, methodParameters, FLOAD_CHECK);
		case Opcode.FLOAD_3:
			return checkType(2, methodParameters, FLOAD_CHECK);
		case Opcode.FLOAD:
			return checkType(loadInstruction.getLocalVariableIndex() - 1, methodParameters, FLOAD_CHECK);

		case Opcode.DLOAD_1:
			return checkType(0, methodParameters, DLOAD_CHECK);
		case Opcode.DLOAD_2:
			return checkType(1, methodParameters, DLOAD_CHECK);
		case Opcode.DLOAD_3:
			return checkType(2, methodParameters, DLOAD_CHECK);
		case Opcode.DLOAD:
			return checkType(loadInstruction.getLocalVariableIndex() - 1, methodParameters, DLOAD_CHECK);

		case Opcode.LLOAD_1:
			return checkType(0, methodParameters, LLOAD_CHECK);
		case Opcode.LLOAD_2:
			return checkType(1, methodParameters, LLOAD_CHECK);
		case Opcode.LLOAD_3:
			return checkType(2, methodParameters, LLOAD_CHECK);
		case Opcode.LLOAD:
			return checkType(loadInstruction.getLocalVariableIndex() - 1, methodParameters, LLOAD_CHECK);
		default:
			throw new IllegalArgumentException(
					String.format("invalid load opcode: %s", Mnemonic.OPCODE[loadInstruction.getOpcode()]));
		}
	}

	private Optional<Integer> checkType(int methodParameterIndex, List<String> methodParameters,
			Function<String, Boolean> typeCheck) {
		String type = methodParameters.get(methodParameterIndex);

		return typeCheck.apply(type) ? Optional.of(methodParameterIndex) : Optional.empty();
	}

}
