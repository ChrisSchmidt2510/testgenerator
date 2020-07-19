package de.nvg.agent.classdata.modification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.testgen.runtime.classdata.ClassDataFactory;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;

import de.nvg.agent.classdata.TestHelper;
import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.ConstructorData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.MethodType;
import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.agent.classdata.testclasses.Adresse;
import de.nvg.agent.classdata.testclasses.BlObject;
import de.nvg.agent.classdata.testclasses.Person;
import de.nvg.agent.classdata.testclasses.Person.Geschlecht;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;

public class ClassDataGeneratorTest extends TestHelper {

	@Test
	public void testAddWithConstructorAndSuperClass()
			throws CannotCompileException, BadBytecode, FileNotFoundException, IOException, NotFoundException {
		init(Adresse.class);

		ClassData classData = prepareAdresseClassData();

		ClassLoader loader = this.getClass().getClassLoader();

		ClassDataGenerator generator = new ClassDataGenerator(classData, loader);
		try {
			generator.generate(ctClass);

			// normally this step is done by Adresse but Adresse dont get modified for this
			// test
			Class<?> loadClass = loader
					.loadClass("de.nvg.agent.classdata.testclasses.Adresse$$Testgenerator$ClassData");

			ClassDataFactory.getInstance().register(Adresse.class, loadClass);

			// normally this step is done than BlObject gets loaded into the jvm
			ClassDataFactory.getInstance().register(BlObject.class,
					de.nvg.agent.classdata.testclasses.BlObject$$Testgenerator$ClassData.class);

			org.testgen.runtime.classdata.model.ClassData runtimeClassData = ClassDataFactory.getInstance()
					.getClassData(Adresse.class);

			compareClassDataAdresse(runtimeClassData);
		} catch (ClassNotFoundException | BadBytecode | CannotCompileException | IOException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAddWithDefaultConstructor() throws NotFoundException, CannotCompileException, BadBytecode {
		init(Person.class);

		ClassData classData = preparePersonClassData();

		ClassLoader loader = this.getClass().getClassLoader();

		ClassDataGenerator generator = new ClassDataGenerator(classData, loader);
		try {
			generator.generate(ctClass);

			// normally this step is done by Person but Person dont get modified for this
			// test
			Class<?> loadClass = loader.loadClass("de.nvg.agent.classdata.testclasses.Person$$Testgenerator$ClassData");

			ClassDataFactory.getInstance().register(Person.class, loadClass);

			org.testgen.runtime.classdata.model.ClassData runtimeClassData = ClassDataFactory.getInstance()
					.getClassData(Person.class);

			compareClassDataPerson(runtimeClassData);
		} catch (ClassNotFoundException | BadBytecode | CannotCompileException | IOException e) {
			Assert.fail();
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

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.Adresse");
		classData.setSuperClass(new ClassData("de.nvg.agent.classdata.testclasses.BlObject"));
		classData.setConstructor(constructor);

		classData.addFields(Arrays.asList(fieldStrasse, fieldHausnummer, fieldOrt, fieldPlz));

		MethodData methodSetStrasse = new MethodData("setStrasse", "(Ljava/lang/String;)V",
				MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetStrasse, fieldStrasse);

		MethodData methodSetHausnummer = new MethodData("setHausnummer", "(S)V", MethodType.REFERENCE_VALUE_SETTER, 0,
				false);
		classData.addMethod(methodSetHausnummer, fieldHausnummer);

		MethodData methodSetOrt = new MethodData("setOrt", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER,
				0, false);
		classData.addMethod(methodSetOrt, fieldOrt);

		MethodData methodSetPlz = new MethodData("setPlz", "(I)V", MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetPlz, fieldPlz);

		return classData;
	}

	private ClassData preparePersonClassData() {
		SignatureData adressenList = new SignatureData("Ljava/util/List;");
		adressenList.addSubType(new SignatureData("Lde/nvg/agent/classdata/testclasses/Adresse;"));

		FieldData fieldName = new FieldData.Builder().withName("name").withDataType("java.lang.String").build();
		FieldData fieldFirstName = new FieldData.Builder().withName("firstName").withDataType("java.lang.String")
				.build();
		FieldData fieldDateOfBirth = new FieldData.Builder().withName("dateOfBirth").withDataType("java.time.LocalDate")
				.build();
		FieldData fieldGeschlecht = new FieldData.Builder().withName("geschlecht")
				.withDataType("de.nvg.agent.classdata.testclasses.Person$Geschlecht").build();
		FieldData fieldAdressen = new FieldData.Builder().withName("adressen").withDataType("java.util.List")
				.withSignature(adressenList).build();

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.Person");
		classData.setDefaultConstructor(true);
		classData.addFields(Arrays.asList(fieldName, fieldFirstName, fieldDateOfBirth, fieldGeschlecht, fieldAdressen));

		MethodData methodSetName = new MethodData("setName", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER,
				0, false);
		classData.addMethod(methodSetName, fieldName);

		MethodData methodSetVorname = new MethodData("setVorname", "(Ljava/lang/String;)V",
				MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetVorname, fieldFirstName);

		MethodData methodSetDateOfBirth = new MethodData("setDateOfBirth", "(Ljava/time/LocalDate;)V",
				MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetDateOfBirth, fieldDateOfBirth);

		MethodData methodSetGeschlecht = new MethodData("setGeschlecht",
				"(Lde/nvg/agent/classdata/testclasses/Person$Geschlecht;)V", MethodType.REFERENCE_VALUE_SETTER, 0,
				false);
		classData.addMethod(methodSetGeschlecht, fieldGeschlecht);

		MethodData methodAddAdresse = new MethodData("addAdresse", "(Lde/nvg/agent/classdata/testclasses/Adresse;)V",
				MethodType.COLLECTION_SETTER, 0, false);
		classData.addMethod(methodAddAdresse, fieldAdressen);

		// normally this method has the methodType IMMUTABLE_GETTER, but for this test
		// change it to REFERENCE_VALUE_GETTER
		MethodData methodGetAdressen = new MethodData("getAdressen", "()Ljava/util/List;",
				MethodType.REFERENCE_VALUE_GETTER, 0, false);
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
				"de.nvg.agent.classdata.testclasses.Adresse",
				() -> ClassDataFactory.getInstance().getClassData(BlObject.class), null, constructor);
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
				"de.nvg.agent.classdata.testclasses.Person", constructor);
		compareClasses(classData, person);

		SetterMethodData setterName = new SetterMethodData("setName", "(Ljava/lang/String;)V", false);
		compareSetterMethods(setterName, person.getSetterInHierarchie(fieldName));

		SetterMethodData setterVorname = new SetterMethodData("setVorname", "(Ljava/lang/String;)V", false);
		compareSetterMethods(setterVorname, person.getSetterInHierarchie(fieldVorname));

		SetterMethodData setterDateOfBirth = new SetterMethodData("setDateOfBirth", "(Ljava/time/LocalDate;)V", false);
		compareSetterMethods(setterDateOfBirth, person.getSetterInHierarchie(fieldDateOfBirth));

		SetterMethodData setterGeschlecht = new SetterMethodData("setGeschlecht",
				"(Lde/nvg/agent/classdata/testclasses/Person$Geschlecht;)V", false);
		compareSetterMethods(setterGeschlecht, person.getSetterInHierarchie(fieldGeschlecht));

		SetterMethodData setterAdresse = new SetterMethodData("addAdresse",
				"(Lde/nvg/agent/classdata/testclasses/Adresse;)V", false, SetterType.COLLECTION_SETTER);
		compareSetterMethods(setterAdresse, person.getSetterInHierarchie(fieldAdressen));

	}

	private void compareClasses(org.testgen.runtime.classdata.model.ClassData expected,
			org.testgen.runtime.classdata.model.ClassData actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getSuperclass(), actual.getSuperclass());
	}

	private void compareConstructors(org.testgen.runtime.classdata.model.ConstructorData expected,
			org.testgen.runtime.classdata.model.ConstructorData actual) {
		Assert.assertEquals(expected.hasDefaultConstructor(), actual.hasDefaultConstructor());
		Assert.assertEquals(expected.getConstructorFields(), actual.getConstructorFields());
	}

	private void compareFields(org.testgen.runtime.classdata.model.FieldData expected,
			org.testgen.runtime.classdata.model.FieldData actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.getSignature(), actual.getSignature());
	}

	private void compareSetterMethods(org.testgen.runtime.classdata.model.SetterMethodData expected,
			org.testgen.runtime.classdata.model.SetterMethodData actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getDescriptor(), actual.getDescriptor());
		Assert.assertEquals(expected.getType(), actual.getType());
		Assert.assertEquals(expected.isStatic(), actual.isStatic());
	}

}
