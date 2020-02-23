package de.nvg.javaagent.transformer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.nvg.javaagent.AgentException;
import de.nvg.javaagent.classdata.Instruction;
import de.nvg.javaagent.classdata.Instructions;
import de.nvg.javaagent.classdata.analysis.MethodAnalyser;
import de.nvg.javaagent.classdata.model.ClassData;
import de.nvg.javaagent.classdata.model.ClassDataStorage;
import de.nvg.javaagent.classdata.model.ConstructorData;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.model.MethodData;
import de.nvg.javaagent.classdata.modification.MetaDataAdder;
import de.nvg.javaagent.classdata.modification.fields.FieldTypeChanger;
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

		if (className.startsWith(properties.getBlPackage())
				|| ClassDataStorage.getInstance().containsSuperclassToLoad(Descriptor.toJavaName(className))
				|| properties.getClassName().equals(className)) {

			LOGGER.info("ClassName: " + className);

			ClassDataStorage.getInstance().removeSuperclassToLoad(className);

			final ClassPool pool = ClassPool.getDefault();

			try {
				CtClass loadingClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

				ClassData classData = new ClassData(loadingClass.getName());

				byte[] bytecode = collectAndAlterMetaData(loadingClass, classData);

				ClassDataStorage.getInstance().addClassData(loadingClass.getName(), classData);

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

	private byte[] collectAndAlterMetaData(CtClass loadingClass, ClassData classData) throws Exception {
		ClassFile classFile = loadingClass.getClassFile();

		String superClass = classFile.getSuperclass();

		if (!JavaTypes.OBJECT.equals(superClass)) {
			ClassData superClassData = ClassDataStorage.getInstance().getClassData(superClass);

			if (superClassData == null) {
				ClassDataStorage.getInstance().addSuperclassToLoad(superClass);
			}

			LOGGER.info("Superclass: " + superClass);
			classData.setSuperClass(superClass);
		}

		ConstPool constantPool = classFile.getConstPool();

		if (Modifier.isEnum(loadingClass.getModifiers())) {
			LOGGER.info("isEnum: true");
			classData.setIsEnum(true);
		} else {
			List<FieldData> fields = getFieldsFromClass(loadingClass, classData);

			classData.addFields(fields);

			MethodAnalyser methodAnalyser = new MethodAnalyser(loadingClass.getName(), fields);

			FieldTypeChanger fieldTypeChanger = new FieldTypeChanger(fields, constantPool, //
					loadingClass);

			// only add the calledFields Set if the Flag is set
			if (properties.isTraceGetterCalls()) {
				fieldTypeChanger.addFieldCalledField();
			}

			checkAndAlterMethods(loadingClass, classFile.getMethods(), methodAnalyser, //
					fieldTypeChanger, classData);

			addMetaDataToClassFile(loadingClass, constantPool, classData);

		}

		byte[] bytecode = loadingClass.toBytecode();

		loadingClass.detach();

		return bytecode;
	}

	private List<FieldData> getFieldsFromClass(CtClass loadedClass, ClassData classData)
			throws CannotCompileException, NotFoundException {
		List<FieldData> fieldsFromClass = new ArrayList<>();

		for (CtField field : loadedClass.getDeclaredFields()) {

			if (!Instructions.isConstant(field.getModifiers())) {
				FieldInfo fieldInfo = field.getFieldInfo();

				SignatureAttribute signature = (SignatureAttribute) fieldInfo.getAttribute(SignatureAttribute.tag);

				FieldData fieldData = new FieldData.Builder()
						.withDataType(Descriptor.toClassName(fieldInfo.getDescriptor())).withName(field.getName())
						.isMutable(!Modifier.isFinal(fieldInfo.getAccessFlags()))
						.isStatic(Modifier.isStatic(fieldInfo.getAccessFlags()))
						.withSignature(signature != null ? signature.getSignature() : null).build();

				LOGGER.info("added Field: " + fieldData);

				fieldsFromClass.add(fieldData);

				FieldTypeChanger.changeFieldDataTypeToProxy(loadedClass.getClassFile(), fieldInfo);
			}
		}

		return fieldsFromClass;
	}

	private void checkAndAlterMethods(CtClass loadingClass, List<MethodInfo> methods, MethodAnalyser methodAnalyser,
			FieldTypeChanger fieldTypeChanger, ClassData classData) throws Exception {

		for (int i = 0; i < methods.size(); i++) {
			MethodInfo method = methods.get(i);

			LOGGER.info("Starte Transformation fuer die Methode " + method.getName() + method.getDescriptor());

			if (MethodInfo.nameInit.equals(method.getName())) {

				List<Instruction> instructions = Instructions.getAllInstructions(method);

				Map<Integer, List<Instruction>> filteredInstructions = Instructions.getFilteredInstructions(
						instructions, Arrays.asList(Opcode.ALOAD_0, Opcode.PUTFIELD, Opcode.RETURN));

				Map<Integer, Instruction> aloadPutFieldInstructionPairs = createAload0PutFieldInstructionPairs(
						filteredInstructions.get(Opcode.ALOAD_0), filteredInstructions.get(Opcode.PUTFIELD));

				fieldTypeChanger.changeFieldInitialization(instructions, aloadPutFieldInstructionPairs,
						method.getCodeAttribute());

				if (!classData.hasDefaultConstructor() && AccessFlag.isPublic(method.getAccessFlags())) {

					Map<Integer, FieldData> constructorInitalizedFields = methodAnalyser.analyseConstructor(
							method.getDescriptor(), filteredInstructions.get(Opcode.PUTFIELD), instructions);

					if (constructorInitalizedFields.isEmpty()) {
						classData.setDefaultConstructor(true);
					} else {
						ConstructorData constructor = new ConstructorData(constructorInitalizedFields);
						classData.setConstructor(constructor);
					}
				}

			} else if (MethodInfo.nameClinit.equals(method.getName())) {

			} else {

				List<Instruction> instructions = Instructions.getAllInstructions(method);

				Map<Integer, List<Instruction>> filteredInstructions = Instructions
						.getFilteredInstructions(instructions, Arrays.asList(Opcode.PUTFIELD, Opcode.GETFIELD));

				fieldTypeChanger.overrideFieldAccess(filteredInstructions, instructions, //
						method.getCodeAttribute());

				if (!MethodInfo.nameInit.equals(method.getName())
						&& !JavaTypes.OBJECT_STANDARD_METHODS.contains(method.getName())
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

	private static Map<Integer, Instruction> createAload0PutFieldInstructionPairs(List<Instruction> aloadInstructions,
			List<Instruction> putFieldInstructions) {
		Map<Integer, Instruction> map = new LinkedHashMap<>();

		if (putFieldInstructions != null && !putFieldInstructions.isEmpty()) {
			for (int i = 0; i < putFieldInstructions.size(); i++) {
				Instruction instruction = putFieldInstructions.get(i);

				map.put(aloadInstructions.get(i + 1).getCodeArrayIndex(), instruction);
			}
		}

		return map;

	}
}
