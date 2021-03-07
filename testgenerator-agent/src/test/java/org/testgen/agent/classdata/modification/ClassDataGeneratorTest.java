package org.testgen.agent.classdata.modification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testgen.agent.classdata.TestHelper;
import org.testgen.agent.classdata.model.ClassData;
import org.testgen.agent.classdata.model.ConstructorData;
import org.testgen.agent.classdata.model.FieldData;
import org.testgen.agent.classdata.model.MethodData;
import org.testgen.agent.classdata.model.MethodType;
import org.testgen.agent.classdata.model.SignatureData;
import org.testgen.agent.classdata.testclasses.Adresse;
import org.testgen.agent.classdata.testclasses.BlObject;
import org.testgen.agent.classdata.testclasses.Person.Geschlecht;
import org.testgen.runtime.classdata.access.ClassDataAccess;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class ClassDataGeneratorTest extends TestHelper {

	@Test
	public void testAddWithConstructorAndSuperClass()
			throws CannotCompileException, BadBytecode, FileNotFoundException, IOException, NotFoundException {
		init("org.testgen.agent.classdata.testclasses.Adresse");

		ClassData classData = prepareAdresseClassData();

		ClassDataGenerator generator = new ClassDataGenerator(classData);
		try {
			generator.generate(ctClass);

			Class<?> testClass = ClassPool.getDefault().toClass(ctClass);

			org.testgen.runtime.classdata.model.ClassData runtimeClassData = ClassDataAccess.getClassData(testClass);

			compareClassDataAdresse(runtimeClassData);
		} catch (ClassNotFoundException | BadBytecode | CannotCompileException | IOException e) {
			Assertions.fail();
		}
	}

	@Test
	public void testAddWithDefaultConstructor() throws NotFoundException, CannotCompileException, BadBytecode {
		init("org.testgen.agent.classdata.testclasses.Person");

		ClassData classData = preparePersonClassData();

		ClassDataGenerator generator = new ClassDataGenerator(classData);
		try {
			generator.generate(ctClass);

			Class<?> testClass = ClassPool.getDefault().toClass(ctClass);

			org.testgen.runtime.classdata.model.ClassData runtimeClassData = ClassDataAccess.getClassData(testClass);

			compareClassDataPerson(runtimeClassData);
		} catch (ClassNotFoundException | BadBytecode | CannotCompileException | IOException e) {
			Assertions.fail(e.getMessage());
		}
	}

	private ClassData prepareAdresseClassData() {
		FieldData fieldStrasse = new FieldData.Builder().withName("strasse").withDataType("java.lang.String").build();
		FieldData fieldHausnummer = new FieldData.Builder().withName("hausnummer").withDataType("short").build();
		FieldData fieldOrt = new FieldData.Builder().withName("ort").withDataType("java.lang.String").build();
		FieldData fieldPlz = new FieldData.Builder().withName("plz").withDataType("int").build();

		ConstructorData constructor = new ConstructorData(false);
		constructor.addConstructorElement(0, fieldStrasse);
		constructor.addConstructorElement(1, fieldHausnummer);
		constructor.addConstructorElement(2, fieldOrt);
		constructor.addConstructorElement(3, fieldPlz);

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Adresse");
		classData.setSuperClass(new ClassData("org.testgen.agent.classdata.testclasses.BlObject"));
		classData.setConstructor(constructor);

		classData.addFields(Arrays.asList(fieldStrasse, fieldHausnummer, fieldOrt, fieldPlz));

		MethodData methodSetStrasse = new MethodData("setStrasse", "(Ljava/lang/String;)V",
				MethodType.REFERENCE_VALUE_SETTER, false);
		classData.addMethod(methodSetStrasse, fieldStrasse);

		MethodData methodSetHausnummer = new MethodData("setHausnummer", "(S)V", MethodType.REFERENCE_VALUE_SETTER,
				false);
		classData.addMethod(methodSetHausnummer, fieldHausnummer);

		MethodData methodSetOrt = new MethodData("setOrt", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER,
				false);
		classData.addMethod(methodSetOrt, fieldOrt);

		MethodData methodSetPlz = new MethodData("setPlz", "(I)V", MethodType.REFERENCE_VALUE_SETTER, false);
		classData.addMethod(methodSetPlz, fieldPlz);

		return classData;
	}

	private ClassData preparePersonClassData() {
		SignatureData adressenList = new SignatureData("Ljava/util/List;");
		adressenList.addSubType(new SignatureData("Lorg/testgen/agent/classdata/testclasses/Adresse;"));

		FieldData fieldName = new FieldData.Builder().withName("name").withDataType("java.lang.String").build();
		FieldData fieldFirstName = new FieldData.Builder().withName("firstName").withDataType("java.lang.String")
				.build();
		FieldData fieldDateOfBirth = new FieldData.Builder().withName("dateOfBirth").withDataType("java.time.LocalDate")
				.build();
		FieldData fieldGeschlecht = new FieldData.Builder().withName("geschlecht")
				.withDataType("org.testgen.agent.classdata.testclasses.Person$Geschlecht").build();
		FieldData fieldAdressen = new FieldData.Builder().withName("adressen").withDataType("java.util.List")
				.withSignature(adressenList).build();

		ClassData classData = new ClassData("org.testgen.agent.classdata.testclasses.Person");
		classData.setDefaultConstructor(true);
		classData.addFields(Arrays.asList(fieldName, fieldFirstName, fieldDateOfBirth, fieldGeschlecht, fieldAdressen));

		MethodData methodSetName = new MethodData("setName", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER,
				false);
		classData.addMethod(methodSetName, fieldName);

		MethodData methodSetVorname = new MethodData("setVorname", "(Ljava/lang/String;)V",
				MethodType.REFERENCE_VALUE_SETTER, false);
		classData.addMethod(methodSetVorname, fieldFirstName);

		MethodData methodSetDateOfBirth = new MethodData("setDateOfBirth", "(Ljava/time/LocalDate;)V",
				MethodType.REFERENCE_VALUE_SETTER, false);
		classData.addMethod(methodSetDateOfBirth, fieldDateOfBirth);

		MethodData methodSetGeschlecht = new MethodData("setGeschlecht",
				"(Lorg/testgen/agent/classdata/testclasses/Person$Geschlecht;)V", MethodType.REFERENCE_VALUE_SETTER,
				false);
		classData.addMethod(methodSetGeschlecht, fieldGeschlecht);

		MethodData methodAddAdresse = new MethodData("addAdresse",
				"(Lorg/testgen/agent/classdata/testclasses/Adresse;)V", MethodType.COLLECTION_SETTER, false);
		classData.addMethod(methodAddAdresse, fieldAdressen);

		// normally this method has the methodType IMMUTABLE_GETTER, but for this test
		// change it to REFERENCE_VALUE_GETTER
		MethodData methodGetAdressen = new MethodData("getAdressen", "()Ljava/util/List;",
				MethodType.REFERENCE_VALUE_GETTER, false);
		classData.addMethod(methodGetAdressen, fieldAdressen);

		return classData;
	}

	private void compareClassDataAdresse(org.testgen.runtime.classdata.model.ClassData adresse) {

		org.testgen.runtime.classdata.model.FieldData fieldStrasse = new org.testgen.runtime.classdata.model.FieldData(
				false, "strasse", String.class);
		compareFields(fieldStrasse, adresse.getFieldInHierarchie(fieldStrasse));

		org.testgen.runtime.classdata.model.FieldData fieldHausnummer = new org.testgen.runtime.classdata.model.FieldData(
				false, "hausnummer", short.class);
		compareFields(fieldHausnummer, adresse.getFieldInHierarchie(fieldHausnummer));

		org.testgen.runtime.classdata.model.FieldData fieldOrt = new org.testgen.runtime.classdata.model.FieldData(
				false, "ort", String.class);
		compareFields(fieldOrt, adresse.getFieldInHierarchie(fieldOrt));

		org.testgen.runtime.classdata.model.FieldData fieldPlz = new org.testgen.runtime.classdata.model.FieldData(
				false, "plz", int.class);
		compareFields(fieldPlz, adresse.getFieldInHierarchie(fieldPlz));

		org.testgen.runtime.classdata.model.ConstructorData constructor = new org.testgen.runtime.classdata.model.ConstructorData(
				false);
		constructor.addElement(0, fieldStrasse);
		constructor.addElement(1, fieldHausnummer);
		constructor.addElement(2, fieldOrt);
		constructor.addElement(3, fieldPlz);
		compareConstructors(constructor, adresse.getConstructor());

		org.testgen.runtime.classdata.model.ClassData classData = new org.testgen.runtime.classdata.model.ClassData(
				"org.testgen.agent.classdata.testclasses.Adresse", //
				() -> ClassDataAccess.getClassData(BlObject.class), null, constructor);
		compareClasses(classData, adresse);

		SetterMethodData setterStrasse = new SetterMethodData("setStrasse", "(Ljava/lang/String;)V", false);
		compareSetterMethods(setterStrasse, adresse.getSetterInHierarchie(fieldStrasse));

		SetterMethodData setterHausnummer = new SetterMethodData("setHausnummer", "(S)V", false);
		compareSetterMethods(setterHausnummer, adresse.getSetterInHierarchie(fieldHausnummer));

		SetterMethodData setterOrt = new SetterMethodData("setOrt", "(Ljava/lang/String;)V", false);
		compareSetterMethods(setterOrt, adresse.getSetterInHierarchie(fieldOrt));

		SetterMethodData setterPlz = new SetterMethodData("setPlz", "(I)V", false);
		compareSetterMethods(setterPlz, adresse.getSetterInHierarchie(fieldPlz));

	}

	private void compareClassDataPerson(org.testgen.runtime.classdata.model.ClassData person) {

		org.testgen.runtime.classdata.model.FieldData fieldName = new org.testgen.runtime.classdata.model.FieldData(
				false, "name", String.class);
		compareFields(fieldName, person.getFieldInHierarchie(fieldName));

		org.testgen.runtime.classdata.model.FieldData fieldVorname = new org.testgen.runtime.classdata.model.FieldData(
				false, "firstName", String.class);
		compareFields(fieldVorname, person.getFieldInHierarchie(fieldVorname));

		org.testgen.runtime.classdata.model.FieldData fieldDateOfBirth = new org.testgen.runtime.classdata.model.FieldData(
				false, "dateOfBirth", LocalDate.class);
		compareFields(fieldDateOfBirth, person.getFieldInHierarchie(fieldDateOfBirth));

		org.testgen.runtime.classdata.model.FieldData fieldGeschlecht = new org.testgen.runtime.classdata.model.FieldData(
				false, "geschlecht", Geschlecht.class);
		compareFields(fieldGeschlecht, person.getFieldInHierarchie(fieldGeschlecht));

		org.testgen.runtime.classdata.model.descriptor.SignatureType adressenSignature = new org.testgen.runtime.classdata.model.descriptor.SignatureType(
				List.class);
		adressenSignature.addSubType(new org.testgen.runtime.classdata.model.descriptor.SignatureType(Adresse.class));

		org.testgen.runtime.classdata.model.FieldData fieldAdressen = new org.testgen.runtime.classdata.model.FieldData(
				false, "adressen", List.class);
		fieldAdressen.setSignature(adressenSignature);
		compareFields(fieldAdressen, person.getFieldInHierarchie(fieldAdressen));

		org.testgen.runtime.classdata.model.ConstructorData constructor = new org.testgen.runtime.classdata.model.ConstructorData(
				true);
		compareConstructors(constructor, person.getConstructor());

		org.testgen.runtime.classdata.model.ClassData classData = new org.testgen.runtime.classdata.model.ClassData(
				"org.testgen.agent.classdata.testclasses.Person", constructor);
		compareClasses(classData, person);

		SetterMethodData setterName = new SetterMethodData("setName", "(Ljava/lang/String;)V", false);
		compareSetterMethods(setterName, person.getSetterInHierarchie(fieldName));

		SetterMethodData setterVorname = new SetterMethodData("setVorname", "(Ljava/lang/String;)V", false);
		compareSetterMethods(setterVorname, person.getSetterInHierarchie(fieldVorname));

		SetterMethodData setterDateOfBirth = new SetterMethodData("setDateOfBirth", "(Ljava/time/LocalDate;)V", false);
		compareSetterMethods(setterDateOfBirth, person.getSetterInHierarchie(fieldDateOfBirth));

		SetterMethodData setterGeschlecht = new SetterMethodData("setGeschlecht",
				"(Lorg/testgen/agent/classdata/testclasses/Person$Geschlecht;)V", false);
		compareSetterMethods(setterGeschlecht, person.getSetterInHierarchie(fieldGeschlecht));

		SetterMethodData setterAdresse = new SetterMethodData("addAdresse",
				"(Lorg/testgen/agent/classdata/testclasses/Adresse;)V", false, SetterType.COLLECTION_SETTER);
		compareSetterMethods(setterAdresse, person.getSetterInHierarchie(fieldAdressen));

	}

	private void compareClasses(org.testgen.runtime.classdata.model.ClassData expected,
			org.testgen.runtime.classdata.model.ClassData actual) {
		Assertions.assertEquals(expected.getName(), actual.getName());
		Assertions.assertEquals(expected.getSuperclass(), actual.getSuperclass());
	}

	private void compareConstructors(org.testgen.runtime.classdata.model.ConstructorData expected,
			org.testgen.runtime.classdata.model.ConstructorData actual) {
		Assertions.assertEquals(expected.hasDefaultConstructor(), actual.hasDefaultConstructor());
		Assertions.assertEquals(expected.getConstructorFields(), actual.getConstructorFields());
	}

	private void compareFields(org.testgen.runtime.classdata.model.FieldData expected,
			org.testgen.runtime.classdata.model.FieldData actual) {
		Assertions.assertEquals(expected, actual);
		Assertions.assertEquals(expected.getSignature(), actual.getSignature());
	}

	private void compareSetterMethods(org.testgen.runtime.classdata.model.SetterMethodData expected,
			org.testgen.runtime.classdata.model.SetterMethodData actual) {
		Assertions.assertEquals(expected.getName(), actual.getName());
		Assertions.assertEquals(expected.getDescriptor(), actual.getDescriptor());
		Assertions.assertEquals(expected.getType(), actual.getType());
		Assertions.assertEquals(expected.isStatic(), actual.isStatic());
	}

}
