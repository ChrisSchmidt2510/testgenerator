package de.nvg.agent.classdata.modification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.testgen.core.MethodHandles;
import org.testgen.core.TestgeneratorConstants;

import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.ConstructorData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.MethodType;
import de.nvg.agent.classdata.model.SignatureData;
import de.nvg.agent.classdata.testclasses.Adresse;
import de.nvg.agent.classdata.testclasses.BlObject;
import de.nvg.agent.classdata.testclasses.Person.Geschlecht;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.runtime.classdatamodel.SetterType;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;

public class MetaDataAdderTest {

	private CtClass testClass;
	private ClassPool classPool = ClassPool.getDefault();
	private ConstPool constantPool;
	private CodeAttribute codeAttribute;

	private void initTest(String className) throws NotFoundException, DuplicateMemberException {

		testClass = classPool.get(className);

		ClassFile classFile = testClass.getClassFile();
		constantPool = classFile.getConstPool();

		MethodInfo method = new MethodInfo(constantPool, MethodInfo.nameClinit, "()V");

		codeAttribute = new CodeAttribute(constantPool, 0, 0, new byte[0], new ExceptionTable(constantPool));

		method.setCodeAttribute(codeAttribute);
		method.setAccessFlags(Modifier.STATIC);

		classFile.addMethod(method);
	}

	@After
	public void shutdown() {
		testClass.detach();

		testClass = null;
	}

	@Test
	public void testAddWithConstructorAndSuperClass()
			throws CannotCompileException, BadBytecode, FileNotFoundException, IOException, NotFoundException {
		initTest("de.nvg.agent.classdata.testclasses.Adresse");

		ClassData classData = prepareAdresseClassData();

		MetaDataAdder metaDataAdder = new MetaDataAdder(constantPool, testClass, classData);
		metaDataAdder.add(codeAttribute, new ArrayList<>());

		Class<?> adresseClazz = classPool.toClass(testClass);

		de.nvg.runtime.classdatamodel.ClassData runtimeClassData = MethodHandles.getStaticFieldValue(adresseClazz,
				TestgeneratorConstants.FIELDNAME_CLASS_DATA);

		compareClassDataAdresse(runtimeClassData);
	}

