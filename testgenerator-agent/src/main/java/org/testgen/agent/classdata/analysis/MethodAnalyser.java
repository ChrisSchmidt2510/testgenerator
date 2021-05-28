package org.testgen.agent.classdata.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.core.Wrapper;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.Modifier;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class MethodAnalyser {

	private static final Logger LOGGER = LogManager.getLogger(MethodAnalyser.class);

	private final NormalSetterAnalyser setterAnalyser = new NormalSetterAnalyser();
	private final NormalGetterAnalyser getterAnalyser;
	private final CollectionSetterAnalyser collectionAddAnalyser;
	private final ImmutableCollectionGetterAnalyser immutableCollectionGetter = new ImmutableCollectionGetterAnalyser();

	private final ClassData classData;

	public MethodAnalyser(ClassData classData) {
		collectionAddAnalyser = new CollectionSetterAnalyser(classData.getFields());
		getterAnalyser = new NormalGetterAnalyser(classData.getName());
		this.classData = classData;
	}

	public MethodData analyse(String name, String descriptor, int modifier, List<Instruction> instructions,
			Wrapper<FieldData> fieldWrapper) {

		LOGGER.info("Starting Analysis of Method: " + name + descriptor);

		MethodData methodData = null;

		if (setterAnalyser.analyse(descriptor, instructions, fieldWrapper)) {
			methodData = new MethodData(name, descriptor, MethodType.REFERENCE_VALUE_SETTER,
					Modifier.isStatic(modifier));
		} else if (collectionAddAnalyser.analyse(descriptor, instructions, fieldWrapper)) {
			methodData = new MethodData(name, descriptor, MethodType.COLLECTION_SETTER, //
					Modifier.isStatic(modifier));
		} else if (immutableCollectionGetter.analyse(descriptor, instructions, fieldWrapper)) {
			methodData = new MethodData(name, descriptor, MethodType.IMMUTABLE_GETTER, //
					Modifier.isStatic(modifier));
		} else if (getterAnalyser.analyse(descriptor, instructions, fieldWrapper)) {

			FieldData foundedField = fieldWrapper.getValue();

			FieldData field = AnalysisHelper.getField(classData.getFields(), foundedField.getName(),
					foundedField.getDataType());

			methodData = new MethodData(name, descriptor,
					field.isMutable() || (!field.isMutable() && JavaTypes.COLLECTION_LIST.contains(field.getDataType()))
							? MethodType.REFERENCE_VALUE_GETTER
							: MethodType.IMMUTABLE_GETTER, //
					Modifier.isStatic(modifier));
		}

		LOGGER.info("Result of Analysis: " + methodData);

		return methodData;
	}

	public Map<Integer, FieldData> analyseConstructor(String methodDescriptor, //
			List<Instruction> filteredInstructions, List<Instruction> allInstructions) {
		Map<Integer, FieldData> initialzedFields = new HashMap<>();

		LOGGER.info("Starting Analysing Constructor " + methodDescriptor);

		if (filteredInstructions != null) {
			List<String> params = Instructions.getMethodParams(methodDescriptor);

			for (Instruction instruction : filteredInstructions) {

				if (Opcode.PUTFIELD == instruction.getOpcode()) {
					Wrapper<Integer> constructorParameterIndex = new Wrapper<>();

					String type = Descriptor.toClassName(instruction.getType());
					// inner classes got an instance of the outerclass as an constructor argument,
					// we ignore this because in the java code it doesnt exist
					if ((classData.isInnerClass() && !classData.getOuterClass().equals(type)
							&& AnalysisHelper.isDescriptorEqual(allInstructions, allInstructions.indexOf(instruction), //
									instruction.getType(), params, constructorParameterIndex))
							|| (!classData.isInnerClass() && AnalysisHelper.isDescriptorEqual(allInstructions,
									allInstructions.indexOf(instruction), //
									instruction.getType(), params, constructorParameterIndex))) {

						FieldData field = new FieldData.Builder().withName(instruction.getName()).withDataType(type)
								.build();

						initialzedFields.put(constructorParameterIndex.getValue(), field);
					}
					// invokespecial oder invokevirtual
				} else {
					Entry<MethodData, FieldData> method = classData.getMethod(instruction.getName(),
							instruction.getType());

					if (method != null) {

						MethodType type = method.getKey().getMethodType();
						if (MethodType.REFERENCE_VALUE_SETTER == type || MethodType.COLLECTION_SETTER == type) {

							Optional<Integer> argumentIndex = AnalysisHelper.getArgumentIndex(instruction,
									allInstructions);
							if (argumentIndex.isPresent()) {
								initialzedFields.put(argumentIndex.get(), method.getValue());
							}
						}
					}
				}
			}
		}

		return Collections.unmodifiableMap(initialzedFields);

	}

}
