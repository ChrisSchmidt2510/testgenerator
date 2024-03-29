package org.testgen.agent.classdata.modification;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.modification.indy.InvocationType;
import org.testgen.agent.classdata.modification.indy.SupplierBootstrapMethodCreator;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.Wrapper;

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
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class ClassDataGenerator {

	private static final String CLASS_DATA_ACCESS_CLASSNAME = "org/testgen/runtime/classdata/access/ClassDataAccess";
	private static final String CLASS_DATA_ACCESS_METHOD_GET_CLASSDATA = "getClassData";
	private static final String CLASS_DATA_ACCESS_METHOD_GET_CLASSDATA_DESC = "(Ljava/lang/Class;)Lorg/testgen/runtime/classdata/model/ClassData;";

	private static final String CLASS_DATA_CLASSNAME = "org/testgen/runtime/classdata/model/ClassData";
	private static final String CLASS_DATA_CONSTRUCTOR = "(Ljava/lang/String;Lorg/testgen/runtime/classdata/model/ConstructorData;)V";
	private static final String CLASS_DATA_CONSTRUCTOR_ALL_PARAMETER = "(Ljava/lang/String;Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lorg/testgen/runtime/classdata/model/ConstructorData;)V";
	private static final String CLASS_DATA_METHOD_ADD_FIELD_SETTER_PAIR = "addFieldSetterPair";
	private static final String CLASS_DATA_METHOD_ADD_FIELD_SETTER_PAIR_DESC = "(Lorg/testgen/runtime/classdata/model/FieldData;Lorg/testgen/runtime/classdata/model/SetterMethodData;)V";
	private static final String CLASS_DATA_METHOD_ADD_FIELD = "addField";
	private static final String CLASS_DATA_METHOD_ADD_FIELD_DESC = "(Lorg/testgen/runtime/classdata/model/FieldData;)V";

	private static final String CONSTRUCTOR_DATA_CLASSNAME = "org/testgen/runtime/classdata/model/ConstructorData";
	private static final String CONSTRUCTOR_DATA_CONSTRUCTOR = "(Z)V";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_ELEMENT = "addElement";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_ELEMENT_DESC = "(ILorg/testgen/runtime/classdata/model/FieldData;)V";

	private static final String FIELD_DATA_CLASSNAME = "org/testgen/runtime/classdata/model/FieldData";
	private static final String FIELD_DATA_CONSTRUCTOR = "(ZLjava/lang/String;Ljava/lang/Class;)V";
	private static final String FIELD_DATA_METHOD_SET_SIGNATURE = "setSignature";
	private static final String FIELD_DATA_METHOD_SET_SIGNATURE_DESC = "(Lorg/testgen/runtime/classdata/model/descriptor/SignatureType;)V";

	private static final String SETTER_TYPE_CLASSNAME = "org/testgen/runtime/classdata/model/SetterType";
	private static final String SETTER_TYPE = "Lorg/testgen/runtime/classdata/model/SetterType;";
	private static final String SETTER_TYPE_FIELDNAME_VALUE_SETTER = "VALUE_SETTER";
	private static final String SETTER_TYPE_FIELDNAME_VALUE_GETTER = "VALUE_GETTER";
	private static final String SETTER_TYPE_FIELDNAME_COLLECTION_SETTER = "COLLECTION_SETTER";

	private static final String SETTER_METHOD_DATA_CLASSNAME = "org/testgen/runtime/classdata/model/SetterMethodData";
	private static final String SETTER_METHOD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String;ZLorg/testgen/runtime/classdata/model/SetterType;)V";

	private static final String INDY_SUPPLIER_TYPED_RETURN_TYPE = "()Lorg/testgen/runtime/classdata/model/ClassData;";
	private static final String INDY_METHOD_NAME = "testgenerator$";

	private static final String SUPPLIER_METHOD_DESC = "()Ljava/util/function/Supplier;";
	private static final String SUPPLIER_METHOD_NAME = "get";

	private static final String INTERFACE_CLASS_DATA_HOLDER = "org/testgen/runtime/classdata/ClassDataHolder";

	private final ClassData classData;

	private ConstPool constantPool;

	private final Map<String, Integer> localVariableIndex = new HashMap<>();
	private int localVariableCounter = 0;

	private int localVariableClassDataIndex;
	private int lambdaCounter = 0;

	public ClassDataGenerator(ClassData classData) {
		this.classData = classData;
	}

	public void generate(CtClass loadingClass)
			throws BadBytecode, CannotCompileException, IOException, ClassNotFoundException {

		ClassFile classFile = loadingClass.getClassFile();

		classFile.addInterface(INTERFACE_CLASS_DATA_HOLDER);

		this.constantPool = classFile.getConstPool();

		generateGetClassDataMethod(classFile);
	}

	private void generateGetClassDataMethod(ClassFile classFile) throws BadBytecode, CannotCompileException {

		Bytecode code = new Bytecode(constantPool);

		addFields(code);
		addConstructor(code);
		code.addNew(CLASS_DATA_CLASSNAME);
		code.add(Opcode.DUP);
		code.addLdc(classData.getName());

		if (classData.getSuperClass() != null || classData.isInnerClass()) {

			if (classData.getSuperClass() != null) {
				int bootstrapMethodAttributeIndex = createSupplierLambdaWithMethodHandle(classFile,
						Descriptor.toJvmName(classData.getSuperClass().getName()));

				code.addInvokedynamic(bootstrapMethodAttributeIndex, SUPPLIER_METHOD_NAME, SUPPLIER_METHOD_DESC);
			} else {
				code.add(Opcode.ACONST_NULL);
			}

			if (classData.isInnerClass()) {
				int bootstrapMethodIndex = createSupplierLambdaWithMethodHandle(classFile,
						Descriptor.toJvmName(classData.getOuterClass()));

				code.addInvokedynamic(bootstrapMethodIndex, SUPPLIER_METHOD_NAME, SUPPLIER_METHOD_DESC);
			} else {
				code.add(Opcode.ACONST_NULL);
			}
		}

		// load Local Variable ConstructorData
		code.addAload(localVariableCounter - 1);
		code.addInvokespecial(CLASS_DATA_CLASSNAME, MethodInfo.nameInit,
				classData.getSuperClass() != null || classData.isInnerClass() ? CLASS_DATA_CONSTRUCTOR_ALL_PARAMETER
						: CLASS_DATA_CONSTRUCTOR);

		localVariableClassDataIndex = localVariableCounter++;
		code.addAstore(localVariableClassDataIndex);

		for (Entry<String, Integer> field : localVariableIndex.entrySet()) {
			code.addAload(localVariableClassDataIndex);
			code.addAload(field.getValue());
			code.addInvokevirtual(CLASS_DATA_CLASSNAME, CLASS_DATA_METHOD_ADD_FIELD, CLASS_DATA_METHOD_ADD_FIELD_DESC);
		}

		addSetter(code);

		code.addAload(localVariableClassDataIndex);
		code.add(Opcode.ARETURN);

		CodeAttribute codeAttribute = code.toCodeAttribute();
		codeAttribute.computeMaxStack();
		codeAttribute.setMaxLocals(localVariableCounter);

		MethodInfo getClassData = classFile.getMethods().stream()
				.filter(method -> TestgeneratorConstants.CLASS_DATA_METHOD_GET_CLASS_DATA.equals(method.getName())
						&& TestgeneratorConstants.CLASS_DATA_METHOD_GET_CLASS_DATA_DESC.equals(method.getDescriptor()))
				.findAny().orElse(createGetClassDataMethodInfo(classFile));

		getClassData.setCodeAttribute(codeAttribute);

	}

	private MethodInfo createGetClassDataMethodInfo(ClassFile classFile) throws DuplicateMemberException {
		MethodInfo method = new MethodInfo(constantPool, TestgeneratorConstants.CLASS_DATA_METHOD_GET_CLASS_DATA,
				TestgeneratorConstants.CLASS_DATA_METHOD_GET_CLASS_DATA_DESC);
		method.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.SYNTHETIC);

		classFile.addMethod(method);

		return method;
	}

	private void addFields(Bytecode code) {

		for (FieldData field : classData.getFields()) {
			code.addNew(FIELD_DATA_CLASSNAME);
			code.add(Opcode.DUP);
			code.addIconst(field.isPublic() && field.isMutable() ? 1 : 0);
			code.addLdc(field.getName());
			BytecodeUtils.addClassInfoToBytecode(code, field.getDataType());
			code.addInvokespecial(FIELD_DATA_CLASSNAME, MethodInfo.nameInit, FIELD_DATA_CONSTRUCTOR);

			localVariableIndex.put(field.getName(), localVariableCounter);

			int currentFieldIndex = localVariableCounter;
			code.addAstore(localVariableCounter++);

			if (field.getSignature() != null) {
				code.addAload(currentFieldIndex);

				Wrapper<Integer> localVariableCounter = new Wrapper<>(this.localVariableCounter);
				int localSignatureIndex = SignatureAdder.add(code, field.getSignature(), localVariableCounter);
				code.addAload(localSignatureIndex);
				code.addInvokevirtual(FIELD_DATA_CLASSNAME, FIELD_DATA_METHOD_SET_SIGNATURE,
						FIELD_DATA_METHOD_SET_SIGNATURE_DESC);

				this.localVariableCounter = localVariableCounter.getValue();
			}

		}
	}

	private void addConstructor(Bytecode code) {
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
	}

	private void addSetter(Bytecode code) {
		for (Entry<FieldData, List<MethodData>> entry : classData.getFieldsUsedInMethods().entrySet()) {
			FieldData field = entry.getKey();
			List<MethodData> methods = entry.getValue();

			if (methods != null && !methods.isEmpty()) {

				methods.sort((m1, m2) -> m1.getMethodType().compareTo(m2.getMethodType()));

				MethodData method = methods.get(0);

				MethodType type = method.getMethodType();
				String dataType = field.getDataType();

				if (MethodType.REFERENCE_VALUE_SETTER == type
						|| (JavaTypes.COLLECTION_LIST.contains(dataType)
								&& (MethodType.COLLECTION_SETTER == type || MethodType.REFERENCE_VALUE_GETTER == type))
						|| JavaTypes.isArray(dataType) && MethodType.REFERENCE_VALUE_GETTER == type) {
					code.addAload(localVariableClassDataIndex);
					// load specific LocalVariable Field
					code.addAload(localVariableIndex.get(field.getName()));

					code.addNew(SETTER_METHOD_DATA_CLASSNAME);
					code.add(Opcode.DUP);
					code.addLdc(method.getName());
					code.addLdc(method.getDescriptor());
					code.addIconst(method.isStatic() ? 1 : 0);

					code.addGetstatic(SETTER_TYPE_CLASSNAME, getSetterType(type), SETTER_TYPE);
					code.addInvokespecial(SETTER_METHOD_DATA_CLASSNAME, MethodInfo.nameInit,
							SETTER_METHOD_DATA_CONSTRUCTOR);

					code.addInvokevirtual(CLASS_DATA_CLASSNAME, CLASS_DATA_METHOD_ADD_FIELD_SETTER_PAIR,
							CLASS_DATA_METHOD_ADD_FIELD_SETTER_PAIR_DESC);
				}
			}

		}
	}

	private int createSupplierLambdaWithMethodHandle(ClassFile classFile, String name)
			throws BadBytecode, DuplicateMemberException {

		SupplierBootstrapMethodCreator bootstrapMethodCreator = new SupplierBootstrapMethodCreator(classFile,
				constantPool, INDY_SUPPLIER_TYPED_RETURN_TYPE);

		int bootstrapMethodAttributeIndex = bootstrapMethodCreator.create(InvocationType.INVOKE_STATIC,
				classData.getName(), INDY_METHOD_NAME + lambdaCounter);

		MethodInfo lambdaBody = new MethodInfo(constantPool, INDY_METHOD_NAME + lambdaCounter++,
				INDY_SUPPLIER_TYPED_RETURN_TYPE);
		lambdaBody.setAccessFlags(AccessFlag.STATIC | AccessFlag.PRIVATE | AccessFlag.SYNTHETIC);

		Bytecode lambdaCode = new Bytecode(constantPool);
		lambdaCode.addLdc(constantPool.addClassInfo(name));
		lambdaCode.addInvokestatic(CLASS_DATA_ACCESS_CLASSNAME, CLASS_DATA_ACCESS_METHOD_GET_CLASSDATA,
				CLASS_DATA_ACCESS_METHOD_GET_CLASSDATA_DESC);
		lambdaCode.addOpcode(Opcode.ARETURN);

		CodeAttribute lambdaBodycodeAttribute = lambdaCode.toCodeAttribute();
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
