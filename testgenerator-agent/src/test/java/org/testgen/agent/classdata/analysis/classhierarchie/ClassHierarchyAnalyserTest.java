package org.testgen.agent.classdata.analysis.classhierarchie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testgen.agent.classdata.analysis.classhierarchy.ClassHierarchyAnalyser;
import org.testgen.agent.classdata.constants.JVMTypes;
import org.testgen.agent.classdata.constants.JavaTypes;
import org.testgen.agent.classdata.constants.Primitives;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.modification.helper.CodeArrayModificator;
import org.testgen.agent.classdata.testclasses.BlObject;
import org.testgen.agent.classdata.testclasses.Person;
import org.testgen.agent.classdata.testclasses.Person.Geschlecht;
import org.testgen.agent.classdata.testclasses.SerializationHelper;
import org.testgen.agent.classdata.testclasses.SerializationHelper.A;
import org.testgen.agent.classdata.testclasses.SerializationHelper.Test;
import org.testgen.runtime.classdata.ClassDataHolder;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;

public class ClassHierarchyAnalyserTest {

	private static Stream<Arguments> testAnalyseHierarchy() throws NotFoundException {
		ClassPool classPool = ClassPool.getDefault();

		return Stream.of(
				Arguments.of(classPool.get(CodeArrayModificator.class.getName()), classDataCodeArrayModificator()),
				Arguments.of(classPool.get(Person.class.getName()), classDataPerson()),
				Arguments.of(classPool.get(A.class.getName()), classDataA()));
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

		ClassData classDataLookup = new ClassData(Lookup.class.getName());

		FieldData lookupClass = new FieldData.Builder().withName("lookupClass").withDataType(JavaTypes.CLASS)
				.withSignature(new SignatureData("Ljava/lang/Class;")).withModifier(Modifier.PRIVATE | Modifier.FINAL)
				.build();
		FieldData allowedModes = new FieldData.Builder().withName("allowedModes").withDataType(Primitives.JAVA_INT)
				.withModifier(Modifier.PRIVATE | Modifier.FINAL).build();

		SignatureData signatureConcurrentMap = new SignatureData("Ljava/util/concurrent/ConcurrentHashMap;");
		signatureConcurrentMap.addSubTypes(Arrays.asList(new SignatureData("Ljava/lang/invoke/MemberName;"),
				new SignatureData("Ljava/lang/invoke/DirectMethodHandle;")));

		FieldData lookasideTable = new FieldData.Builder().withName("LOOKASIDE_TABLE")
				.withDataType(ConcurrentHashMap.class.getName()).withSignature(signatureConcurrentMap)
				.withModifier(Modifier.STATIC).build();

		classDataLookup.addFields(Arrays.asList(lookupClass, allowedModes, lookasideTable));
		classDataLookup.setOuterClass(MethodHandles.class.getName());

		ClassData classDataMemberNameFactory = new ClassData("java.lang.invoke.MemberName$Factory");

		FieldData instance = new FieldData.Builder().withName("INSTANCE")
				.withDataType("java.lang.invoke.MemberName$Factory").withModifier(Modifier.STATIC).build();

		FieldData allowedFlags = new FieldData.Builder().withName("ALLOWED_FLAGS").withDataType(Primitives.JAVA_INT)
				.withModifier(Modifier.PRIVATE | Modifier.STATIC).build();

		classDataMemberNameFactory.addFields(Arrays.asList(instance, allowedFlags));
		classDataMemberNameFactory.setOuterClass("java.lang.invoke.MemberName");

		ClassData classDataConstants = new ClassData("java.lang.invoke.MethodHandleNatives$Constants");
		classDataConstants.setOuterClass("java.lang.invoke.MethodHandleNatives");

		classDataLookup.addInnerClass(classDataConstants);
		classDataLookup.addInnerClass(classDataMemberNameFactory);

		classData.addInnerClass(classDataLookup);

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
		classDataBlObject.addInterface(ClassDataHolder.class.getName());

		classData.setSuperClass(classDataBlObject);

		return classData;
	}

	private static ClassData classDataA() {
		ClassData classData = new ClassData(A.class.getName());

		FieldData parent = new FieldData.Builder().withName("this$0").withDataType(SerializationHelper.class.getName())
				.withModifier(Modifier.FINAL | AccessFlag.SYNTHETIC).build();

		classData.addFields(Arrays.asList(parent));

		classData.setOuterClass(SerializationHelper.class.getName());

		classData.addInterface(Test.class.getName());
		classData.addInterface(Serializable.class.getName());

		ClassData helperTest = new ClassData(Test.class.getName());
		helperTest.addInterface(Serializable.class.getName());
		helperTest.setOuterClass(SerializationHelper.class.getName());

		classData.addInnerClass(helperTest);

		return classData;
	}

	@ParameterizedTest
	@MethodSource
	public void testAnalyseHierarchy(CtClass ctClass, ClassData expected) throws NotFoundException {

		ClassHierarchyAnalyser classAnalyser = new ClassHierarchyAnalyser();

		compareClassData(expected, classAnalyser.analyseHierarchy(ctClass));
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

		for (ClassData expectedInnerclass : expected.getInnerClasses()) {
			ClassData resultInnerClass = result.getInnerClasses().stream()
					.filter(cd -> cd.getName().equals(expectedInnerclass.getName())).findAny()
					.orElseThrow(() -> new NoSuchElementException(expectedInnerclass.getName()));

			compareClassData(expectedInnerclass, resultInnerClass);
		}

		assertEquals(expected.getInterfaces(), result.getInterfaces());

	}
}
