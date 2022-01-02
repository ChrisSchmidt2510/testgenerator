package org.testgen.agent.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Month;
import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.ClassDataStorage;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.modification.helper.CodeArrayModificator;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.BlObject;
import org.testgen.agent.classdata.testclasses.Person;
import org.testgen.agent.classdata.testclasses.Person.Geschlecht;
import org.testgen.config.TestgeneratorConfig;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;

public class ClassDataTransformerTest {

	private static Stream<Arguments> testModifyClassFile() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		CtClass classValueTransformer = classPool.get(ValueTrackerTransformer.class.getName());
		CtClass classMonth = classPool.get(Month.class.getName());
		CtClass classIllegalArgumentException = classPool.get(IllegalArgumentException.class.getName());
		CtClass classClassTransformer = classPool.get(ClassTransformer.class.getName());
		CtClass classAdresse = classPool.get(Adresse.class.getName());
		CtClass classBlObject = classPool.get(BlObject.class.getName());

		return Stream.of(Arguments.of(classValueTransformer.getName().replace(".", "/"), classValueTransformer, true),
				Arguments.of(classAdresse.getName().replace(".", "/"), classAdresse, true),
				Arguments.of(classBlObject.getName().replace(".", "/"), classBlObject, true),
				Arguments.of(classMonth.getName().replace(".", "/"), classMonth, false),
				Arguments.of(classIllegalArgumentException.getName().replace(".", "/"), classIllegalArgumentException,
						false),
				Arguments.of(classClassTransformer.getName().replace(".", "/"), classClassTransformer, false));
	}

	@ParameterizedTest
	@MethodSource
	public void testModifyClassFile(String className, CtClass ctClass, boolean result) {
		ClassDataTransformer transformer = new ClassDataTransformer();

		String blPackageProperty = "org/testgen/agent/transformer,java/time,java/lang";

		System.setProperty(TestgeneratorConfig.PARAM_BL_PACKAGE, blPackageProperty);
		System.setProperty(TestgeneratorConfig.PARAM_CLASS_NAMES, Adresse.class.getName().replace(".", "/"));
		ClassDataStorage.getInstance().addSuperclassToLoad(BlObject.class.getName());

		assertEquals(result, transformer.modifyClassFile(className, ctClass));
	}

	private static Stream<Arguments> testAnalyseClassHierarchie() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		return Stream.of(
				Arguments.of(classPool.get(CodeArrayModificator.class.getName()), classDataCodeArrayModificator()),
				Arguments.of(classPool.get(Person.class.getName()), classDataPerson()));
	}

	private static ClassData classDataCodeArrayModificator() {
		String className = CodeArrayModificator.class.getName();

		ClassData classData = new ClassData(className);

		SignatureData signature = new SignatureData(JVMTypes.LIST);
		signature.addSubType(new SignatureData(
				"Lorg/testgen/agent/classdata/modification/helper/CodeArrayModificator$CodeArrayModificatorModel;"));

		FieldData codeArrayModificatorModel = new FieldData.Builder().withName("codeArrayModificatorModel")
				.withDataType(JavaTypes.LIST).withModifier(Modifier.PRIVATE | Modifier.FINAL).withSignature(signature)
				.build();

		classData.addFields(Arrays.asList(codeArrayModificatorModel));

		ClassData classDataModel = new ClassData(
				"org.testgen.agent.classdata.modification.helper.CodeArrayModificator$CodeArrayModificatorModel");

		FieldData codeArrayStartIndex = new FieldData.Builder().withName("codeArrayStartIndex")
				.withDataType(Primitives.JAVA_INT).withModifier(Modifier.FINAL).build();
		FieldData modificator = new FieldData.Builder().withName("modificator").withDataType(Primitives.JAVA_INT)
				.withModifier(Modifier.FINAL).build();
		FieldData parent = new FieldData.Builder().withName("this$0").withDataType(className)
				.withModifier(Modifier.FINAL | AccessFlag.SYNTHETIC).build();

		classDataModel.addFields(Arrays.asList(codeArrayStartIndex, modificator, parent));
		classDataModel.setOuterClass(className);

		classData.addInnerClass(classDataModel);

		return classData;
	}

	private static ClassData classDataPerson() {
		ClassData classData = new ClassData(Person.class.getName());

		FieldData name = new FieldData.Builder().withName("name").withDataType(JavaTypes.STRING)
				.withModifier(Modifier.PRIVATE).build();
		FieldData firstName = new FieldData.Builder().withName("firstName").withDataType(JavaTypes.STRING)
				.withModifier(Modifier.PRIVATE).build();
		FieldData dateOfBirth = new FieldData.Builder().withName("dateOfBirth").withDataType("java.time.LocalDate")
				.withModifier(Modifier.PRIVATE).build();
		FieldData geschlecht = new FieldData.Builder().withName("geschlecht").withDataType(Geschlecht.class.getName())
				.withModifier(Modifier.PRIVATE).build();

		SignatureData signatureAdresse = new SignatureData(JVMTypes.LIST);
		signatureAdresse.addSubType(new SignatureData("Lorg/testgen/agent/classdata/testclasses/Adresse;"));

		FieldData adressen = new FieldData.Builder().withName("adressen").withDataType(JavaTypes.LIST)
				.withSignature(signatureAdresse).withModifier(Modifier.PRIVATE).build();
		classData.addFields(Arrays.asList(name, firstName, dateOfBirth, geschlecht, adressen));

		ClassData classGeschlecht = new ClassData(Geschlecht.class.getName());
		classGeschlecht.setOuterClass(Person.class.getName());
		classData.addInnerClass(classGeschlecht);

		ClassData classDataBlObject = new ClassData(BlObject.class.getName());

		FieldData erdat = new FieldData.Builder().withName("erdat").withDataType("java.time.LocalDate")
				.withModifier(Modifier.PRIVATE).build();
		FieldData ersb = new FieldData.Builder().withName("ersb").withDataType(JavaTypes.STRING)
				.withModifier(Modifier.PRIVATE).build();
		FieldData aedat = new FieldData.Builder().withName("aedat").withDataType("java.time.LocalDate")
				.withModifier(Modifier.PRIVATE).build();
		FieldData aesb = new FieldData.Builder().withName("aesb").withDataType(JavaTypes.STRING)
				.withModifier(Modifier.PRIVATE).build();

		classDataBlObject.addFields(Arrays.asList(erdat, ersb, aedat, aesb));
		classData.setSuperClass(classDataBlObject);

		return classData;
	}

	@ParameterizedTest
	@MethodSource
	public void testAnalyseClassHierarchie(CtClass ctClass, ClassData expected) throws NotFoundException {
		ClassDataTransformer transformer = new ClassDataTransformer();

		compareClassData(expected, transformer.analyseClassHierachie(ctClass));
	}

	private void compareClassData(ClassData expected, ClassData result) {
		assertEquals(expected.getName(), result.getName());

		assertEquals(expected.getFields().size(), result.getFields().size());

		for (FieldData expectedField : expected.getFields()) {
			FieldData resultField = result.getField(expectedField.getName(), expectedField.getDataType());

			assertNotNull(resultField);
			assertEquals(expectedField.getSignature(), resultField.getSignature(), expectedField.getName());
			assertEquals(expectedField.getModifier(), resultField.getModifier(), expectedField.getName());
		}

		if (expected.getSuperClass() != null) {
			assertNotNull(result.getSuperClass());
			compareClassData(expected.getSuperClass(), result.getSuperClass());
		}

		assertEquals(expected.getOuterClass(), result.getOuterClass());

		assertEquals(expected.getInnerClasses().size(), result.getInnerClasses().size(), expected.getName());

		for (int i = 0; i < expected.getInnerClasses().size(); i++) {
			compareClassData(expected.getInnerClasses().get(i), result.getInnerClasses().get(i));
		}

	}
}
