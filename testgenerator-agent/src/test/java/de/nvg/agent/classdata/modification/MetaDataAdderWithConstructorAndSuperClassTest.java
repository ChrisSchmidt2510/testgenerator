package de.nvg.agent.classdata.modification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.ConstructorData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.MethodType;
import de.nvg.agent.classdata.modification.MetaDataAdder;
import de.nvg.runtime.classdatamodel.SetterMethodData;
import de.nvg.testgenerator.MethodHandles;
import de.nvg.testgenerator.TestgeneratorConstants;
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

public class MetaDataAdderWithConstructorAndSuperClassTest {

	private CtClass adresse;
	private ClassPool classPool = ClassPool.getDefault();
	private ConstPool constantPool;
	private CodeAttribute codeAttribute;

	private FieldData fieldStrasse = new FieldData.Builder().withName("strasse").withDataType("java.lang.String")
			.build();
	private FieldData fieldHausnummer = new FieldData.Builder().withName("hausnummer").withDataType("short").build();
	private FieldData fieldOrt = new FieldData.Builder().withName("ort").withDataType("java.lang.String").build();
	private FieldData fieldPlz = new FieldData.Builder().withName("plz").withDataType("int").build();

	@Before
	public void init() throws NotFoundException, DuplicateMemberException {

		adresse = classPool.get("de.nvg.agent.classdata.modify.testclasses.Adresse");

		ClassFile classFile = adresse.getClassFile();
		constantPool = classFile.getConstPool();

		MethodInfo method = new MethodInfo(constantPool, MethodInfo.nameClinit, "()V");

		codeAttribute = new CodeAttribute(constantPool, 0, 0, new byte[0], new ExceptionTable(constantPool));

		method.setCodeAttribute(codeAttribute);
		method.setAccessFlags(Modifier.STATIC);

		classFile.addMethod(method);
	}

	@After
	public void shutdown() {
		adresse.detach();

		adresse = null;
	}

	@Test
	public void testMetaDataAdderWithConstructorAndSuperClass()
			throws CannotCompileException, BadBytecode, FileNotFoundException, IOException {
		ClassData classData = prepareClassData();
		classData.setSuperClass(new ClassData("de.nvg.agent.classdata.modify.testclasses.BlObject"));

		ConstructorData constructor = new ConstructorData(false);
		constructor.addConstructorElement(0, fieldStrasse);
		constructor.addConstructorElement(1, fieldHausnummer);
		constructor.addConstructorElement(2, fieldOrt);
		constructor.addConstructorElement(3, fieldPlz);

		classData.setConstructor(constructor);

		MetaDataAdder metaDataAdder = new MetaDataAdder(constantPool, adresse, classData);
		metaDataAdder.add(codeAttribute, new ArrayList<>());

		try (FileOutputStream stream = new FileOutputStream(new File("D:\\Adresse.class"))) {
			stream.write(adresse.toBytecode());
		}

		Class<?> adresseClazz = classPool.toClass(adresse);

		de.nvg.runtime.classdatamodel.ClassData runtimeClassData = MethodHandles.getStaticFieldValue(adresseClazz,
				TestgeneratorConstants.FIELDNAME_CLASS_DATA);

		de.nvg.runtime.classdatamodel.ClassData superclass = runtimeClassData.getSuperclass();

		// TODO CS vergleichs runtimeData object schreiben
		System.out.println(runtimeClassData);
		Assert.assertEquals(createdClassData(), runtimeClassData);
		System.out.println(superclass);
	}

	private ClassData prepareClassData() {

		ClassData classData = new ClassData("de.nvg.agent.classdata.modify.testclasses.Adresse");
		classData.setConstructor(new ConstructorData(true));

		classData.addFields(Arrays.asList(fieldStrasse, fieldHausnummer, fieldOrt, fieldPlz));

		MethodData methodSetStrasse = new MethodData("setStrasse", "(Ljava/lang/String;)V",
				MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetStrasse, fieldStrasse);

		MethodData methodSetHausnummer = new MethodData("setHausnummer", "(I)V", MethodType.REFERENCE_VALUE_SETTER, 0,
				false);
		classData.addMethod(methodSetHausnummer, fieldHausnummer);

		MethodData methodSetOrt = new MethodData("setOrt", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER,
				0, false);
		classData.addMethod(methodSetOrt, fieldOrt);

		MethodData methodSetPlz = new MethodData("setPlz", "(I)V", MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetPlz, fieldPlz);

		// TODO CS restliche Methoden implementieren

		return classData;
	}

	private de.nvg.runtime.classdatamodel.ClassData createdClassData() {

		de.nvg.runtime.classdatamodel.FieldData fieldStrasse = new de.nvg.runtime.classdatamodel.FieldData(false,
				"strasse", "java.lang.String");
		de.nvg.runtime.classdatamodel.FieldData fieldHausnummer = new de.nvg.runtime.classdatamodel.FieldData(false,
				"hausnummer", "short");
		de.nvg.runtime.classdatamodel.FieldData fieldOrt = new de.nvg.runtime.classdatamodel.FieldData(false, "ort",
				"java.lang.String");
		de.nvg.runtime.classdatamodel.FieldData fieldPlz = new de.nvg.runtime.classdatamodel.FieldData(false, "plz",
				"int");

		de.nvg.runtime.classdatamodel.ConstructorData constructor = new de.nvg.runtime.classdatamodel.ConstructorData(
				false);
		constructor.addElement(0, fieldStrasse);
		constructor.addElement(1, fieldHausnummer);
		constructor.addElement(2, fieldOrt);
		constructor.addElement(3, fieldPlz);

		de.nvg.runtime.classdatamodel.ClassData classData = new de.nvg.runtime.classdatamodel.ClassData(
				"de.nvg.agent.classdata.modify.testclasses.Adresse", constructor);

		classData.addFieldSetterPair(fieldStrasse, new SetterMethodData("setStrasse", "(Ljava/lang/String;)V", false));
		classData.addFieldSetterPair(fieldHausnummer, new SetterMethodData("setHausnummer", "(S)V", false));
		classData.addFieldSetterPair(fieldOrt, new SetterMethodData("setOrt", "(Ljava/lang/String;)V", false));
		classData.addFieldSetterPair(fieldPlz, new SetterMethodData("setPlz", "(I)V", false));

		return classData;
	}

}
