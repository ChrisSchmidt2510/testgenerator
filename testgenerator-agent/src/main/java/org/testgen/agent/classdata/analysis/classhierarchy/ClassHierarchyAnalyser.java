package org.testgen.agent.classdata.analysis.classhierarchy;

import java.util.ArrayList;
import java.util.List;

import org.testgen.agent.classdata.analysis.signature.SignatureParser;
import org.testgen.agent.classdata.analysis.signature.SignatureParserException;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Modifiers;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.ClassDataStorage;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.logging.LogManager;
import org.testgen.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.SignatureAttribute;

public class ClassHierarchyAnalyser {

	private Logger LOGGER = LogManager.getLogger(ClassHierarchyAnalyser.class);

	public ClassData analyseHierarchy(CtClass loadingClass) throws NotFoundException {
		String className = loadingClass.getName();

		ClassData classData = ClassDataStorage.getInstance().getClassData(className);

		if (classData != null)
			return classData;

		ClassData newClassData = new ClassData(className);

		LOGGER.info("ClassName: " + className);
		newClassData.addFields(analyseFields(loadingClass.getClassFile()));

		String superClassName = loadingClass.getSuperclass() != null ? loadingClass.getSuperclass().getName() : null;

		if (!JavaTypes.OBJECT.equals(superClassName) && !JavaTypes.ENUM.equals(superClassName)) {
			LOGGER.info("SuperClass: " + loadingClass.getSuperclass().getName());

			ClassDataStorage.getInstance().addSuperclassToLoad(loadingClass.getSuperclass().getName());
			newClassData.setSuperClass(analyseHierarchy(loadingClass.getSuperclass()));
		}

		analyseInnerClasses(loadingClass, newClassData);

		for (CtClass interfaceClass : loadingClass.getInterfaces()) {
			analyseInterface(interfaceClass, newClassData);
		}

		ClassDataStorage.getInstance().addClassData(className, newClassData);

		return newClassData;
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
				newClassData.addInnerClass(analyseHierarchy(classPool.get(innerClass)));
			}

		}
	}

	private List<String> getInnerClasses(InnerClassesAttribute innerClassesAtt, String className) {
		int length = innerClassesAtt.tableLength();

		List<String> innerClasses = new ArrayList<>();

		for (int i = 0; i < length; i++) {
			if (!innerClassesAtt.innerClass(i).equals(className))
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
}