	@Test
	public void testAddWithDefaultConstructor() throws NotFoundException, CannotCompileException, BadBytecode {
		initTest("de.nvg.agent.classdata.testclasses.Person");

		ClassData classData = preparePersonClassData();

		MetaDataAdder metaDataAdder = new MetaDataAdder(constantPool, testClass, classData);
		metaDataAdder.add(codeAttribute, new ArrayList<>());

		Class<?> adresseClazz = classPool.toClass(testClass);

		de.nvg.runtime.classdatamodel.ClassData runtimeClassData = MethodHandles.getStaticFieldValue(adresseClazz,
				TestgeneratorConstants.FIELDNAME_CLASS_DATA);

		compareClassDataPerson(runtimeClassData);
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

	private void compareClassDataAdresse(de.nvg.runtime.classdatamodel.ClassData adresse) {

		de.nvg.runtime.classdatamodel.FieldData fieldStrasse = new de.nvg.runtime.classdatamodel.FieldData(false,
				"strasse", String.class);
		compareFields(fieldStrasse, adresse.getFieldInHierarchie(fieldStrasse));

		de.nvg.runtime.classdatamodel.FieldData fieldHausnummer = new de.nvg.runtime.classdatamodel.FieldData(false,
				"hausnummer", short.class);
		compareFields(fieldHausnummer, adresse.getFieldInHierarchie(fieldHausnummer));

		de.nvg.runtime.classdatamodel.FieldData fieldOrt = new de.nvg.runtime.classdatamodel.FieldData(false, "ort",
				String.class);
		compareFields(fieldOrt, adresse.getFieldInHierarchie(fieldOrt));

		de.nvg.runtime.classdatamodel.FieldData fieldPlz = new de.nvg.runtime.classdatamodel.FieldData(false, "plz",
				int.class);
		compareFields(fieldPlz, adresse.getFieldInHierarchie(fieldPlz));

		de.nvg.runtime.classdatamodel.ConstructorData constructor = new de.nvg.runtime.classdatamodel.ConstructorData(
				false);
		constructor.addElement(0, fieldStrasse);
		constructor.addElement(1, fieldHausnummer);
		constructor.addElement(2, fieldOrt);
		constructor.addElement(3, fieldPlz);
		compareConstructors(constructor, adresse.getConstructor());

		de.nvg.runtime.classdatamodel.ClassData classData = new de.nvg.runtime.classdatamodel.ClassData(
				"de.nvg.agent.classdata.testclasses.Adresse",
				() -> MethodHandles.getStaticFieldValue(BlObject.class, TestgeneratorConstants.FIELDNAME_CLASS_DATA),
				null, constructor);
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

	private void compareClassDataPerson(de.nvg.runtime.classdatamodel.ClassData person) {

		de.nvg.runtime.classdatamodel.FieldData fieldName = new de.nvg.runtime.classdatamodel.FieldData(false, "name",
				String.class);
		compareFields(fieldName, person.getFieldInHierarchie(fieldName));

		de.nvg.runtime.classdatamodel.FieldData fieldVorname = new de.nvg.runtime.classdatamodel.FieldData(false,
				"firstName", String.class);
		compareFields(fieldVorname, person.getFieldInHierarchie(fieldVorname));

		de.nvg.runtime.classdatamodel.FieldData fieldDateOfBirth = new de.nvg.runtime.classdatamodel.FieldData(false,
				"dateOfBirth", LocalDate.class);
		compareFields(fieldDateOfBirth, person.getFieldInHierarchie(fieldDateOfBirth));

		de.nvg.runtime.classdatamodel.FieldData fieldGeschlecht = new de.nvg.runtime.classdatamodel.FieldData(false,
				"geschlecht", Geschlecht.class);
		compareFields(fieldGeschlecht, person.getFieldInHierarchie(fieldGeschlecht));

		de.nvg.runtime.classdatamodel.SignatureData adressenSignature = new de.nvg.runtime.classdatamodel.SignatureData(
				List.class);
		adressenSignature.addSubType(new de.nvg.runtime.classdatamodel.SignatureData(Adresse.class));

		de.nvg.runtime.classdatamodel.FieldData fieldAdressen = new de.nvg.runtime.classdatamodel.FieldData(false,
				"adressen", List.class);
		fieldAdressen.setSignature(adressenSignature);
		compareFields(fieldAdressen, person.getFieldInHierarchie(fieldAdressen));

		de.nvg.runtime.classdatamodel.ConstructorData constructor = new de.nvg.runtime.classdatamodel.ConstructorData(
				true);
		compareConstructors(constructor, person.getConstructor());

		de.nvg.runtime.classdatamodel.ClassData classData = new de.nvg.runtime.classdatamodel.ClassData(
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

	private void compareClasses(de.nvg.runtime.classdatamodel.ClassData expected,
			de.nvg.runtime.classdatamodel.ClassData actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getSuperclass(), actual.getSuperclass());
	}

	private void compareConstructors(de.nvg.runtime.classdatamodel.ConstructorData expected,
			de.nvg.runtime.classdatamodel.ConstructorData actual) {
		Assert.assertEquals(expected.hasDefaultConstructor(), actual.hasDefaultConstructor());
		Assert.assertEquals(expected.getConstructorFields(), actual.getConstructorFields());
	}

	private void compareFields(de.nvg.runtime.classdatamodel.FieldData expected,
			de.nvg.runtime.classdatamodel.FieldData actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.getSignature(), actual.getSignature());
	}

	private void compareSetterMethods(de.nvg.runtime.classdatamodel.SetterMethodData expected,
			de.nvg.runtime.classdatamodel.SetterMethodData actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getDescriptor(), actual.getDescriptor());
		Assert.assertEquals(expected.getType(), actual.getType());
		Assert.assertEquals(expected.isStatic(), actual.isStatic());
	}

}
