package org.testgen.agent.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testgen.agent.AgentException;
import org.testgen.agent.classdata.analysis.method.Analyser;
import org.testgen.agent.classdata.analysis.method.MethodAnalyser;
import org.testgen.agent.classdata.analysis.signature.SignatureParser;
import org.testgen.agent.classdata.analysis.signature.SignatureParserException;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Modifiers;
import org.testgen.agent.classdata.instructions.Instruction;
import org.testgen.agent.classdata.instructions.Instructions;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.ClassDataStorage;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.modification.ClassDataGenerator;
import org.testgen.agent.classdata.modification.fields.FieldTypeChanger;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.core.ReflectionUtil;
import org.testgen.core.TestgeneratorConstants;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;

public class ClassDataTransformer implements ClassTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ClassDataTransformer.class);

	@Override
	public boolean modifyClassFile(String className, CtClass ctClass) {
		if (TestgeneratorConfig.getBlPackages().stream().noneMatch(className::startsWith)
				&& !ClassDataStorage.getInstance().containsSuperclassToLoad(Descriptor.toJavaName(className))
				&& !TestgeneratorConfig.getClassNames().contains(className)) {
			return false;
		}

		if (ctClass.isEnum()) {
			LOGGER.info(className + " is an Enum");
			return false;

		} else if (ctClass.isInterface()) {
			LOGGER.info(className + " is an Interface");
			return false;

		} else if (isProxyImplementation(ctClass.getClassFile())) {
			LOGGER.info(className + " is a proxy implementation");
			return false;
		} else if (isException(ctClass)) {
			LOGGER.info(className + " is an Exception");
			return false;
		}

		return true;

	}

	@Override
	public void transformClassFile(String className, CtClass loadingClass) {
		ClassDataStorage.getInstance().removeSuperclassToLoad(Descriptor.toJavaName(className));

		try {

			LOGGER.info("create ClassHierachie for " + loadingClass.getName());
			ClassData classData = analyseClassHierachie(loadingClass);

			manipulateFields(loadingClass, classData);

			long start = System.currentTimeMillis();

			analyseAndManipulateMethods(loadingClass, classData);

			long end = System.currentTimeMillis();

			LOGGER.info("Processing of manipulation for class " + classData + " :" + (end - start));

			ClassDataStorage.getInstance().addClassData(loadingClass.getName(), classData);

			ClassDataGenerator classDataGenerator = new ClassDataGenerator(classData);
			classDataGenerator.generate(loadingClass);

		} catch (Throwable e) {
			LOGGER.error("error while transforming class", e);
			throw new AgentException("error while transforming class", e);
		}
	}

	private boolean isProxyImplementation(ClassFile classFile) {
		return classFile.getFields().stream().anyMatch(f -> JVMTypes.INVOCATION_HANDLER.equals(f.getDescriptor()));
	}

	private boolean isException(CtClass ctClass) {
		CtClass superclass = null;
		try {
			superclass = ctClass.getSuperclass();
		} catch (NotFoundException e) {
			LOGGER.error("superclass not found", e);
		}

		if (JavaTypes.OBJECT.equals(superclass.getName())) {
			return false;
		} else if (JavaTypes.EXCEPTION.equals(superclass.getName())) {
			return true;
		}

		return isException(superclass);
	}

	private List<FieldData> analyseFields(ClassFile loadedClass) {
		List<FieldData> fieldsFromClass = new ArrayList<>();

		for (FieldInfo field : loadedClass.getFields()) {

			if (!Modifiers.isConstant(field.getAccessFlags())) {

				SignatureAttribute signature = (SignatureAttribute) field.getAttribute(SignatureAttribute.tag);

				SignatureData signatureData = null;
				try {
					if (signature != null) {
						signatureData = SignatureParser.parse(signature.getSignature());
					}

				} catch (SignatureParserException e) {
					LOGGER.error("error while parsing signature " + signature, e);
				}

				FieldData fieldData = new FieldData.Builder()
						.withDataType(Descriptor.toClassName(field.getDescriptor())).withName(field.getName())
						.withModifier(field.getAccessFlags()).withSignature(signatureData).build();

				LOGGER.info("added Field: " + fieldData);

				fieldsFromClass.add(fieldData);
			}
		}

		return fieldsFromClass;
	}

	private void manipulateFields(CtClass loadingClass, ClassData classData) throws CannotCompileException {
		// only add the calledFields Set if the Flag is set
		if (!TestgeneratorConfig.traceReadFieldAccess() || classData.isSerializable()) {
			return;
		}

		ClassFile classFile = loadingClass.getClassFile();

		classFile.addInterface(TestgeneratorConstants.PROXIFIED_CLASSNAME);

		// necessary because otherwise the indexes of the list get overridden
		List<FieldInfo> fields = new ArrayList<>(classFile.getFields());
		int size = fields.size();

		for (int i = 0; i < size; i++) {
			FieldInfo field = fields.get(i);

			if (!Modifiers.isConstant(field.getAccessFlags()) //
					&& !AccessFlag.isPublic(field.getAccessFlags()) && !Modifier.isPackage(field.getAccessFlags())
					&& !Modifiers.isSynthetic(field.getAccessFlags())
					&& !TestgeneratorConstants.isTestgeneratorField(field.getName())
					// temp. fix exclude static fields, maybe they are supported in a later version
					&& !Modifier.isStatic(field.getAccessFlags())) {
				FieldTypeChanger.changeFieldDataTypeToProxy(classFile, field);
			}

		}

		FieldTypeChanger.addFieldCalledField(loadingClass);

	}

	private void analyseAndManipulateMethods(CtClass loadingClass, ClassData classData) throws BadBytecode {

		ClassFile classFile = loadingClass.getClassFile();

		Analyser methodAnalyser = getAnalyserImplementation(classData, classFile);

		FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, classFile.getConstPool(), loadingClass);

		List<MethodInfo> methods = classFile.getMethods();
		for (MethodInfo method : methods) {
			if (method.isMethod() && !Modifier.isAbstract(method.getAccessFlags())) {

				List<Instruction> instructions = Instructions.getAllInstructions(method);
				analyseMethod(method, instructions, classData, methodAnalyser);

				if (TestgeneratorConfig.traceReadFieldAccess() && !classData.isSerializable()) {
					manipulateMethod(method, instructions, fieldTypeChanger);
				}

			}
		}

		List<MethodInfo> constructors = methods.stream().filter(MethodInfo::isConstructor).collect(Collectors.toList());

		for (MethodInfo constructor : constructors) {

			List<Instruction> instructions = Instructions.getAllInstructions(constructor);
			methodAnalyser.analyseMethod(constructor, instructions);

			if (TestgeneratorConfig.traceReadFieldAccess() && !classData.isSerializable()) {
				manipulateConstructor(constructor, instructions, fieldTypeChanger);
			}

		}

		methodAnalyser.resetMethodAnalyser();

	}

	ClassData analyseClassHierachie(CtClass loadingClass) throws NotFoundException {
		String className = loadingClass.getName();

		ClassData classData = ClassDataStorage.getInstance().getClassData(className);

		if (classData == null) {
			ClassData newClassData = new ClassData(className);

			LOGGER.info("ClassName: " + className);
			newClassData.addFields(analyseFields(loadingClass.getClassFile()));

			String superClassName = loadingClass.getSuperclass() != null ? loadingClass.getSuperclass().getName()
					: null;

			if (!JavaTypes.OBJECT.equals(superClassName) && !JavaTypes.ENUM.equals(superClassName)) {
				LOGGER.info("SuperClass: " + loadingClass.getSuperclass().getName());

				ClassDataStorage.getInstance().addSuperclassToLoad(loadingClass.getSuperclass().getName());
				newClassData.setSuperClass(analyseClassHierachie(loadingClass.getSuperclass()));
			}

			analyseInnerClasses(loadingClass, newClassData);

			for (CtClass interfaceClass : loadingClass.getInterfaces()) {
				analyseInterface(interfaceClass, newClassData);
			}

			ClassDataStorage.getInstance().addClassData(className, newClassData);

			return newClassData;
		}

		return classData;
	}

	private void analyseInnerClasses(CtClass loadingClass, ClassData newClassData) throws NotFoundException {
		ClassFile classFile = loadingClass.getClassFile();

		InnerClassesAttribute innerClassesAtt = (InnerClassesAttribute) classFile
				.getAttribute(InnerClassesAttribute.tag);

		if (innerClassesAtt != null) {
			int index = innerClassesAtt.find(classFile.getName());

			if (index != -1) {
				newClassData.setOuterClass(innerClassesAtt.outerClass(index));
			}

			List<String> innerClasses = getInnerClasses(innerClassesAtt, loadingClass.getName());

			ClassPool classPool = ClassPool.getDefault();

			for (String innerClass : innerClasses) {
				LOGGER.info("InnerClass: " + innerClass);
				newClassData.addInnerClass(analyseClassHierachie(classPool.get(innerClass)));
			}

		}
	}

	private List<String> getInnerClasses(InnerClassesAttribute innerClassesAtt, String outerClassName) {
		int length = innerClassesAtt.tableLength();

		List<String> innerClasses = new ArrayList<>();

		for (int i = 0; i < length; i++) {

			if (!innerClassesAtt.innerClass(i).equals(outerClassName))
				innerClasses.add(innerClassesAtt.innerClass(i));
		}

		return innerClasses;
	}

	private void analyseInterface(CtClass interfaceClass, ClassData classData) throws NotFoundException {
		classData.addInterface(interfaceClass.getName());

		for (CtClass interfaceCls : interfaceClass.getInterfaces()) {
			analyseInterface(interfaceCls, classData);
		}
	}

	private Analyser getAnalyserImplementation(ClassData classData, ClassFile classFile) {
		String customAnalysisClass = TestgeneratorConfig.getCustomAnalysisClass();

		if (customAnalysisClass != null) {
			Class<?> customAnalysis = ReflectionUtil.forName(customAnalysisClass);

			if (Analyser.class.isAssignableFrom(customAnalysis)) {
				throw new IllegalArgumentException(customAnalysisClass + "need to extend Analyser.class");
			}

			if (ReflectionUtil.getConstructor(customAnalysis, ClassData.class, ClassFile.class) == null) {
				throw new IllegalArgumentException(
						customAnalysisClass + "is a invalid implementation. Constructorargs: ClassData, ClassFile");
			}

			return (Analyser) ReflectionUtil.newInstance(customAnalysis,
					new Class<?>[] { ClassData.class, ClassFile.class }, classData, classFile);
		}

		return new MethodAnalyser(classData, classFile);
	}

	private void analyseMethod(MethodInfo method, List<Instruction> instructions, ClassData classData,
			Analyser methodAnalyser) {

		if (!JavaTypes.OBJECT_STANDARD_METHODS.contains(method.getName())) {

			methodAnalyser.analyseMethod(method, instructions);
		}
	}

	private void manipulateMethod(MethodInfo method, List<Instruction> instructions, FieldTypeChanger fieldTypeChanger)
			throws BadBytecode {

		LOGGER.info("Starte Transformation fuer die Methode " + method.getName() + method.getDescriptor());

		Map<Integer, List<Instruction>> filteredInstructions = Instructions.getFilteredInstructions(instructions,
				Arrays.asList(Opcode.PUTFIELD, Opcode.GETFIELD));

		fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, //
				method.getCodeAttribute());

		method.rebuildStackMap(ClassPool.getDefault());
	}

	private void manipulateConstructor(MethodInfo method, List<Instruction> instructions,
			FieldTypeChanger fieldTypeChanger) throws BadBytecode {
		List<Instruction> filteredInstructions = instructions.stream()
				.filter(inst -> Opcode.PUTFIELD == inst.getOpcode()).collect(Collectors.toList());

		List<Instruction> putFieldInstructions = filteredInstructions == null ? Collections.emptyList()
				: filteredInstructions;

		fieldTypeChanger.changeFieldInitialization(instructions, putFieldInstructions, method.getCodeAttribute());

		method.rebuildStackMap(ClassPool.getDefault());
	}

}
