package de.nvg.agent.classdata.modification.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.InstructionFilter;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.modification.BytecodeUtils;
import de.nvg.agent.classdata.modification.helper.CodeArrayModificator;
import de.nvg.testgenerator.MapBuilder;
import de.nvg.testgenerator.TestgeneratorConstants;
import de.nvg.testgenerator.classdata.constants.JVMTypes;
import de.nvg.testgenerator.classdata.constants.JavaTypes;
import de.nvg.testgenerator.classdata.constants.Primitives;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;

public class FieldTypeChanger {
	private static final Logger LOGGER = LogManager.getLogger(FieldTypeChanger.class);

	private static final String REFERENCE_PROXY_CLASSNAME = "de/nvg/proxy/impl/ReferenceProxy";
	private static final String REFERENCE_PROXY = "Lde/nvg/proxy/impl/ReferenceProxy;";
	private static final String REFERENCE_PROXY_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";
	private static final String REFERENCE_PROXY_DEFAULT_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";

	private static final String BOOLEAN_PROXY_CLASSNAME = "de/nvg/proxy/impl/BooleanProxy";
	private static final String BOOLEAN_PROXY = "Lde/nvg/proxy/impl/BooleanProxy;";
	private static final String BOOLEAN_PROXY_CONSTRUCTOR = "(ZLjava/lang/Object;Ljava/lang/String;)V";

	private static final String DOUBLE_PROXY_CLASSNAME = "de/nvg/proxy/impl/DoubleProxy";
	private static final String DOUBLE_PROXY = "Lde/nvg/proxy/impl/DoubleProxy;";
	private static final String DOUBLE_PROXY_CONSTRUCTOR = "(DLjava/lang/Object;Ljava/lang/String;)V";

	private static final String FLOAT_PROXY_CLASSNAME = "de/nvg/proxy/impl/FloatProxy";
	private static final String FLOAT_PROXY = "Lde/nvg/proxy/impl/FloatProxy;";
	private static final String FLOAT_PROXY_CONSTRUCTOR = "(FLjava/lang/Object;Ljava/lang/String;)V";

