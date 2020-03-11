package de.nvg.agent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.nvg.agent.AgentException;
import de.nvg.agent.classdata.analysis.MethodAnalyser;
import de.nvg.agent.classdata.instructions.Instruction;
import de.nvg.agent.classdata.instructions.Instructions;
import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.ClassDataStorage;
import de.nvg.agent.classdata.model.ConstructorData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.modification.MetaDataAdder;
import de.nvg.agent.classdata.modification.fields.FieldTypeChanger;
import de.nvg.testgenerator.Wrapper;
import de.nvg.testgenerator.classdata.constants.JavaTypes;
import de.nvg.testgenerator.logging.LogManager;
import de.nvg.testgenerator.logging.Logger;
import de.nvg.testgenerator.properties.AgentProperties;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import javassist.bytecode.SignatureAttribute;

public class ClassDataTransformer implements ClassFileTransformer {

	private static final Logger LOGGER = LogManager.getLogger(ClassDataTransformer.class);

	private final AgentProperties properties = AgentProperties.getInstance();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		if (properties.getBlPackage().stream().anyMatch(packageName -> className.startsWith(packageName))
				|| ClassDataStorage.getInstance().containsSuperclassToLoad(Descriptor.toJavaName(className))
				|| properties.getClassName().equals(className)) {

			ClassDataStorage.getInstance().removeSuperclassToLoad(Descriptor.toJavaName(className));

			final ClassPool pool = ClassPool.getDefault();

			try {
				CtClass loadingClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

				byte[] bytecode = collectAndAlterMetaData(loadingClass);

				try (FileOutputStream fos = new FileOutputStream(
						new File("D:\\" + className.substring(className.lastIndexOf('/')) + ".class"))) {
					fos.write(bytecode);
				}

				return bytecode;

			} catch (Exception e) {
				LOGGER.error(e);
				throw new AgentException("Es ist ein Fehler bei der Transfomation aufgetreten", e);
			}
		}

