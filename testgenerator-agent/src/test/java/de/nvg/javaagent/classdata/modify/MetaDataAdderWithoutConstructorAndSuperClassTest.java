package de.nvg.javaagent.classdata.modify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nvg.javaagent.classdata.model.ClassData;
import de.nvg.javaagent.classdata.model.ConstructorData;
import de.nvg.javaagent.classdata.model.FieldData;
import de.nvg.javaagent.classdata.model.MethodData;
import de.nvg.javaagent.classdata.model.MethodType;
import de.nvg.testgenerator.MethodHandles;
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

public class MetaDataAdderWithoutConstructorAndSuperClassTest {

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

		adresse = classPool.get("de.nvg.javaagent.classdata.modify.testclasses.Adresse");

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
	public void testMetaDataAdderWithDefaultConstructorAndSuperClass()
			throws CannotCompileException, BadBytecode, IOException {
		ClassData classData = prepareClassData();

		MetaDataAdder metaDataAdder = new MetaDataAdder(constantPool, adresse, classData);
		metaDataAdder.add(codeAttribute, new ArrayList<>());

		Class<?> adresseClazz = classPool.toClass(adresse);

		de.nvg.runtime.classdatamodel.ClassData runtimeClassData = MethodHandles.getStaticFieldValue(adresseClazz,
				"classData");

		// TODO CS vergleichs runtimeData object schreiben
		System.out.println(runtimeClassData);
	}

//	@Test
//	public void testMetaDataAdderWithConstructorAndSuperClass()
//			throws CannotCompileException, BadBytecode, FileNotFoundException, IOException {
//		ClassData classData = prepareClassData();
//		classData.setSuperClass("de.nvg.javaagent.classdata.modify.testclasses.BlObject");
//
//		ConstructorData constructor = new ConstructorData(false);
//		constructor.addConstructorElement(0, fieldStrasse);
//		constructor.addConstructorElement(1, fieldHausnummer);
//		constructor.addConstructorElement(2, fieldOrt);
//		constructor.addConstructorElement(3, fieldPlz);
//
//		classData.setConstructor(constructor);
//
//		MetaDataAdder metaDataAdder = new MetaDataAdder(constantPool, adresse, classData);
//		metaDataAdder.add(codeAttribute, new ArrayList<>());
//
//		try (FileOutputStream stream = new FileOutputStream(new File("D:\\Adresse.class"))) {
//			stream.write(adresse.toBytecode());
//		}
//
//		Class<?> adresseClazz = classPool.toClass(adresse);
//
//		de.nvg.runtime.classdatamodel.ClassData runtimeClassData = MethodHandles.getStaticFieldValue(adresseClazz,
//				"classData");
//
//		de.nvg.runtime.classdatamodel.ClassData superclass = runtimeClassData.getSuperclass();
//
//		// TODO CS vergleichs runtimeData object schreiben
//		System.out.println(runtimeClassData);
//		System.out.println(superclass);
//	}

	private ClassData prepareClassData() {

		ClassData classData = new ClassData("de.nvg.javaagent.classdata.modify.testclasses.Adresse");
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

}
