package de.nvg.agent.transformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.testgen.core.TestgeneratorConstants;
import org.testgen.core.Wrapper;
import org.testgen.core.classdata.constants.JavaTypes;
import org.testgen.core.properties.AgentProperties;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import de.nvg.agent.AgentException;
import de.nvg.agent.classdata.analysis.MethodAnalyser;
import de.nvg.agent.classdata.analysis.signature.SignatureParser;
import de.nvg.agent.classdata.analysis.signature.SignatureParserException;
import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.ClassDataStorage;
import de.nvg.agent.classdata.model.ConstructorData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.agent.classdata.modification.ClassDataGenerator;
import de.nvg.agent.classdata.modification.fields.FieldTypeChanger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;

public class ClassDataTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ClassDataTransformer.class);

	private static final String PROXIFIED = "de/nvg/proxy/Proxified";

	private final AgentProperties properties = AgentProperties.getInstance();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if (properties.getBlPackage().stream().anyMatch(className::startsWith)
				|| ClassDataStorage.getInstance().containsSuperclassToLoad(Descriptor.toJavaName(className))
				|| properties.getClassNames().contains(className)) {

			ClassDataStorage.getInstance().removeSuperclassToLoad(Descriptor.toJavaName(className));

			final ClassPool pool = ClassPool.getDefault();

			CtClass loadingClass = null;

			try (ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer)) {
				loadingClass = pool.makeClass(stream);

				ClassData classData = collectClassData(loadingClass);
				ClassDataStorage.getInstance().addClassData(loadingClass.getName(), classData);

				if (!classData.isEnum()) {
					ClassDataGenerator classDataGenerator = new ClassDataGenerator(classData, loader);
					classDataGenerator.generate(loadingClass);
				}

				return loadingClass.toBytecode();

			} catch (Throwable e) {
				LOGGER.error(e);
				throw new AgentException("Es ist ein Fehler bei der Transfomation aufgetreten", e);
			} finally {
				if (loadingClass != null)
					loadingClass.detach();
			}
		}

		return classfileBuffer;
	}

	private ClassData collectClassData(CtClass loadingClass)
			throws NotFoundException, CannotCompileException, BadBytecode, IOException {
		ClassData classData;

		ClassFile classFile = loadingClass.getClassFile();
		ConstPool constantPool = classFile.getConstPool();

		if (Modifier.isEnum(loadingClass.getModifiers())) {
			LOGGER.info("isEnum: true");
			classData = new ClassData(loadingClass.getName());
			classData.setIsEnum(true);

		} else {
			LOGGER.info("create ClassHierachie for " + classFile.getName());
			classData = createClassHierachie(loadingClass);

			checkIsInnerClass(classFile, classData);

			MethodAnalyser methodAnalyser = new MethodAnalyser(classData);

			FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, //
					loadingClass);

			// only add the calledFields Set if the Flag is set
			if (properties.isTraceReadFieldAccess()) {

				classFile.addInterface(PROXIFIED);

				for (CtField field : loadingClass.getDeclaredFields()) {

					if (!Instructions.isConstant(field.getModifiers()) //
							&& !AccessFlag.isPublic(field.getModifiers()) && !isSynthetic(field.getModifiers())
							&& !TestgeneratorConstants.isTestgeneratorField(field.getName())) {
						FieldTypeChanger.changeFieldDataTypeToProxy(classFile, field.getFieldInfo());
					}
				}

				fieldTypeChanger.addFieldCalledField();
			}

			long start = System.currentTimeMillis();

			analyseAndManipulateMethods(classFile.getMethods(), classData, methodAnalyser, fieldTypeChanger);

			long end = System.currentTimeMillis();

			LOGGER.info("Processing of manipulation for class " + classData + " :" + (end - start));
		}

		return classData;
	}

	private List<FieldData> analyseFields(ClassFile loadedClass) {
		List<FieldData> fieldsFromClass = new ArrayList<>();

		for (FieldInfo field : loadedClass.getFields()) {

			if (!Instructions.isConstant(field.getAccessFlags())) {

				SignatureAttribute signature = (SignatureAttribute) field.getAttribute(SignatureAttribute.tag);

				SignatureData signatureData = null;
				try {
					if (signature != null) {
						signatureData = SignatureParser.parse(signature.getSignature());
					}

				} catch (SignatureParserException e) {
					LOGGER.error(e);
				}

				FieldData fieldData = new FieldData.Builder()
						.withDataType(Descriptor.toClassName(field.getDescriptor())).withName(field.getName())
						.isMutable(!Modifier.isFinal(field.getAccessFlags()))
						.isStatic(Modifier.isStatic(field.getAccessFlags()))
						.isPublic(Modifier.isPublic(field.getAccessFlags()))
						.isSynthetic(isSynthetic(field.getAccessFlags())).withSignature(signatureData).build();

				LOGGER.info("added Field: " + fieldData);

				fieldsFromClass.add(fieldData);
			}
		}

		return fieldsFromClass;
	}

	private void analyseAndManipulateMethods(List<MethodInfo> methods, ClassData classData,
			MethodAnalyser methodAnalyser, FieldTypeChanger fieldTypeChanger) throws BadBytecode {

		for (MethodInfo method : methods) {
			if (!isMethodClInitOrConstructor(method) && !Modifier.isAbstract(method.getAccessFlags())) {

				List<Instruction> instructions = Instructions.getAllInstructions(method);
				analyseMethod(method, instructions, classData, methodAnalyser);

				if (properties.isTraceReadFieldAccess()) {
					manipulateMethod(method, instructions, fieldTypeChanger);
				}
			}
		}

		List<MethodInfo> constructors = methods.stream().filter(method -> MethodInfo.nameInit.equals(//
				method.getName())).collect(Collectors.toList());

		for (MethodInfo constructor : constructors) {

			List<Instruction> instructions = Instructions.getAllInstructions(constructor);
			analyseConstructor(constructor, instructions, classData, methodAnalyser);

			if (properties.isTraceReadFieldAccess()) {
				manipulateConstructor(constructor, instructions, fieldTypeChanger);
			}
		}

	}

	private boolean isMethodClInitOrConstructor(MethodInfo method) {
		return MethodInfo.nameClinit.equals(method.getName()) || MethodInfo.nameInit.equals(method.getName());
	}

	private ClassData createClassHierachie(CtClass loadingClass) throws NotFoundException {
		String className = loadingClass.getName();

		ClassData classData = ClassDataStorage.getInstance().getClassData(className);

		if (classData == null) {
			ClassData newClassData = new ClassData(className);

			LOGGER.info("ClassName: " + className);
			newClassData.addFields(analyseFields(loadingClass.getClassFile()));

			if (loadingClass.getSuperclass() != null
					&& !JavaTypes.OBJECT.equals(loadingClass.getSuperclass().getName())) {
				LOGGER.info("SuperClass: " + loadingClass.getSuperclass().getName());

				ClassDataStorage.getInstance().addSuperclassToLoad(loadingClass.getSuperclass().getName());
				newClassData.setSuperClass(createClassHierachie(loadingClass.getSuperclass()));
			}

			ClassDataStorage.getInstance().addClassData(className, newClassData);

			return newClassData;
		}

		return classData;
	}

	private void analyseMethod(MethodInfo method, List<Instruction> instructions, ClassData classData,
			MethodAnalyser methodAnalyser) {

		if (!JavaTypes.OBJECT_STANDARD_METHODS.contains(method.getName())
				&& (AccessFlag.SYNTHETIC & method.getAccessFlags()) == 0
				&& !AccessFlag.isPrivate(method.getAccessFlags())) {

			Wrapper<FieldData> fieldWrapper = new Wrapper<>();

			MethodData methodData = methodAnalyser.analyse(method.getName(), method.getDescriptor(),
					method.getAccessFlags(), instructions, fieldWrapper);

			if (methodData != null) {
				classData.addMethod(methodData, fieldWrapper.getValue());
			}

		}
	}

	private void analyseConstructor(MethodInfo method, List<Instruction> instructions, //
			ClassData classData, MethodAnalyser methodAnalyser) {

		if ((!classData.hasDefaultConstructor()) && AccessFlag.isPublic(method.getAccessFlags())) {
			List<Instruction> filteredInstructions = instructions
					.stream().filter(inst -> Opcode.PUTFIELD == inst.getOpcode()
							|| Opcode.INVOKEVIRTUAL == inst.getOpcode() || Opcode.INVOKESPECIAL == inst.getOpcode())
					.collect(Collectors.toList());

			Map<Integer, FieldData> constructorInitalizedFields = methodAnalyser
					.analyseConstructor(method.getDescriptor(), filteredInstructions, instructions);

			if (constructorInitalizedFields.isEmpty()) {
				classData.setDefaultConstructor(true);
			} else if (classData.getConstructor() == null) {
				ConstructorData constructor = new ConstructorData(constructorInitalizedFields);
				classData.setConstructor(constructor);
			}

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

	private static boolean isSynthetic(int modifier) {
		return (modifier & AccessFlag.SYNTHETIC) != 0;
	}

	private static void checkIsInnerClass(ClassFile classFile, ClassData classData) {
		InnerClassesAttribute innerClasses = (InnerClassesAttribute) classFile.getAttribute(InnerClassesAttribute.tag);

		if (innerClasses != null) {
			int index = innerClasses.find(classFile.getName());

			if (index != -1) {
				classData.setOuterClass(innerClasses.outerClass(index));
			}

		}
	}
}
