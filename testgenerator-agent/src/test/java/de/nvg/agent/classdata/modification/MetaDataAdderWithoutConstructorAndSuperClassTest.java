package de.nvg.agent.classdata.modification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.nvg.agent.classdata.model.ClassData;
import de.nvg.agent.classdata.model.ConstructorData;
import de.nvg.agent.classdata.model.FieldData;
import de.nvg.agent.classdata.model.MethodData;
import de.nvg.agent.classdata.model.MethodType;
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

//TODO CS refactoren
public class MetaDataAdderWithoutConstructorAndSuperClassTest {

	private CtClass adresse;
	private ClassPool classPool = ClassPool.getDefault();
	private ConstPool constantPool;
	private CodeAttribute codeAttribute;

	private FieldData fieldName = new FieldData.Builder().withName("name").withDataType("java.lang.String").build();
	private FieldData fieldVorname = new FieldData.Builder().withName("firstName").withDataType("java.lang.String")
			.build();
	private FieldData fieldOrt = new FieldData.Builder().withName("dateOfBirth").withDataType("java.time.LocalDate")
			.build();
	private FieldData fieldPlz = new FieldData.Builder().withName("geschlecht")
			.withDataType("de.nvg.agent.classdata.testclasses.Person$Geschlecht").build();

	@Before
	public void init() throws NotFoundException, DuplicateMemberException {

		adresse = classPool.get("de.nvg.agent.classdata.testclasses.Person");

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
				TestgeneratorConstants.FIELDNAME_CLASS_DATA);

		// TODO CS vergleichs runtimeData object schreiben
		System.out.println(runtimeClassData);
	}

	private ClassData prepareClassData() {

		ClassData classData = new ClassData("de.nvg.agent.classdata.testclasses.Adresse");
		classData.setConstructor(new ConstructorData(true));

		classData.addFields(Arrays.asList(fieldName, fieldVorname, fieldOrt, fieldPlz));

		MethodData methodSetStrasse = new MethodData("setStrasse", "(Ljava/lang/String;)V",
				MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetStrasse, fieldName);

		MethodData methodSetHausnummer = new MethodData("setHausnummer", "(I)V", MethodType.REFERENCE_VALUE_SETTER, 0,
				false);
		classData.addMethod(methodSetHausnummer, fieldVorname);

		MethodData methodSetOrt = new MethodData("setOrt", "(Ljava/lang/String;)V", MethodType.REFERENCE_VALUE_SETTER,
				0, false);
		classData.addMethod(methodSetOrt, fieldOrt);

		MethodData methodSetPlz = new MethodData("setPlz", "(I)V", MethodType.REFERENCE_VALUE_SETTER, 0, false);
		classData.addMethod(methodSetPlz, fieldPlz);

		// TODO CS restliche Methoden implementieren

		return classData;
	}

}
