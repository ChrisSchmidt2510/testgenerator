package de.nvg.agent.classdata.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.MethodType;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.constants.JavaTypes;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Descriptor;

public class MethodAnalyser {

	private static final Logger LOGGER = LogManager.getLogger(MethodAnalyser.class);

	private final NormalSetterAnalyser setterAnalyser = new NormalSetterAnalyser();
	private final NormalGetterAnalyser getterAnalyser;
	private final CollectionSetterAnalyser collectionAddAnalyser;
	private final ImmutableCollectionGetterAnalyser immutableCollectionGetter = new ImmutableCollectionGetterAnalyser();

	private final List<FieldData> fields;

	public MethodAnalyser(String className, List<FieldData> fields) {
		collectionAddAnalyser = new CollectionSetterAnalyser(fields);
		getterAnalyser = new NormalGetterAnalyser(className);
		this.fields = fields;
	}

	public MethodData analyse(String name, String descriptor, int modifier, List<Instruction> instructions,
			Wrapper<FieldData> fieldWrapper) {

		LOGGER.info("Starting Analysis of Method: " + name + descriptor);

		MethodData methodData = null;

		if (setterAnalyser.analyse(descriptor, instructions, fieldWrapper)) {
			methodData = new MethodData(name, descriptor, MethodType.REFERENCE_VALUE_SETTER, 0,
					Modifier.isStatic(modifier));
		} else if (collectionAddAnalyser.analyse(descriptor, instructions, fieldWrapper)) {
			methodData = new MethodData(name, descriptor, MethodType.COLLECTION_SETTER, 0, //
					Modifier.isStatic(modifier));
		} else if (immutableCollectionGetter.analyse(descriptor, instructions, fieldWrapper)) {
			methodData = new MethodData(name, descriptor, MethodType.IMMUTABLE_GETTER, -1, //
					Modifier.isStatic(modifier));
		} else if (getterAnalyser.analyse(descriptor, instructions, fieldWrapper)) {

			FieldData foundedField = fieldWrapper.getValue();

			FieldData field = AnalysisHelper.getField(fields, foundedField.getName(), foundedField.getDataType());

			methodData = new MethodData(name, descriptor,
					field.isMutable() || (!field.isMutable() && JavaTypes.COLLECTION_LIST.contains(field.getDataType()))
							? MethodType.REFERENCE_VALUE_GETTER
							: MethodType.IMMUTABLE_GETTER, //
					-1, Modifier.isStatic(modifier));
		}

		LOGGER.info("Result of Analysis: " + methodData);

		return methodData;
	}

	public boolean isDefaultConstructor(String descriptor) {
		List<String> params = Instructions.getMethodParams(descriptor);
		return params.isEmpty();
	}

	public Map<Integer, FieldData> analyseConstructor(String methodDescriptor, //
			List<Instruction> putFieldInstructions, List<Instruction> allInstructions) throws BadBytecode {
		Map<Integer, FieldData> initialzedFields = new HashMap<>();

		if (putFieldInstructions != null) {
			List<String> params = Instructions.getMethodParams(methodDescriptor);

			for (Instruction instruction : putFieldInstructions) {

				Wrapper<Integer> constructorParameterIndex = new Wrapper<>();

				if (AnalysisHelper.isDescriptorEqual(allInstructions, allInstructions.indexOf(instruction), //
						instruction.getType(), params, constructorParameterIndex)) {

					FieldData field = new FieldData.Builder().withName(instruction.getName())
							.withDataType(Descriptor.toClassName(instruction.getType())).build();

					initialzedFields.put(constructorParameterIndex.getValue(), field);
				}
			}
		}

		return Collections.unmodifiableMap(initialzedFields);

	}

}
