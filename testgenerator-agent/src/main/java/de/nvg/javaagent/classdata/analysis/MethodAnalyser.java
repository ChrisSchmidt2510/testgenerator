package de.nvg.javaagent.classdata.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.model.MethodData;
import de.nvg.javaagent.classdata.model.MethodType;
import de.nvg.testgenerator.Wrapper;
import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Descriptor;

public class MethodAnalyser {

	private final NormalSetterAnalyser setterAnalyser = new NormalSetterAnalyser();
	private final NormalGetterAnalyser getterAnalyser = new NormalGetterAnalyser();
	private final CollectionSetterAnalyser collectionAddAnalyser;
	private final ImmutableCollectionGetter immutableCollectionGetter = new ImmutableCollectionGetter();

	public MethodAnalyser(List<FieldData> fields) {
		collectionAddAnalyser = new CollectionSetterAnalyser(fields);
	}

	public MethodData analyse(String name, String descriptor, int modifier, List<Instruction> instructions,
			Wrapper<FieldData> fieldWrapper) {

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

			methodData = new MethodData(name, descriptor,
					fieldWrapper.getValue().isMutable() ? MethodType.REFERENCE_VALUE_GETTER
							: MethodType.IMMUTABLE_GETTER, //
					-1, Modifier.isStatic(modifier));
		}

		return methodData;
	}

	public boolean isDefaultConstructor(String descriptor) {
		List<String> params = AnalysisHelper.getMethodParams(descriptor);
		return params.isEmpty();
	}

	public Map<Integer, FieldData> analyseConstructor(String methodDescriptor, //
			List<Instruction> putFieldInstructions, List<Instruction> allInstructions) throws BadBytecode {
		Map<Integer, FieldData> initialzedFields = new HashMap<>();

		if (putFieldInstructions != null) {
			List<String> params = AnalysisHelper.getMethodParams(methodDescriptor);

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
