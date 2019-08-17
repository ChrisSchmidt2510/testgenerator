package de.nvg.javaagent.classdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import de.nvg.javaagent.classdata.model.ClassData;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.model.MethodData;
import de.nvg.javaagent.classdata.model.MethodType;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.BootstrapMethodsAttribute;
import javassist.bytecode.BootstrapMethodsAttribute.BootstrapMethod;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
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
	private static final String CONSTRUCTOR_DATA = "Lde/nvg/runtime/classdatamodel/ConstructorData;";
	private static final String CONSTRUCTOR_DATA_CONSTRUCTOR = "(Z)V";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_CONSTRUCTOR_ELEMENT = "addConstructorElement";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_CONSTRUCTOR_ELEMENT_DESC = "(Ljava/lang/Integer;Lde/nvg/runtime/classdatamodel/FieldData;)V";

	private static final String FIELD_DATA = "Lde/nvg/runtime/classdatamodel/FieldData;";
	private static final String FIELD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String);";

	private static final String SETTER_METHOD_DATA = "Lde/nvg/runtime/classdatamodel/SetterMethodData;";
	private static final String SETTER_METHOD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String;Z)V";

	private static final String INDY_SUPPLIER_GENERIC_RETURN_TYPE = "()Ljava/lang/Object;";
	private static final String INDY_SUPPLIER_TYPED_RETURN_TYPE = "()Lde/nvg/runtime/classdatamodel/ClassData;";

	private static final String INDY_METHOD_NAME = "testgenerator$0";

	private static final String SUPPLIER_METHOD_DESC = "()Ljava/util/function/Supplier;";
	private static final String SUPPLIER_METHOD_NAME = "get";

	private static final String BOOTSTRAP_METHOD_CLASS = "java/lang/invoke/LambdaMetafactory";
	private static final String BOOTSTRAP_METHOD_NAME = "metafactory";
	private static final String BOOTSTRAP_METHOD_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;";

	private static final String METHODHANDLES_CLASS_NAME = "Lde/nvg/testgenerator/MethodHandles";
	private static final String METHODHANDLES_METHOD_NAME = "getStaticFieldValue";
	private static final String METHODHANDLES_RETURN_TYPE = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;";

	private static final String FIELD_NAME = "classData";

	private static final String COLLECTION = "java.util.Collection";
	private static final String LIST = "java.util.List";
	private static final String SET = "java.util.Set";
	private static final String MAP = "java.util.Map";
	private static final String QUEUE = "java.util.Queue";

	private static final List<String> COLLECTIONS = Collections
			.unmodifiableList(Arrays.asList(COLLECTION, LIST, SET, MAP, QUEUE));

	public MetaDataAdder(ConstPool constantPool, CtClass loadingClass, ClassData classData) {
		this.constantPool = constantPool;
		this.loadingClass = loadingClass;
		this.classData = classData;
	}

	public void add(CodeAttribute codeAttribute, List<Instruction> instructions)
			throws CannotCompileException, BadBytecode {
		loadingClass.addField(CtField.make("private static de.nvg.runtime.classdatamodel.ClassData " + FIELD_NAME + ";",
				loadingClass));

		Optional<Instruction> returnInstruction = instructions.stream()
				.filter(instruction -> Opcode.RETURN == instruction.getOpcode()).findAny();

		Bytecode code = new Bytecode(constantPool);
		code.addNew(CLASS_DATA_CLASSNAME);
		code.add(Opcode.DUP);
		code.addLdc(classData.getName());

		if (classData.getSuperClass() != null) {
			ClassFile classFile = loadingClass.getClassFile();

			int bootstrapMethodIndex = constantPool.addMethodrefInfo(constantPool.addClassInfo(BOOTSTRAP_METHOD_CLASS),
					BOOTSTRAP_METHOD_NAME, BOOTSTRAP_METHOD_DESC);

			int bootstrapMethodHandleIndex = constantPool.addMethodHandleInfo(ConstPool.REF_invokeStatic,
					bootstrapMethodIndex);

			int genericMethodTypeIndex = constantPool
					.addMethodTypeInfo(constantPool.addUtf8Info(INDY_SUPPLIER_GENERIC_RETURN_TYPE));

			int lambdaBodyMethodIndex = constantPool.addMethodrefInfo(constantPool.addClassInfo(classData.getName()),
					INDY_METHOD_NAME, INDY_SUPPLIER_TYPED_RETURN_TYPE);

			int methodHandleIndex = constantPool.addMethodHandleInfo(ConstPool.REF_invokeStatic, lambdaBodyMethodIndex);

			int typedMethodTypeIndex = constantPool
					.addMethodTypeInfo(constantPool.addUtf8Info(INDY_SUPPLIER_TYPED_RETURN_TYPE));

			BootstrapMethod bootstrapMethod = new BootstrapMethod(bootstrapMethodHandleIndex,
					new int[] { genericMethodTypeIndex, methodHandleIndex, typedMethodTypeIndex });

			int bootstrapMethodAttributeIndex = 0;

			if (classFile.getAttribute(BootstrapMethodsAttribute.tag) != null) {

				BootstrapMethodsAttribute attribute = (BootstrapMethodsAttribute) classFile
						.getAttribute(BootstrapMethodsAttribute.tag);
				BootstrapMethod[] bootstrapMethods = attribute.getMethods();

				bootstrapMethodAttributeIndex = bootstrapMethods.length;

				BootstrapMethod[] copyOfBootrapMethods = Arrays.copyOf(bootstrapMethods, bootstrapMethods.length);
				copyOfBootrapMethods[bootstrapMethodAttributeIndex] = bootstrapMethod;

				classFile.addAttribute(new BootstrapMethodsAttribute(constantPool, copyOfBootrapMethods));

			} else {
				classFile.addAttribute(
						new BootstrapMethodsAttribute(constantPool, new BootstrapMethod[] { bootstrapMethod }));
			}

			MethodInfo lambdaBody = new MethodInfo(constantPool, "testgenerator$0", INDY_SUPPLIER_TYPED_RETURN_TYPE);
			lambdaBody.setAccessFlags(AccessFlag.STATIC | AccessFlag.PRIVATE | AccessFlag.SYNTHETIC);

			Bytecode lambdaBodyCode = new Bytecode(constantPool);
			lambdaBodyCode.addLdc(constantPool.addClassInfo(Descriptor.toJvmName(classData.getSuperClass())));
			lambdaBodyCode.addLdc(FIELD_NAME);
			lambdaBodyCode.addInvokestatic(METHODHANDLES_CLASS_NAME, METHODHANDLES_METHOD_NAME,
					METHODHANDLES_RETURN_TYPE);
			lambdaBodyCode.addCheckcast(CLASS_DATA);
			lambdaBodyCode.addOpcode(Opcode.ARETURN);

			CodeAttribute lambdaBodycodeAttribute = lambdaBodyCode.toCodeAttribute();
			lambdaBodycodeAttribute.computeMaxStack();

			lambdaBody.setCodeAttribute(lambdaBodycodeAttribute);

			classFile.addMethod(lambdaBody);

			code.addInvokedynamic(bootstrapMethodAttributeIndex, SUPPLIER_METHOD_NAME, SUPPLIER_METHOD_DESC);
		}

		code.addNew(CONSTRUCTOR_DATA_CLASSNAME);
		code.add(Opcode.DUP);
		code.addIconst(classData.hasDefaultConstructor() ? 1 : 0);
		code.addInvokespecial(CONSTRUCTOR_DATA_CLASSNAME, MethodInfo.nameInit, CONSTRUCTOR_DATA_CONSTRUCTOR);
		code.addInvokespecial(CLASS_DATA_CLASSNAME, MethodInfo.nameInit,
				classData.getSuperClass() != null ? CLASS_DATA_CONSTRUCTOR_WITH_SUPERCLASS : CLASS_DATA_CONSTRUCTOR);
		code.addPutstatic(loadingClass, FIELD_NAME, CLASS_DATA);

		for (Entry<FieldData, List<MethodData>> entry : classData.getFieldsUsedInMethods().entrySet()) {
			FieldData field = entry.getKey();
			List<MethodData> methods = entry.getValue();

			if (methods != null && !methods.isEmpty()) {

				methods.sort((m1, m2) -> m1.getMethodType().compareTo(m2.getMethodType()));

				MethodData method = methods.get(0);

				if (MethodType.REFERENCE_VALUE_SETTER == method.getMethodType()
						|| COLLECTIONS.contains(field.getDataType())
								&& (MethodType.COLLECTION_SETTER == method.getMethodType()
										|| MethodType.REFERENCE_VALUE_GETTER == method.getMethodType())) {

					code.addAload(0);
					code.addNew(FIELD_DATA);
					code.add(Opcode.DUP);
					code.addLdc(field.getName());
					code.addLdc(field.getDataType());
					code.addInvokespecial(FIELD_DATA, MethodInfo.nameInit, FIELD_DATA_CONSTRUCTOR);

					code.addNew(SETTER_METHOD_DATA);
					code.add(Opcode.DUP);
					code.addLdc(method.getName());
					code.addLdc(method.getDescriptor());
					code.addIconst(method.isStatic() ? 1 : 0);
					code.addInvokespecial(SETTER_METHOD_DATA_CONSTRUCTOR, MethodInfo.nameInit,
							SETTER_METHOD_DATA_CONSTRUCTOR);
					code.addInvokevirtual(CLASS_DATA, CLASS_DATA_METHOD_ADD_FIELD, CLASS_DATA_METHOD_ADD_FIELD_DESC);
				}
			}

		}

		if (returnInstruction.isPresent()) {
			codeAttribute.iterator().insertEx(returnInstruction.get().getCodeArrayIndex(), code.get());
		} else {
			codeAttribute.iterator().append(code.get());
		}
		codeAttribute.computeMaxStack();
	}

}