	private static final String INTEGER_PROXY_CLASSNAME = "de/nvg/proxy/impl/IntegerProxy";
	private static final String INTEGER_PROXY = "Lde/nvg/proxy/impl/IntegerProxy;";
	private static final String INTEGER_PROXY_CONSTRUCTOR = "(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";
	private static final String INTEGER_PROXY_DEFAULT_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Class;)V";

	private static final String LONG_PROXY_CLASSNAME = "de/nvg/proxy/impl/LongProxy";
	private static final String LONG_PROXY = "Lde/nvg/proxy/impl/LongProxy;";
	private static final String LONG_PROXY_CONSTRUCTOR = "(LLjava/lang/Object;Ljava/lang/String;)V";

	private static final String DEFAULT_PROXY_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;)V";

	private static final String SET_VALUE = "setValue";

	private static final String GET_VALUE = "getValue";
	private static final String GET_BYTE_VALUE = "getByteValue";
	private static final String GET_SHORT_VALUE = "getShortValue";
	private static final String GET_CHAR_VALUE = "getCharValue";

	private static final Map<String, String> PRIMITIVE_PROXIES = MapBuilder.<String, String>hashMapBuilder()
			.add(Primitives.JVM_BYTE, INTEGER_PROXY)//
			.add(Primitives.JVM_BOOLEAN, BOOLEAN_PROXY)//
			.add(Primitives.JVM_SHORT, INTEGER_PROXY)//
			.add(Primitives.JVM_CHAR, INTEGER_PROXY)//
			.add(Primitives.JVM_INT, INTEGER_PROXY)//
			.add(Primitives.JVM_FLOAT, FLOAT_PROXY)//
			.add(Primitives.JVM_DOUBLE, DOUBLE_PROXY)//
			.add(Primitives.JVM_LONG, LONG_PROXY).toUnmodifiableMap();

	private static final Map<String, String> PRIMITIVE_PROXIES_CLASSNAME = MapBuilder.<String, String>hashMapBuilder()//
			.add(Primitives.JVM_BYTE, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_BOOLEAN, BOOLEAN_PROXY_CLASSNAME)//
			.add(Primitives.JVM_SHORT, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_CHAR, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_INT, INTEGER_PROXY_CLASSNAME)//
			.add(Primitives.JVM_FLOAT, FLOAT_PROXY_CLASSNAME)//
			.add(Primitives.JVM_DOUBLE, DOUBLE_PROXY_CLASSNAME)//
			.add(Primitives.JVM_LONG, LONG_PROXY_CLASSNAME).toUnmodifiableMap();

	private static final Map<String, String> PROXY_CONSTRUCTOR_WITH_INITALIZATION = //
			MapBuilder.<String, String>hashMapBuilder()//
					.add(REFERENCE_PROXY_CLASSNAME, REFERENCE_PROXY_CONSTRUCTOR)//
					.add(BOOLEAN_PROXY_CLASSNAME, BOOLEAN_PROXY_CONSTRUCTOR)//
					.add(INTEGER_PROXY_CLASSNAME, INTEGER_PROXY_CONSTRUCTOR)//
					.add(FLOAT_PROXY_CLASSNAME, FLOAT_PROXY_CONSTRUCTOR)//
					.add(DOUBLE_PROXY_CLASSNAME, DOUBLE_PROXY_CONSTRUCTOR)//
					.add(LONG_PROXY_CLASSNAME, LONG_PROXY_CONSTRUCTOR).toUnmodifiableMap();

	private static final Map<String, String> PROXY_SET_VALUE_DESCRIPTOR = //
			MapBuilder.<String, String>hashMapBuilder()//
					.add(REFERENCE_PROXY_CLASSNAME, JVMTypes.OBJECT) //
					.add(INTEGER_PROXY_CLASSNAME, Primitives.JVM_INT)//
					.add(BOOLEAN_PROXY_CLASSNAME, Primitives.JVM_BOOLEAN)//
					.add(FLOAT_PROXY_CLASSNAME, Primitives.JVM_FLOAT)//
					.add(DOUBLE_PROXY_CLASSNAME, Primitives.JVM_DOUBLE)//
					.add(LONG_PROXY_CLASSNAME, Primitives.JVM_LONG).toUnmodifiableMap();

	private static final Map<String, String> PROXY_FIELD_MAPPER = MapBuilder.<String, String>hashMapBuilder()
			.add(REFERENCE_PROXY_CLASSNAME, REFERENCE_PROXY)//
			.add(INTEGER_PROXY_CLASSNAME, INTEGER_PROXY)//
			.add(BOOLEAN_PROXY_CLASSNAME, BOOLEAN_PROXY)//
			.add(FLOAT_PROXY_CLASSNAME, FLOAT_PROXY)//
			.add(DOUBLE_PROXY_CLASSNAME, DOUBLE_PROXY)//
			.add(LONG_PROXY_CLASSNAME, LONG_PROXY).toUnmodifiableMap();

	private final ClassData classData;
	private final ConstPool constantPool;
	private final CtClass loadingClass;

	public FieldTypeChanger(ClassData classData, ConstPool constantPool, CtClass loadingClass) {
		this.classData = classData;
		this.constantPool = constantPool;
		this.loadingClass = loadingClass;
	}

	public void changeFieldInitialization(List<Instruction> instructions, List<Instruction> putFieldInstructions,
			CodeAttribute codeAttribute) throws BadBytecode {
		CodeIterator iterator = codeAttribute.iterator();

		LOGGER.debug("before manipulation: ", stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		if (putFieldInstructions.isEmpty()) {
			// a Constructor has always a return-instruction
			Instruction returnInstruction = instructions.stream().filter(inst -> Opcode.RETURN == inst.getOpcode())
					.max((inst1, inst2) -> Integer.compare(inst1.getCodeArrayIndex(), inst2.getCodeArrayIndex()))
					.orElse(null);

			Instruction constructorCall = Instructions.getBeforeInstruction(instructions, returnInstruction);

			if (Opcode.INVOKESPECIAL == constructorCall.getOpcode()
					&& constructorCall.getClassRef().equals(classData.getName())
					&& MethodInfo.nameInit.equals(constructorCall.getName())) {
				return;
			}
		}

		CodeArrayModificator codeArrayModificator = new CodeArrayModificator();

		List<FieldData> initalizedFields = new ArrayList<>();

		InstructionFilter filter = new InstructionFilter(instructions);

		for (Instruction instruction : putFieldInstructions) {

			if (!classData.getField(instruction.getName(), Descriptor.toClassName(instruction.getType())).isPublic()
					&& !TestgeneratorConstants.isTestgeneratorField(instruction.getName())) {

				Instruction aloadInstruction = filter.filterForAloadInstruction(instruction);

				int lastAloadInstructionIndex = aloadInstruction.getCodeArrayIndex()
						+ codeArrayModificator.getModificator(instruction.getCodeArrayIndex());

				int putFieldIndex = instructions.indexOf(instruction);
				Instruction instructionBeforePutField = instructions.get(putFieldIndex - 1);

				String proxy = getProxyClassname(instruction.getType());

				Bytecode beforeValueCreation = new Bytecode(constantPool);
				beforeValueCreation.addNew(proxy);
				beforeValueCreation.addOpcode(Opcode.DUP);

				iterator.insertEx(lastAloadInstructionIndex + 1, beforeValueCreation.get());

				// new =3 + dup=1 =4
				codeArrayModificator.addCodeArrayModificator(aloadInstruction.getCodeArrayIndex(),
						beforeValueCreation.getSize());

				Bytecode afterValueCreation = new Bytecode(constantPool);
				afterValueCreation.addAload(0);
				afterValueCreation.addLdc(instruction.getName());

				if (INTEGER_PROXY_CLASSNAME.equals(proxy) || REFERENCE_PROXY_CLASSNAME.equals(proxy)) {
					BytecodeUtils.addClassInfoToBytecode(afterValueCreation, constantPool,
							Descriptor.toClassName(instruction.getType()));
				}

				if (Opcode.ACONST_NULL == instructionBeforePutField.getOpcode()) {

					afterValueCreation.addInvokespecial(proxy, MethodInfo.nameInit,
							REFERENCE_PROXY_DEFAULT_CONSTRUCTOR);
					afterValueCreation.addPutfield(loadingClass, instruction.getName(), PROXY_FIELD_MAPPER.get(proxy));

					// aconst_null (1) + putField(3)
					iterator.insertGapAt(
							instructionBeforePutField.getCodeArrayIndex() + codeArrayModificator
									.getModificator(instructionBeforePutField.getCodeArrayIndex()),
							afterValueCreation.getSize() - 4, true);

					iterator.write(afterValueCreation.get(), instructionBeforePutField.getCodeArrayIndex()
							+ codeArrayModificator.getModificator(instructionBeforePutField.getCodeArrayIndex()));

					codeArrayModificator.addCodeArrayModificator(instruction.getCodeArrayIndex(),
							afterValueCreation.getSize() - 4);
				} else {

					afterValueCreation.addInvokespecial(proxy, MethodInfo.nameInit,
							PROXY_CONSTRUCTOR_WITH_INITALIZATION.get(proxy));
					afterValueCreation.addPutfield(loadingClass, instruction.getName(), PROXY_FIELD_MAPPER.get(proxy));

					iterator.insertGapAt(
							instruction.getCodeArrayIndex()
									+ codeArrayModificator.getModificator(instruction.getCodeArrayIndex()),
							afterValueCreation.getSize() - 3, true);

					iterator.write(afterValueCreation.get(), instruction.getCodeArrayIndex()
							+ codeArrayModificator.getModificator(instruction.getCodeArrayIndex()));

					// for the new invokespecial + aload0 instruction
					codeArrayModificator.addCodeArrayModificator(instruction.getCodeArrayIndex(),
							afterValueCreation.getSize() - 3);
				}

				FieldData field = new FieldData.Builder().withDataType(Descriptor.toClassName(instruction.getType()))
						.withName(instruction.getName()).build();
				initalizedFields.add(field);

				LOGGER.trace("Added Field(\"" + field.getName() + "\", \"" + field.getDataType() + "\") Manipulation: ",
						stream -> Instructions.showCodeArray(stream, iterator, constantPool));
			}
		}

		List<Instruction> getFieldInstructions = Instructions
				.getFilteredInstructions(instructions, Arrays.asList(Opcode.GETFIELD)).get(Opcode.GETFIELD);

		if (getFieldInstructions != null) {
			overrideFieldAccessGetFieldInstructions(instructions, getFieldInstructions, iterator, //
					codeArrayModificator);
		}

		Instruction superConstructorCall = instructions.stream()
				.filter(inst -> isInstructionFromClassOrSuperClass(inst) && MethodInfo.nameInit.equals(inst.getName()))
				.findFirst().orElse(null);

		initalizeUnitalizedFields(initalizedFields, superConstructorCall.getCodeArrayIndex() + 3, iterator);

		LOGGER.debug("after manipulation: ", stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		codeAttribute.computeMaxStack();

	}

	private void initalizeUnitalizedFields(List<FieldData> initalizedFields, int codeArrayStartIndex, //
			CodeIterator iterator) throws BadBytecode {

		int codeArrayModificator = 0;

		for (FieldData field : getUnitializedFields(initalizedFields)) {

			String dataType = Descriptor.of(field.getDataType());

			String proxy = getProxyClassname(dataType);

			Bytecode bytecode = new Bytecode(constantPool);
			bytecode.addAload(0);
			bytecode.addNew(proxy);
			bytecode.addOpcode(Opcode.DUP);
			bytecode.addAload(0);
			bytecode.addLdc(field.getName());

			if (REFERENCE_PROXY_CLASSNAME.equals(proxy) || INTEGER_PROXY_CLASSNAME.equals(proxy)) {
				BytecodeUtils.addClassInfoToBytecode(bytecode, constantPool, Descriptor.toClassName(dataType));
			}

			bytecode.addInvokespecial(proxy, MethodInfo.nameInit, getInitDescriptor(proxy));
			bytecode.addPutfield(loadingClass, field.getName(), PROXY_FIELD_MAPPER.get(proxy));

			iterator.insertEx(codeArrayStartIndex + codeArrayModificator, bytecode.get());

			codeArrayModificator = codeArrayModificator + bytecode.getSize();
		}
	}

	public static void changeFieldDataTypeToProxy(ClassFile loadingClass, FieldInfo field)
			throws CannotCompileException {

		loadingClass.getFields().remove(field);

		String proxy = getProxy(field.getDescriptor());

		FieldInfo proxyField = new FieldInfo(loadingClass.getConstPool(), field.getName(), proxy);
		proxyField.setAccessFlags(field.getAccessFlags());

		if (REFERENCE_PROXY.equals(proxy)) {
			SignatureAttribute signature = (SignatureAttribute) field.getAttribute(SignatureAttribute.tag);

			String dataType = createSignatureForReferenceProxy(
					signature != null ? signature.getSignature() : field.getDescriptor());

			SignatureAttribute proxySignature = new SignatureAttribute(loadingClass.getConstPool(), dataType);
			proxyField.addAttribute(proxySignature);
		}

		loadingClass.addField(proxyField);
	}

	private static String createSignatureForReferenceProxy(String dataType) {
		return REFERENCE_PROXY.substring(0, REFERENCE_PROXY.length() - 1) + "<" + dataType + ">;";
	}

	public void addFieldCalledField() throws CannotCompileException {
		loadingClass.addField(CtField.make("private java.util.Set " + TestgeneratorConstants.FIELDNAME_CALLED_FIELDS
				+ "= new java.util.HashSet();", loadingClass));
	}

	public void overrideFieldAccess(Map<Integer, List<Instruction>> filteredInstructions,
			List<Instruction> instructions, CodeAttribute codeAttribute) throws BadBytecode {

		CodeIterator iterator = codeAttribute.iterator();

		CodeArrayModificator codeArrayModificator = new CodeArrayModificator();

		List<Instruction> putFieldInstructions = filteredInstructions.get(Opcode.PUTFIELD);

		LOGGER.debug("Method before manipulation: ",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		if (putFieldInstructions != null) {

			overrideFieldAccessPutFieldInstructions(instructions, putFieldInstructions, //
					iterator, codeArrayModificator);
		}

		List<Instruction> getFieldInstructions = filteredInstructions.get(Opcode.GETFIELD);
		if (getFieldInstructions != null) {
			overrideFieldAccessGetFieldInstructions(instructions, getFieldInstructions, //
					iterator, codeArrayModificator);
		}

		LOGGER.debug("Method after manipulation: ",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		codeAttribute.computeMaxStack();
	}

	private void overrideFieldAccessPutFieldInstructions(List<Instruction> instructions,
			List<Instruction> putFieldInstructions, CodeIterator iterator, //
			CodeArrayModificator codeArrayModificator) throws BadBytecode {

		InstructionFilter filter = new InstructionFilter(instructions);

		for (Instruction instruction : putFieldInstructions) {

			if (classData.getName().equals(instruction.getClassRef()) && !classData
					.getField(instruction.getName(), Descriptor.toClassName(instruction.getType())).isPublic()
					&& !TestgeneratorConstants.isTestgeneratorField(instruction.getName())) {

				Instruction loadInstruction = filter.filterForAloadInstruction(instruction);

				int codeArrayIndex = loadInstruction.getCodeArrayIndex()
						+ codeArrayModificator.getModificator(loadInstruction.getCodeArrayIndex());

				String dataType = instruction.getType();
				String proxy = getProxyClassname(dataType);
				Bytecode beforeLoad = new Bytecode(constantPool);
				beforeLoad.addGetfield(loadingClass, instruction.getName(), PROXY_FIELD_MAPPER.get(proxy));

				if (loadInstruction.getCodeArrayIndex() == 0) {
					iterator.insertAt(1, beforeLoad.get());
				} else {

					iterator.insertEx(codeArrayIndex + 1, beforeLoad.get());
				}

				Bytecode afterLoad = new Bytecode(constantPool);
				afterLoad.addInvokevirtual(proxy, SET_VALUE, getSetValueDescriptor(proxy));

				codeArrayModificator.addCodeArrayModificator(loadInstruction.getCodeArrayIndex(), 3);

				iterator.write(afterLoad.get(),
						instruction.getCodeArrayIndex() + codeArrayModificator.getModificator(codeArrayIndex));
			}
		}
	}

	private void overrideFieldAccessGetFieldInstructions(List<Instruction> instructions,
			List<Instruction> getFieldInstructions, CodeIterator iterator, //
			CodeArrayModificator codeArrayModificator) throws BadBytecode {
		for (Instruction instruction : getFieldInstructions) {

			if (classData.getName().equals(instruction.getClassRef()) && !classData
					.getField(instruction.getName(), Descriptor.toClassName(instruction.getType())).isPublic()
					&& !TestgeneratorConstants.isTestgeneratorField(instruction.getName())) {

				String dataType = instruction.getType();
				String proxy = getProxyClassname(dataType);

				Instruction thisInstruction = Instructions.getBeforeInstruction(instructions, instruction);
				if (Opcode.DUP == thisInstruction.getOpcode()) {

					Bytecode thisBytecode = new Bytecode(constantPool);
					thisBytecode.addOpcode(Opcode.ALOAD_0);

					int codeArrayIndex = thisInstruction.getCodeArrayIndex();
					iterator.write(thisBytecode.get(),
							codeArrayIndex + codeArrayModificator.getModificator(codeArrayIndex));
				}

				int codeArrayIndex = instruction.getCodeArrayIndex()
						+ codeArrayModificator.getModificator(instruction.getCodeArrayIndex());

				Bytecode bytecode = new Bytecode(constantPool);
				bytecode.addGetfield(instruction.getClassRef(), instruction.getName(), PROXY_FIELD_MAPPER.get(proxy));
				bytecode.addInvokevirtual(proxy, getValueMethodName(dataType), getGetValueDescriptor(dataType));

				if (REFERENCE_PROXY_CLASSNAME.equals(proxy)) {
					bytecode.addCheckcast(dataType.substring(1, dataType.length() - 1));
					iterator.insertGapAt(codeArrayIndex, 6, true);
					iterator.write(bytecode.get(), codeArrayIndex);
					codeArrayModificator.addCodeArrayModificator(instruction.getCodeArrayIndex(), 6);

				} else {
					iterator.insertGapAt(codeArrayIndex, 3, true);
					iterator.write(bytecode.get(), codeArrayIndex);
					codeArrayModificator.addCodeArrayModificator(instruction.getCodeArrayIndex(), 3);
				}
			}
		}
	}

	private static String getProxy(String dataType) {
		if (PRIMITIVE_PROXIES.containsKey(dataType)) {
			return PRIMITIVE_PROXIES.get(dataType);
		}
		return REFERENCE_PROXY;
	}

	private static String getProxyClassname(String dataType) {
		if (PRIMITIVE_PROXIES_CLASSNAME.containsKey(dataType)) {
			return PRIMITIVE_PROXIES_CLASSNAME.get(dataType);
		}
		return REFERENCE_PROXY_CLASSNAME;
	}

	private static String getValueMethodName(String dataType) {
		switch (dataType) {
		case Primitives.JVM_BYTE:
			return GET_BYTE_VALUE;
		case Primitives.JVM_CHAR:
			return GET_CHAR_VALUE;
		case Primitives.JVM_SHORT:
			return GET_SHORT_VALUE;
		default:
			return GET_VALUE;
		}
	}

	private static String getInitDescriptor(String proxy) {
		if (INTEGER_PROXY_CLASSNAME.equals(proxy)) {
			return INTEGER_PROXY_DEFAULT_CONSTRUCTOR;
		} else if (REFERENCE_PROXY_CLASSNAME.equals(proxy)) {
			return REFERENCE_PROXY_DEFAULT_CONSTRUCTOR;
		}
		return DEFAULT_PROXY_CONSTRUCTOR;
	}

	private static String getSetValueDescriptor(String proxy) {
		return "(" + PROXY_SET_VALUE_DESCRIPTOR.get(proxy) + ")V";
	}

	private static String getGetValueDescriptor(String dataType) {
		return "()" + (Primitives.isPrimitiveDataType(dataType) ? dataType : JVMTypes.OBJECT);
	}

	private List<FieldData> getUnitializedFields(List<FieldData> initalizedFields) {
		List<FieldData> unitalizedFields = new ArrayList<>();

		for (FieldData fieldData : classData.getFields()) {
			if (!initalizedFields.contains(fieldData) && !fieldData.isPublic()
					&& !TestgeneratorConstants.isTestgeneratorField(fieldData.getName()))
				unitalizedFields.add(fieldData);
		}

		return unitalizedFields;

	}

	private boolean isInstructionFromClassOrSuperClass(Instruction inst) {
		return classData.getName().equals(inst.getClassRef())
				|| (classData.getSuperClass() != null ? classData.getSuperClass().getName().equals(inst.getClassRef())
						: JavaTypes.OBJECT.equals(inst.getClassRef()));
	}

}