		return classfileBuffer;
	}

	private byte[] collectAndAlterMetaData(CtClass loadingClass) throws Exception {
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

			MethodAnalyser methodAnalyser = new MethodAnalyser(loadingClass.getName(), classData.getFields());

			FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(classData, constantPool, //
					loadingClass);

			// only add the calledFields Set if the Flag is set
			if (properties.isTraceReadFieldAccess()) {

				for (CtField field : loadingClass.getDeclaredFields()) {

					if (!Instructions.isConstant(field.getModifiers()) && !AccessFlag.isPublic(field.getModifiers())) {
						FieldTypeChanger.changeFieldDataTypeToProxy(classFile, field.getFieldInfo());
					}
				}

				fieldTypeChanger.addFieldCalledField();
			}

			analyseAndManipulateMethods(classFile.getMethods(), classData, methodAnalyser, fieldTypeChanger);

			addMetaDataToClassFile(loadingClass, constantPool, classData);

		}

		ClassDataStorage.getInstance().addClassData(loadingClass.getName(), classData);

		byte[] bytecode = loadingClass.toBytecode();

		loadingClass.detach();

		return bytecode;
	}

	private List<FieldData> analyseFields(ClassFile loadedClass) {
		List<FieldData> fieldsFromClass = new ArrayList<>();

		for (FieldInfo field : loadedClass.getFields()) {

			if (!Instructions.isConstant(field.getAccessFlags())) {

				SignatureAttribute signature = (SignatureAttribute) field.getAttribute(SignatureAttribute.tag);

				FieldData fieldData = new FieldData.Builder()
						.withDataType(Descriptor.toClassName(field.getDescriptor())).withName(field.getName())
						.isMutable(!Modifier.isFinal(field.getAccessFlags()))
						.isStatic(Modifier.isStatic(field.getAccessFlags()))
						.isPublic(Modifier.isPublic(field.getAccessFlags()))
						.withSignature(signature != null ? signature.getSignature() : null).build();

				LOGGER.info("added Field: " + fieldData);

				fieldsFromClass.add(fieldData);
			}
		}

		return fieldsFromClass;
	}

	private void analyseAndManipulateMethods(List<MethodInfo> methods, ClassData classData,
			MethodAnalyser methodAnalyser, FieldTypeChanger fieldTypeChanger) throws BadBytecode {

		for (MethodInfo method : methods) {
			if (!MethodInfo.nameClinit.equals(method.getName())) {

				List<Instruction> instructions = Instructions.getAllInstructions(method);
				analyseMethod(method, instructions, classData, methodAnalyser);

				if (properties.isTraceReadFieldAccess()) {
					manipulateMethod(method, instructions, fieldTypeChanger);
				}
			}
		}
	}

	private void addMetaDataToClassFile(CtClass loadingClass, ConstPool constantPool, ClassData classData)
			throws BadBytecode, CannotCompileException {

		ClassFile classFile = loadingClass.getClassFile();

		MethodInfo clinit = null;
		List<Instruction> instructions = null;

		clinit = classFile.getMethod(MethodInfo.nameClinit);

		if (clinit != null) {
			instructions = Instructions.getAllInstructions(clinit);
		} else {
			LOGGER.info("Erstelle " + MethodInfo.nameClinit + " für Klasse " + loadingClass.getName());

			clinit = new MethodInfo(constantPool, MethodInfo.nameClinit, "()V");

			CodeAttribute codeAttribute = new CodeAttribute(constantPool, 0, 0, new byte[0],
					new ExceptionTable(constantPool));

			clinit.setCodeAttribute(codeAttribute);
			clinit.setAccessFlags(Modifier.STATIC);

			classFile.addMethod(clinit);

			instructions = new ArrayList<>();
		}

		MetaDataAdder metaDataAdder = new MetaDataAdder(constantPool, loadingClass, classData);
		metaDataAdder.add(clinit.getCodeAttribute(), instructions);
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
			MethodAnalyser methodAnalyser) throws BadBytecode {

		if (MethodInfo.nameInit.equals(method.getName()) && !classData.hasDefaultConstructor()
				&& AccessFlag.isPublic(method.getAccessFlags())) {

			List<Instruction> filteredInstructions = instructions.stream()
					.filter(inst -> Opcode.PUTFIELD == inst.getOpcode()).collect(Collectors.toList());

			Map<Integer, FieldData> constructorInitalizedFields = methodAnalyser
					.analyseConstructor(method.getDescriptor(), filteredInstructions, instructions);

			if (constructorInitalizedFields.isEmpty()) {
				classData.setDefaultConstructor(true);
			} else {
				ConstructorData constructor = new ConstructorData(constructorInitalizedFields);
				classData.setConstructor(constructor);
			}

		} else if (!JavaTypes.OBJECT_STANDARD_METHODS.contains(method.getName())
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

	private void manipulateMethod(MethodInfo method, List<Instruction> instructions, FieldTypeChanger fieldTypeChanger)
			throws BadBytecode {

		LOGGER.info("Starte Transformation fuer die Methode " + method.getName() + method.getDescriptor());

		if (MethodInfo.nameInit.equals(method.getName())) {
			List<Instruction> filteredInstructions = instructions.stream()
					.filter(inst -> Opcode.PUTFIELD == inst.getOpcode()).collect(Collectors.toList());

			List<Instruction> putFieldInstructions = filteredInstructions == null ? Collections.emptyList()
					: filteredInstructions;

			CodeAttribute codeAttribute = method.getCodeAttribute();

			fieldTypeChanger.changeFieldInitialization(instructions, putFieldInstructions, codeAttribute);

		} else {
			Map<Integer, List<Instruction>> filteredInstructions = Instructions.getFilteredInstructions(instructions,
					Arrays.asList(Opcode.PUTFIELD, Opcode.GETFIELD));

			fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, //
					method.getCodeAttribute());
			method.rebuildStackMap(ClassPool.getDefault());
		}
	}
}
