package de.nvg.javaagent.classdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import de.nvg.javaagent.classdata.model.ClassData;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.model.MethodData;
import de.nvg.javaagent.classdata.model.MethodType;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

public class MetaDataAdder {
	private final ConstPool constantPool;
	private final CtClass loadingClass;
	private final ClassData classData;

//	private static final Map<String, Byte> CLASS_DATA_CACHE = new HashMap<>();

	private static final String CLASS_DATA = "Lde/nvg/runtime/classdatamodel/ClassData;";
	private static final String CLASS_DATA_CONSTRUCTOR = "(Ljava/lang/String;Lde/nvg/runtime/classdatamodel/ConstructorData;)V";
	private static final String CLASS_DATA_CONSTRUCTOR_WITH_SUPERCLASS = "(Ljava/lang/String;Lde/nvg/runtime/classdatamodel/ClassData;Lde/nvg/runtime/classdatamodel/ConstructorData;)V";
	private static final String CLASS_DATA_METHOD_ADD_FIELD = "addField";
	private static final String CLASS_DATA_METHOD_ADD_FIELD_DESC = "(Lde/nvg/runtime/classdatamodel/FieldData;Lde/nvg/runtime/classdatamodel/SetterMethodData;)V";

	private static final String CONSTRUCTOR_DATA = "Lde/nvg/runtime/classdatamodel/ConstructorData;";
	private static final String CONSTRUCTOR_DATA_CONSTRUCTOR = "(Z)V";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_CONSTRUCTOR_ELEMENT = "addConstructorElement";
	private static final String CONSTRUCTOR_DATA_METHOD_ADD_CONSTRUCTOR_ELEMENT_DESC = "(Ljava/lang/Integer;Lde/nvg/runtime/classdatamodel/FieldData;)V";

	private static final String FIELD_DATA = "Lde/nvg/runtime/classdatamodel/FieldData;";
	private static final String FIELD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String);";

	private static final String SETTER_METHOD_DATA = "Lde/nvg/runtime/classdatamodel/SetterMethodData;";
	private static final String SETTER_METHOD_DATA_CONSTRUCTOR = "(Ljava/lang/String;Ljava/lang/String;Z)V";

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

	public void add(CodeAttribute codeAttribute) throws CannotCompileException, BadBytecode {
		loadingClass.addField(
				CtField.make("private static de.nvg.runtime.classdatamodel.ClassData classData", loadingClass));

		// constantPool.addInvokeDynamicInfo(bootstrap, nameAndType)

		Bytecode code = new Bytecode(constantPool);
		code.addNew(CLASS_DATA);
		code.add(Opcode.DUP);
		code.addLdc(classData.getName());
		code.addNew(CONSTRUCTOR_DATA);
		code.add(Opcode.DUP);
		code.addIconst(classData.hasDefaultConstructor() ? 1 : 0);
		code.addInvokespecial(CONSTRUCTOR_DATA, MethodInfo.nameInit, CONSTRUCTOR_DATA_CONSTRUCTOR);
		code.addInvokespecial(CLASS_DATA, MethodInfo.nameInit, CLASS_DATA_CONSTRUCTOR);
		code.addAstore(0);

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

		codeAttribute.iterator().append(code.get());
		codeAttribute.computeMaxStack();
	}

}
