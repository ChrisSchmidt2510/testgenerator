package de.nvg.javaagent.classdata.modification.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.Instructions;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.modification.helper.CodeArrayModificator;
import de.nvg.testgenerator.MapBuilder;
import de.nvg.testgenerator.classdata.constants.JVMTypes;
import de.nvg.testgenerator.classdata.constants.Primitives;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
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

	private static final String REFERENCE_PROXY = "Lde/nvg/proxy/impl/ReferenceProxy;";
	private static final String REFERENCE_PROXY_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;)V";
	private static final String REFERENCE_PROXY_DEFAULT_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V";

	private static final String BOOLEAN_PROXY = "Lde/nvg/proxy/impl/BooleanProxy;";
	private static final String BOOLEAN_PROXY_CONSTRUCTOR = "(ZLjava/lang/Object;Ljava/lang/String;)V";

	private static final String DOUBLE_PROXY = "Lde/nvg/proxy/impl/DoubleProxy;";
	private static final String DOUBLE_PROXY_CONSTRUCTOR = "(DLjava/lang/Object;Ljava/lang/String;)V";

	private static final String FLOAT_PROXY = "Lde/nvg/proxy/impl/FloatProxy;";
	private static final String FLOAT_PROXY_CONSTRUCTOR = "(FLjava/lang/Object;Ljava/lang/String;)V";

	private static final String INTEGER_PROXY = "Lde/nvg/proxy/impl/IntegerProxy;";
	private static final String INTEGER_PROXY_CONSTRUCTOR = "(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V";
	private static final String INTEGER_PROXY_DEFAULT_CONSTRUCTOR = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V";

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

	private static final Map<String, String> PROXY_CONSTRUCTOR_WITH_INITALIZATION = //
			MapBuilder.<String, String>hashMapBuilder()//
					.add(REFERENCE_PROXY, REFERENCE_PROXY_CONSTRUCTOR)//
					.add(BOOLEAN_PROXY, BOOLEAN_PROXY_CONSTRUCTOR)//
					.add(INTEGER_PROXY, INTEGER_PROXY_CONSTRUCTOR)//
					.add(FLOAT_PROXY, FLOAT_PROXY_CONSTRUCTOR)//
					.add(DOUBLE_PROXY, DOUBLE_PROXY_CONSTRUCTOR)//
					.add(LONG_PROXY, LONG_PROXY_CONSTRUCTOR).toUnmodifiableMap();

	private static final Map<String, String> PROXY_SET_VALUE_DESCRIPTOR = //
			MapBuilder.<String, String>hashMapBuilder()//
					.add(REFERENCE_PROXY, JVMTypes.OBJECT) //
					.add(INTEGER_PROXY, Primitives.JVM_INT)//
					.add(BOOLEAN_PROXY, Primitives.JVM_BOOLEAN)//
					.add(FLOAT_PROXY, Primitives.JVM_FLOAT)//
					.add(DOUBLE_PROXY, Primitives.JVM_DOUBLE)//
					.add(LONG_PROXY, Primitives.JVM_LONG).toUnmodifiableMap();

	private final List<FieldData> fields;
	private final ConstPool constantPool;
	private final CtClass loadingClass;

	public FieldTypeChanger(List<FieldData> fields, ConstPool constantPool, CtClass loadingClass) {
		this.fields = fields;
		this.constantPool = constantPool;
		this.loadingClass = loadingClass;
	}

	public void changeFieldInitialization(List<Instruction> instructions,
			Map<Integer, Instruction> aload0PutFieldInstructionPairs, CodeAttribute codeAttribute) throws BadBytecode {

		int codeArrayIndexModificator = 0;

		int codeArrayLastPutFieldInstruction = 0;

		List<FieldData> initalizedFields = new ArrayList<>();

		CodeIterator iterator = codeAttribute.iterator();

		LOGGER.debug("before manipulation: ", stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		for (Entry<Integer, Instruction> entry : aload0PutFieldInstructionPairs.entrySet()) {
			int lastAloadInstructionIndex = entry.getKey() + codeArrayIndexModificator;
			Instruction instruction = entry.getValue();

			codeArrayLastPutFieldInstruction = instruction.getCodeArrayIndex() + 3;

			int putFieldIndex = instructions.indexOf(instruction);
			Instruction instructionBeforePutField = instructions.get(putFieldIndex - 1);

			String proxy = getProxy(instruction.getType());

			Bytecode beforeValueCreation = new Bytecode(constantPool);
			beforeValueCreation.addNew(proxy);
			beforeValueCreation.addOpcode(Opcode.DUP);

			iterator.insertEx(lastAloadInstructionIndex + 1, beforeValueCreation.get());

			// new =3 + dup=1 =4
			codeArrayIndexModificator = codeArrayIndexModificator + beforeValueCreation.getSize();

			Bytecode afterValueCreation = new Bytecode(constantPool);
			afterValueCreation.addAload(0);
			afterValueCreation.addLdc(instruction.getName());

			if (INTEGER_PROXY.equals(proxy)) {
				afterValueCreation.addLdc(Descriptor.toClassName(instruction.getType()));
			}

			if (Opcode.ACONST_NULL == instructionBeforePutField.getOpcode()) {
				afterValueCreation.addLdc(Descriptor.toClassName(instruction.getType()));

				afterValueCreation.addInvokespecial(proxy, MethodInfo.nameInit, REFERENCE_PROXY_DEFAULT_CONSTRUCTOR);
				afterValueCreation.addPutfield(loadingClass, instruction.getName(), proxy);

				// aconst_null (1) + putField(3)
				iterator.insertGapAt(instructionBeforePutField.getCodeArrayIndex() + codeArrayIndexModificator,
						afterValueCreation.getSize() - 4, true);

				iterator.write(afterValueCreation.get(),
						instructionBeforePutField.getCodeArrayIndex() + codeArrayIndexModificator);

				codeArrayIndexModificator += afterValueCreation.getSize() - 4;
			} else {

				afterValueCreation.addInvokespecial(proxy, MethodInfo.nameInit,
						PROXY_CONSTRUCTOR_WITH_INITALIZATION.get(proxy));
				afterValueCreation.addPutfield(loadingClass, instruction.getName(), proxy);

				iterator.insertGapAt(instruction.getCodeArrayIndex() + codeArrayIndexModificator,
						afterValueCreation.getSize() - 3, true);

				iterator.write(afterValueCreation.get(), instruction.getCodeArrayIndex() + codeArrayIndexModificator);

				// for the new invokespecial + aload0 instruction
				codeArrayIndexModificator = codeArrayIndexModificator + afterValueCreation.getSize() - 3;
			}

			FieldData field = new FieldData.Builder().withDataType(Descriptor.toClassName(instruction.getType()))
					.withName(instruction.getName()).build();
			initalizedFields.add(field);

			LOGGER.trace("Added Field(\"" + field.getName() + "\", \"" + field.getDataType() + "\") Manipulation: ",
					stream -> Instructions.showCodeArray(stream, iterator, constantPool));
		}

		for (

		FieldData field :

		getUnitializedFields(initalizedFields)) {

			String dataType = Descriptor.of(field.getDataType());

			String proxy = getProxy(dataType);

			Bytecode bytecode = new Bytecode(constantPool);
			bytecode.addAload(0);
			bytecode.addNew(proxy);
			bytecode.addOpcode(Opcode.DUP);
			bytecode.addAload(0);
			bytecode.addLdc(field.getName());

			if (REFERENCE_PROXY.equals(proxy) || INTEGER_PROXY.equals(proxy)) {
				bytecode.addLdc(Descriptor.toClassName(dataType));
			}

			bytecode.addInvokespecial(proxy, MethodInfo.nameInit, getInitDescriptor(proxy));
			bytecode.addPutfield(loadingClass, field.getName(), proxy);

			if (codeArrayLastPutFieldInstruction == 0) {
				codeArrayLastPutFieldInstruction = 4;
			}

			iterator.insertEx(codeArrayLastPutFieldInstruction + codeArrayIndexModificator, bytecode.get());

			codeArrayIndexModificator = codeArrayIndexModificator + bytecode.getSize();
		}

		LOGGER.debug("after manipulation: ", stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		codeAttribute.computeMaxStack();
	}

	public static void changeFieldDataTypeToProxy(ClassFile loadingClass, FieldInfo field)
			throws CannotCompileException, NotFoundException {

		loadingClass.getFields().remove(field);

		String proxy = getProxy(field.getDescriptor());

		FieldInfo proxyField = new FieldInfo(loadingClass.getConstPool(), field.getName(), proxy);
		proxyField.setAccessFlags(field.getAccessFlags());

		if (REFERENCE_PROXY.equals(proxy)) {
			SignatureAttribute signature = (SignatureAttribute) field.getAttribute(SignatureAttribute.tag);
			String dataType = signature != null ? signature.getSignature() : field.getDescriptor();

			SignatureAttribute proxySignature = new SignatureAttribute(loadingClass.getConstPool(), dataType);
			proxyField.addAttribute(proxySignature);
		}

		loadingClass.addField(proxyField);
	}

	public void addFieldCalledField() throws CannotCompileException {
		loadingClass
				.addField(CtField.make("private java.util.Set calledFields = new java.util.HashSet();", loadingClass));
	}

	public void overrideFieldAccess(Map<Integer, List<Instruction>> filteredInstructions,
			List<Instruction> allInstructions, CodeAttribute codeAttribute) throws BadBytecode {

		CodeIterator iterator = codeAttribute.iterator();

		CodeArrayModificator codeArrayModificator = new CodeArrayModificator();

		List<Instruction> putFieldInstructions = filteredInstructions.get(Opcode.PUTFIELD);

		LOGGER.debug("Method before manipulation: ",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		if (putFieldInstructions != null) {
			for (Instruction instruction : putFieldInstructions) {

				Instruction loadInstruction = Instructions.filterOpcode(allInstructions,
						allInstructions.indexOf(instruction), Opcode.ALOAD_0);

				int codeArrayIndex = loadInstruction.getCodeArrayIndex()
						+ codeArrayModificator.getModificator(loadInstruction.getCodeArrayIndex());

				String dataType = instruction.getType();
				String proxy = getProxy(dataType);
				Bytecode beforeLoad = new Bytecode(constantPool);
				beforeLoad.addGetfield(loadingClass, instruction.getName(), proxy);

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

		List<Instruction> getFieldInstructions = filteredInstructions.get(Opcode.GETFIELD);
		if (getFieldInstructions != null) {

			for (Instruction instruction : getFieldInstructions) {

				String dataType = instruction.getType();
				String proxy = getProxy(dataType);

				int codeArrayIndex = instruction.getCodeArrayIndex()
						+ codeArrayModificator.getModificator(instruction.getCodeArrayIndex());

				Bytecode bytecode = new Bytecode(constantPool);
				bytecode.addGetfield(instruction.getClassRef(), instruction.getName(), proxy);
				bytecode.addInvokevirtual(proxy, getValueMethodName(dataType), getGetValueDescriptor(dataType));

				if (REFERENCE_PROXY.equals(proxy)) {
					bytecode.addCheckcast(dataType);
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

		LOGGER.debug("Method after manipulation: ",
				stream -> Instructions.showCodeArray(stream, iterator, constantPool));

		codeAttribute.computeMaxStack();
	}

	private static String getProxy(String dataType) {
		if (PRIMITIVE_PROXIES.containsKey(dataType)) {
			return PRIMITIVE_PROXIES.get(dataType);
		}
		return REFERENCE_PROXY;
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
		if (INTEGER_PROXY.equals(proxy)) {
			return INTEGER_PROXY_DEFAULT_CONSTRUCTOR;
		} else if (REFERENCE_PROXY.equals(proxy)) {
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

		for (FieldData fieldData : fields) {
			if (!initalizedFields.contains(fieldData)) {
				unitalizedFields.add(fieldData);
			}
		}

		return unitalizedFields;
	}

}
