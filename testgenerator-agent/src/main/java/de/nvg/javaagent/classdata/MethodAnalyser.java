package de.nvg.javaagent.classdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.model.MethodData;
import de.nvg.javaagent.classdata.model.MethodType;
import de.nvg.testgenerator.MapBuilder;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.Primitives;
import javassist.Modifier;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;

public class MethodAnalyser {

	private static final String ADD = "add";

	private static final String OBJECT = "Ljava/lang/Object;";
	private static final String COLLECTION = "Ljava/util/Collection;";
	private static final String LIST = "Ljava/util/List;";
	private static final String SET = "Ljava/util/Set;";
	private static final String QUEUE = "Ljava/util/Queue;";
	private static final String MAP = "Ljava/util/Map;";

	private static final String COLLECTIONS = "java.util.Collections";

	private static final List<String> COLLECTION_TYPES = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, QUEUE, MAP));

	private static final Map<String, Integer> PRIMITIVE_RETURN_OPCODES = //
			MapBuilder.<String, Integer>hashMapBuilder()//
					.add("I", Opcode.IRETURN).add("F", Opcode.FRETURN)//
					.add("D", Opcode.DRETURN).add("L", Opcode.LRETURN)//
					.add("[", Opcode.ARETURN).toUnmodifiableMap();

	private final List<FieldData> fields;

	public MethodAnalyser(List<FieldData> fields) {
		this.fields = fields;
	}

	public MethodData analyse(String name, String descriptor, int modifier, List<Instruction> instructions,
			Wrapper<FieldData> field) {

		MethodData methodData = null;

		if (isNormalSetter(descriptor, instructions, field)) {
			methodData = new MethodData(name, descriptor, MethodType.REFERENCE_VALUE_SETTER, 0,
					Modifier.isStatic(modifier));
		} else if (isAddMethodCollection(descriptor, instructions, field)) {
			methodData = new MethodData(name, descriptor, MethodType.COLLECTION_SETTER, 0, Modifier.isStatic(modifier));
		} else if (isUnmodifiableCollectionGetter(instructions, field)) {
			methodData = new MethodData(name, descriptor, MethodType.IMMUTABLE_GETTER, -1, Modifier.isStatic(modifier));
		} else if (isNormalGetter(instructions, field)) {

			methodData = new MethodData(name, descriptor,
					field.getValue().isMutable() ? MethodType.REFERENCE_VALUE_GETTER : MethodType.IMMUTABLE_GETTER, //
					-1, Modifier.isStatic(modifier));
		}

		return methodData;
	}

	public boolean isDefaultConstructor(String descriptor) {
		List<String> params = getMethodParams(descriptor);
		return params.isEmpty();
	}

	public Map<Integer, FieldData> analyseConstructor(String methodDescriptor, //
			List<Instruction> putFieldInstructions, List<Instruction> allInstructions) throws BadBytecode {
		Map<Integer, FieldData> initialzedFields = new HashMap<>();

		if (putFieldInstructions != null) {
			List<String> params = getMethodParams(methodDescriptor);

			for (Instruction instruction : putFieldInstructions) {

				Wrapper<Integer> constructorParameterIndex = new Wrapper<>();

				if (isDescriptorEqual(allInstructions, allInstructions.indexOf(instruction), //
						instruction.getType(), params, constructorParameterIndex)) {

					FieldData field = getField(instruction.getName(), Descriptor.toClassName(instruction.getType()));
					initialzedFields.put(constructorParameterIndex.getValue(), field);
				}

			}
		}

		return Collections.unmodifiableMap(initialzedFields);

	}

	private boolean isNormalSetter(String methodDescriptor, List<Instruction> instructions,
			Wrapper<FieldData> fieldInstance) {

		for (int index = 0; index < instructions.size(); index++) {
			Instruction instruction = instructions.get(index);

			if (Opcode.PUTFIELD == instruction.getOpcode()) {

				List<String> parameters = getMethodParams(methodDescriptor);

				if (isDescriptorEqual(instructions, index, instruction.getType(), parameters, new Wrapper<>())
						&& instructions.size() < 25) {

					FieldData field = getField(instruction.getName(), Descriptor.toClassName(instruction.getType()));

					fieldInstance.setValue(field);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isAddMethodCollection(String methodDescriptor, List<Instruction> instructions,
			Wrapper<FieldData> fieldInstance) {

		for (int index = 0; index < instructions.size(); index++) {
			Instruction instruction = instructions.get(index);

			if (Opcode.GETFIELD == instruction.getOpcode() && COLLECTION_TYPES.contains(instruction.getType())) {

				FieldData field = getField(instruction.getName(), Descriptor.toClassName(instruction.getType()));

				List<String> params = getMethodParams(methodDescriptor);

				List<String> genericTypes = getGenericTypesFromSignature(field.getSignature());

				if (params.size() == 1 && genericTypes.size() == 1 && params.get(0).equals(genericTypes.get(0))
						&& instructions.size() < 25) {

					Instruction bytecodeInvokeInterface = instructions.get(index + 2);

					List<String> interfaceParams = getMethodParams(bytecodeInvokeInterface.getType());

					if (Opcode.ALOAD_1 == instructions.get(index + 1).getOpcode()
							&& Opcode.INVOKEINTERFACE == bytecodeInvokeInterface.getOpcode()
							&& ADD.equals(bytecodeInvokeInterface.getName()) && interfaceParams.size() == 1
							&& OBJECT.equals(interfaceParams.get(0))) {

						fieldInstance.setValue(field);
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean isUnmodifiableCollectionGetter(List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode()) {

				Instruction invokeStatic = instructions.get(i + 1);

				if (COLLECTION_TYPES.contains(instruction.getType()) && Opcode.INVOKESTATIC == invokeStatic.getOpcode()
						&& COLLECTIONS.equals(invokeStatic.getClassRef())
						&& invokeStatic.getName().startsWith("unmodifiable") && instructions.size() >= i + 2
						&& Opcode.ARETURN == instructions.get(i + 2).getOpcode()) {

					FieldData field = getField(instruction.getName(), Descriptor.toClassName(instruction.getType()));
					fieldWrapper.setValue(field);

					return true;
				}
			}
		}

		return false;
	}

	private boolean isNormalGetter(List<Instruction> instructions, Wrapper<FieldData> fieldWrapper) {
		for (int i = 0; i < instructions.size(); i++) {
			Instruction instruction = instructions.get(i);

			if (Opcode.GETFIELD == instruction.getOpcode()) {

				Integer returnOpcode = PRIMITIVE_RETURN_OPCODES.get(instruction.getType());
				int opcode = instructions.get(i + 1).getOpcode();

				if ((returnOpcode != null && returnOpcode.equals(opcode)) || Opcode.ARETURN == opcode) {
					FieldData field = getField(instruction.getName(), Descriptor.toClassName(instruction.getType()));

					fieldWrapper.setValue(field);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isDescriptorEqual(List<Instruction> instructions, int fieldInstructionIndex,
			String fieldDescriptor, List<String> methodParameters, Wrapper<Integer> argumentIndex) {

		Instruction instruction = instructions.get(fieldInstructionIndex - 1);

		switch (instruction.getOpcode()) {
		case Opcode.ALOAD_1:
		case Opcode.ILOAD_1:
		case Opcode.DLOAD_1:
		case Opcode.FLOAD_1:
		case Opcode.LLOAD_1:
			argumentIndex.setValue(1);
			return fieldDescriptor.equals(methodParameters.get(0));
		case Opcode.ALOAD_2:
		case Opcode.ILOAD_2:
		case Opcode.DLOAD_2:
		case Opcode.FLOAD_2:
		case Opcode.LLOAD_2:
			argumentIndex.setValue(2);
			return fieldDescriptor.equals(methodParameters.get(1));
		case Opcode.ALOAD_3:
		case Opcode.ILOAD_3:
		case Opcode.DLOAD_3:
		case Opcode.FLOAD_3:
		case Opcode.LLOAD_3:
			argumentIndex.setValue(3);
			return fieldDescriptor.equals(methodParameters.get(2));
		case Opcode.ALOAD:
		case Opcode.ILOAD:
		case Opcode.DLOAD:
		case Opcode.FLOAD:
		case Opcode.LLOAD:
			argumentIndex.setValue(instruction.getLocalVariableIndex());
			return fieldDescriptor.equals(methodParameters.get(instruction.getLocalVariableIndex() - 1));
		}
		return false;
	}

	private FieldData getField(String name, String type) {
		return fields.stream().filter(field -> field.getName().equals(name) && field.getDataType().equals(type))
				.findAny()
				// can't be null cause the classfile would be otherwise incorrect
				.orElse(null);
	}

	private static List<String> getMethodParams(String descriptor) {
		List<String> parameters = new ArrayList<>();

		String methodParameters = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.indexOf(")"));

		while (methodParameters.length() > 0) {
			if (Primitives.isPrimitiveDataType(methodParameters)) {
				parameters.add(String.valueOf(methodParameters.charAt(0)));
				methodParameters = methodParameters.substring(1);
			} else {
				int index = methodParameters.indexOf(";") + 1;

				parameters.add(methodParameters.substring(0, index));
				methodParameters = methodParameters.substring(index);
			}
		}

		return Collections.unmodifiableList(parameters);
	}

	private static List<String> getGenericTypesFromSignature(String signature) {
		String genericType = signature.substring(signature.indexOf("<") + 1, signature.indexOf(">"));

		StringTokenizer tokenizer = new StringTokenizer(genericType, ";");

		List<String> genericTypes = new ArrayList<>(tokenizer.countTokens());

		while (tokenizer.hasMoreTokens()) {
			genericTypes.add(tokenizer.nextToken() + ";");
		}

		return Collections.unmodifiableList(genericTypes);
	}

}
