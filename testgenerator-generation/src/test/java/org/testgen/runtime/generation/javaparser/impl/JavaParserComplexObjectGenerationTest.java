package org.testgen.runtime.generation.javaparser.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.ConstructorData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.javaparser.impl.collection.JavaParserCollectionGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.generation.testclasses.Adresse;
import org.testgen.runtime.valuetracker.ObjectValueTracker;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.Type;
import org.testgen.runtime.valuetracker.storage.ValueStorage;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class JavaParserComplexObjectGenerationTest {

	private ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration = new JavaParserComplexObjectGeneration();

	private Set<Class<?>> imports = new HashSet<>();

	FieldData ort = new FieldData("ort", String.class);
	FieldData strasse = new FieldData("strasse", String.class);
	FieldData hausnummer = new FieldData("hausnummer", short.class);
	FieldData plz = new FieldData("plz", int.class);

	@Before
	public void init() {
		JavaParserSimpleObjectGenerationFactory simpleFactory = new JavaParserSimpleObjectGenerationFactory();
		JavaParserCollectionGenerationFactory collectionFactory = new JavaParserCollectionGenerationFactory();
		JavaParserArrayGeneration arrayGeneration = new JavaParserArrayGeneration();

		complexGeneration.setSimpleObjectGenerationFactory(simpleFactory);
		collectionFactory.setSimpleObjectGenerationFactory(simpleFactory);
		arrayGeneration.setSimpleObjectGenerationFactory(simpleFactory);

		collectionFactory.setComplexObjectGeneration(complexGeneration);
		arrayGeneration.setComplexObjectGeneration(complexGeneration);

		complexGeneration.setCollectionGenerationFactory(collectionFactory);
		arrayGeneration.setCollectionGenerationFactory(collectionFactory);

		complexGeneration.setArrayGeneration(arrayGeneration);
		collectionFactory.setArrayGeneration(arrayGeneration);

		Consumer<Class<?>> importCallBackHandler = imports::add;

		complexGeneration.setImportCallBackHandler(importCallBackHandler);
		collectionFactory.setImportCallBackHandler(importCallBackHandler);
		arrayGeneration.setImportCallBackHandler(importCallBackHandler);
		simpleFactory.setImportCallBackHandler(importCallBackHandler);
	}

	@Test
	public void testCreateField() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		ComplexBluePrint bluePrint = new ComplexBluePrint("value", new Object());

		complexGeneration.createField(cu, bluePrint, null);
		Assert.assertEquals("private Object value;", cu.getFields().get(0).toString());
		Assert.assertTrue(imports.contains(Object.class));
	}

	@Test
	public void testCreateObject() {

		Adresse adresse = new Adresse("Nuernberg", 90757);
		adresse.setStrasse("Bahnhofstrasse");
		adresse.setHausnummer((short) 7);

		ValueStorage.getInstance().pushNewTestData();

		ObjectValueTracker.getInstance().track(adresse, "adresse", Type.TESTOBJECT);

		ComplexBluePrint bluePrint = ValueStorage.getInstance().getTestObject().castToComplexBluePrint();

		ClassData classData = getClassDataAdresse(false);

		Set<FieldData> calledFields = new HashSet<>();
		calledFields.add(ort);
		calledFields.add(plz);
		calledFields.add(strasse);
		calledFields.add(hausnummer);

		BlockStmt codeBlock = new BlockStmt();

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, calledFields);

		String expectedValue = "{\r\n" + //
				"    Adresse adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"    adresse.setHausnummer((short) 7);\r\n" + //
				"\r\n" + //
				"}";
		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);

		Assert.assertEquals(expectedValue, codeBlock.toString(printerConfig));
	}

	@Test
	public void testCreateObjectWithoutConstructor() {
		Adresse adresse = new Adresse("Nuernberg", 90757);
		adresse.setStrasse("Bahnhofstrasse");
		adresse.setHausnummer((short) 7);

		ValueStorage.getInstance().pushNewTestData();

		ObjectValueTracker.getInstance().track(adresse, "adresse", Type.TESTOBJECT);

		ComplexBluePrint bluePrint = ValueStorage.getInstance().getTestObject().castToComplexBluePrint();

		ClassData classData = getClassDataAdresse(true);

		Set<FieldData> calledFields = new HashSet<>();
		calledFields.add(ort);
		calledFields.add(plz);
		calledFields.add(strasse);
		calledFields.add(hausnummer);

		BlockStmt codeBlock = new BlockStmt();

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, calledFields);

		String expectedValue = "{\r\n" + //
				"    // TODO add initalization for class: Adresse\r\n" + //
				"    Adresse adresse = null;\r\n" + //
				"    adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"    adresse.setHausnummer((short) 7);\r\n" + //
				"    adresse.setOrt(\"Nuernberg\");\r\n" + //
				"    adresse.setPlz(90757);\r\n" + //
				"\r\n" + //
				"}";
		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);

		Assert.assertEquals(expectedValue, codeBlock.toString(printerConfig));

	}

	private ClassData getClassDataAdresse(boolean withDefaultConstructor) {
		ConstructorData constructor = new ConstructorData(false);
		constructor.addElement(0, ort);
		constructor.addElement(1, plz);

		ClassData classData = new ClassData("org.testgen.runtime.generation.testclasses.Adresse",
				withDefaultConstructor ? new ConstructorData(false) : constructor);
		classData.addField(ort);
		classData.addField(plz);
		classData.addField(strasse);
		classData.addField(hausnummer);

		classData.addFieldSetterPair(strasse,
				new SetterMethodData("setStrasse", "(Ljava/lang/String;)V", false, SetterType.VALUE_SETTER));
		classData.addFieldSetterPair(hausnummer,
				new SetterMethodData("setHausnummer", "(S)V", false, SetterType.VALUE_SETTER));
		classData.addFieldSetterPair(ort,
				new SetterMethodData("setOrt", "(Ljava/lang/String;)V", false, SetterType.VALUE_SETTER));
		classData.addFieldSetterPair(plz, new SetterMethodData("setPlz", "(I)V", false, SetterType.VALUE_SETTER));
		return classData;
	}

}
