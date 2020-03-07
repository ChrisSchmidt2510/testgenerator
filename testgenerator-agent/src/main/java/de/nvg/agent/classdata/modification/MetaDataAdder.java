package de.nvg.agent.classdata.modification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.MethodType;
import de.nvg.agent.classdata.modification.indy.InvocationType;
import de.nvg.agent.classdata.modification.indy.SupplierBootstrapMethodCreator;
import de.nvg.testgenerator.classdata.constants.JavaTypes;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class MetaDataAdder {
	private final ConstPool constantPool;
	private final CtClass loadingClass;
	private final ClassData classData;

	private static final String CLASS_DATA_CLASSNAME = "de/nvg/runtime/classdatamodel/ClassData";
	private static final String CLASS_DATA = "Lde/nvg/runtime/classdatamodel/ClassData;";
	private static final String CLASS_DATA_CONSTRUCTOR = "(Ljava/lang/String;Lde/nvg/runtime/classdatamodel/ConstructorData;)V";
	private static final String CLASS_DATA_CONSTRUCTOR_WITH_SUPERCLASS = "(Ljava/lang/String;Ljava/util/function/Supplier;Lde/nvg/runtime/classdatamodel/ConstructorData;)V";
	private static final String CLASS_DATA_METHOD_ADD_FIELD = "addField";
	private static final String CLASS_DATA_METHOD_ADD_FIELD_DESC = "(Lde/nvg/runtime/classdatamodel/FieldData;Lde/nvg/runtime/classdatamodel/SetterMethodData;)V";

	private static final String CONSTRUCTOR_DATA_CLASSNAME = "de/nvg/runtime/classdatamodel/ConstructorData";
	private static final String CONSTRUCTOR_DATA_CONSTRUCTOR = "(Z)V";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_ELEMENT = "addElement";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_ELEMENT_DESC = "(ILde/nvg/runtime/classdatamodel/FieldData;)V";

	private static final String FIELD_DATA_CLASSNAME = "de/nvg/runtime/classdatamodel/FieldData";
	private static final String FIELD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String;)V";

	private static final String SETTER_TYPE_CLASSNAME = "de/nvg/runtime/classdatamodel/SetterType";
	private static final String SETTER_TYPE = "Lde/nvg/runtime/classdatamodel/SetterType;";
	private static final String SETTER_TYPE_FIELDNAME_VALUE_SETTER = "VALUE_SETTER";
	private static final String SETTER_TYPE_FIELDNAME_VALUE_GETTER = "VALUE_GETTER";
	private static final String SETTER_TYPE_FIELDNAME_COLLECTION_SETTER = "COLLECTION_SETTER";

	private static final String SETTER_METHOD_DATA_CLASSNAME = "de/nvg/runtime/classdatamodel/SetterMethodData";
	private static final String SETTER_METHOD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String;Z)V";
	private static final String SETTER_METHOD_DATA_CONSTRUCTOR_WITH_SETTER_TYPE = "(Ljava/lang/String;Ljava/lang/String;ZLde/nvg/runtime/classdatamodel/SetterType;)V";

	private static final String INDY_SUPPLIER_TYPED_RETURN_TYPE = "()Lde/nvg/runtime/classdatamodel/ClassData;";

	private static final String INDY_METHOD_NAME = "testgenerator$0";

	private static final String SUPPLIER_METHOD_DESC = "()Ljava/util/function/Supplier;";
	private static final String SUPPLIER_METHOD_NAME = "get";

	private static final String METHODHANDLES_CLASSNAME = "de/nvg/testgenerator/MethodHandles";
	private static final String METHODHANDLES_METHOD_NAME = "getStaticFieldValue";
	private static final String METHODHANDLES_RETURN_TYPE = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;";

	private static final String FIELD_NAME = "classData";

	public MetaDataAdder(ConstPool constantPool, CtClass loadingClass, ClassData classData) {
		this.constantPool = constantPool;
		this.loadingClass = loadingClass;
		this.classData = classData;
	}

	public void add(CodeAttribute codeAttribute, List<Instruction> instructions)
			throws CannotCompileException, BadBytecode {
		ClassFile classFile = loadingClass.getClassFile();

		FieldInfo classDataField = new FieldInfo(constantPool, FIELD_NAME, CLASS_DATA);
		classDataField.setAccessFlags(AccessFlag.PRIVATE | AccessFlag.STATIC);
		classFile.addField(classDataField);

		Optional<Instruction> returnInstruction = instructions.stream()
				.filter(instruction -> Opcode.RETURN == instruction.getOpcode()).findAny();

		Bytecode code = new Bytecode(constantPool);

		int localVariableCounter = 0;

		Map<String, Integer> localVariableIndex = new HashMap<>();

		for (FieldData field : classData.getFields()) {
			code.addNew(FIELD_DATA_CLASSNAME);
			code.add(Opcode.DUP);
			code.addLdc(field.getName());
			code.addLdc(field.getDataType());
			code.addInvokespecial(FIELD_DATA_CLASSNAME, MethodInfo.nameInit, FIELD_DATA_CONSTRUCTOR);

			localVariableIndex.put(field.getName(), localVariableCounter);
			code.addAstore(localVariableCounter++);
		}

		boolean defaultConstructor = classData.hasDefaultConstructor();

		code.addNew(CONSTRUCTOR_DATA_CLASSNAME);
		code.add(Opcode.DUP);
		code.addIconst(defaultConstructor ? 1 : 0);
		code.addInvokespecial(CONSTRUCTOR_DATA_CLASSNAME, MethodInfo.nameInit, CONSTRUCTOR_DATA_CONSTRUCTOR);
		code.addAstore(localVariableCounter++);

		if (!defaultConstructor && classData.getConstructor() != null) {
			for (Entry<Integer, FieldData> entry : classData.getConstructor().getConstructorElements().entrySet()) {
				Integer argumentIndex = entry.getKey();
				FieldData field = entry.getValue();

				// load Local Variable ConstructorData
				code.addAload(localVariableCounter - 1);

				code.addIconst(argumentIndex);
				code.addAload(localVariableIndex.get(field.getName()));
				code.addInvokevirtual(CONSTRUCTOR_DATA_CLASSNAME, CONSTRUCTOR_DATA_METHOD_ADD_ELEMENT,
						CONSTRUCTOR_DATA_METHOD_ADD_ELEMENT_DESC);
			}

		}

		code.addNew(CLASS_DATA_CLASSNAME);
		code.add(Opcode.DUP);
		code.addLdc(classData.getName());

		if (classData.getSuperClass() != null) {

			int bootstrapMethodAttributeIndex = createLambdaForSuperclass(classFile);

			code.addInvokedynamic(bootstrapMethodAttributeIndex, SUPPLIER_METHOD_NAME, SUPPLIER_METHOD_DESC);
		}

		// load Local Variable ConstructorData
		code.addAload(localVariableCounter - 1);
		code.addInvokespecial(CLASS_DATA_CLASSNAME, MethodInfo.nameInit,
				classData.getSuperClass() != null ? CLASS_DATA_CONSTRUCTOR_WITH_SUPERCLASS : CLASS_DATA_CONSTRUCTOR);
		code.addPutstatic(loadingClass, FIELD_NAME, CLASS_DATA);

		for (Entry<FieldData, List<MethodData>> entry : classData.getFieldsUsedInMethods().entrySet()) {
			FieldData field = entry.getKey();
			List<MethodData> methods = entry.getValue();

			if (methods != null && !methods.isEmpty()) {

				methods.sort((m1, m2) -> m1.getMethodType().compareTo(m2.getMethodType()));

				MethodData method = methods.get(0);

				MethodType type = method.getMethodType();

				if (MethodType.REFERENCE_VALUE_SETTER == type || JavaTypes.COLLECTIONS.contains(field.getDataType())
						&& (MethodType.COLLECTION_SETTER == type || MethodType.REFERENCE_VALUE_GETTER == type)) {

					code.addGetstatic(loadingClass, FIELD_NAME, CLASS_DATA);
					// load specific LocalVariable Field
					code.addAload(localVariableIndex.get(field.getName()));

					code.addNew(SETTER_METHOD_DATA_CLASSNAME);
					code.add(Opcode.DUP);
					code.addLdc(method.getName());
					code.addLdc(method.getDescriptor());
					code.addIconst(method.isStatic() ? 1 : 0);

					if (JavaTypes.COLLECTIONS.contains(field.getDataType())) {
						code.addGetstatic(SETTER_TYPE_CLASSNAME, getSetterType(type), SETTER_TYPE);
						code.addInvokespecial(SETTER_METHOD_DATA_CLASSNAME, MethodInfo.nameInit,
								SETTER_METHOD_DATA_CONSTRUCTOR_WITH_SETTER_TYPE);
					} else {
						code.addInvokespecial(SETTER_METHOD_DATA_CLASSNAME, MethodInfo.nameInit,
								SETTER_METHOD_DATA_CONSTRUCTOR);
					}
					code.addInvokevirtual(CLASS_DATA_CLASSNAME, CLASS_DATA_METHOD_ADD_FIELD,
							CLASS_DATA_METHOD_ADD_FIELD_DESC);
				}
			}

		}

		if (returnInstruction.isPresent()) {
			codeAttribute.iterator().insertEx(returnInstruction.get().getCodeArrayIndex(), code.get());
		} else {
			code.add(Opcode.RETURN);
			codeAttribute.iterator().append(code.get());
		}

		codeAttribute.setMaxLocals(localVariableCounter);
		codeAttribute.computeMaxStack();
	}

	private int createLambdaForSuperclass(ClassFile classFile) throws BadBytecode, DuplicateMemberException {

		SupplierBootstrapMethodCreator bootstrapMethodCreator = new SupplierBootstrapMethodCreator(classFile,
				constantPool);

		int bootstrapMethodAttributeIndex = bootstrapMethodCreator.create(InvocationType.INVOKE_STATIC,
				classData.getName(), INDY_METHOD_NAME);

		MethodInfo lambdaBody = new MethodInfo(constantPool, INDY_METHOD_NAME, INDY_SUPPLIER_TYPED_RETURN_TYPE);
		lambdaBody.setAccessFlags(AccessFlag.STATIC | AccessFlag.PRIVATE | AccessFlag.SYNTHETIC);

		Bytecode lambdaBodyCode = new Bytecode(constantPool);
		lambdaBodyCode.addLdc(constantPool.addClassInfo(Descriptor.toJvmName(classData.getSuperClass())));
		lambdaBodyCode.addLdc(FIELD_NAME);
		lambdaBodyCode.addInvokestatic(METHODHANDLES_CLASSNAME, METHODHANDLES_METHOD_NAME, METHODHANDLES_RETURN_TYPE);
		lambdaBodyCode.addCheckcast(CLASS_DATA_CLASSNAME);
		lambdaBodyCode.addOpcode(Opcode.ARETURN);

		CodeAttribute lambdaBodycodeAttribute = lambdaBodyCode.toCodeAttribute();
		lambdaBodycodeAttribute.computeMaxStack();

		lambdaBody.setCodeAttribute(lambdaBodycodeAttribute);

		classFile.addMethod(lambdaBody);

		return bootstrapMethodAttributeIndex;
	}

	private String getSetterType(MethodType type) {
		if (type == MethodType.REFERENCE_VALUE_SETTER) {
			return SETTER_TYPE_FIELDNAME_VALUE_SETTER;
		} else if (type == MethodType.REFERENCE_VALUE_GETTER) {
			return SETTER_TYPE_FIELDNAME_VALUE_GETTER;
		} else if (type == MethodType.COLLECTION_SETTER) {
			return SETTER_TYPE_FIELDNAME_COLLECTION_SETTER;
		}
		throw new IllegalArgumentException(type + "is not a valid MethodType");
	}

}
