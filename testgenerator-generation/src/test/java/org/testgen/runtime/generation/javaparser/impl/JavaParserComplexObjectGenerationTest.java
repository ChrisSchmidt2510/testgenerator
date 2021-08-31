package org.testgen.runtime.generation.javaparser.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.config.TestgeneratorConfig;
import org.testgen.runtime.classdata.ClassDataHolder;
import org.testgen.runtime.classdata.model.ClassData;
import org.testgen.runtime.classdata.model.ConstructorData;
import org.testgen.runtime.classdata.model.FieldData;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.generation.api.ComplexObjectGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.collection.JavaParserCollectionGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.generation.testclasses.Adresse;
import org.testgen.runtime.valuetracker.blueprint.ComplexBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class JavaParserComplexObjectGenerationTest implements ClassDataHolder {

	private ComplexObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> complexGeneration = new JavaParserComplexObjectGeneration();

	private Set<Class<?>> imports = new HashSet<>();

	FieldData ort = new FieldData("ort", String.class);
	FieldData strasse = new FieldData("strasse", String.class);
	FieldData hausnummer = new FieldData("hausnummer", short.class);
	FieldData plz = new FieldData("plz", int.class);

	private PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
			.setVisitorFactory(TestgeneratorPrettyPrinter::new);

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

	@After
	public void destroy() {
		imports.clear();

		setPropertyTraceReadFieldAccess(false);

		NamingServiceProvider.getNamingService().clearFields();
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
		ComplexBluePrint bluePrint = getBluePrintAdresse();

		ClassData classData = getClassDataAdresse(false);

		BlockStmt codeBlock = new BlockStmt();

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, Collections.emptySet());

		String expectedLocalValue = "{\r\n" + //
				"    Adresse adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"    adresse.setHausnummer((short) 7);\r\n" + //
				"\r\n" + //
				"}";

		Assert.assertEquals(expectedLocalValue, codeBlock.toString(printerConfig));
		Assert.assertTrue(imports.contains(Adresse.class));

		String expectedFieldValue = "{\r\n" + //
				"    this.adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    this.adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"    this.adresse.setHausnummer((short) 7);\r\n" + //
				"\r\n" + //
				"}";

		bluePrint.resetBuildState();

		imports.clear();

		BlockStmt block = new BlockStmt();

		complexGeneration.createObject(block, bluePrint, true, classData, Collections.emptySet());

		Assert.assertEquals(expectedFieldValue, block.toString(printerConfig));
		Assert.assertTrue(imports.contains(Adresse.class));
	}

	@Test
	public void testCreateObjectWithoutConstructor() {
		ClassData classData = getClassDataAdresse(true);

		ComplexBluePrint bluePrint = getBluePrintAdresse();

		BlockStmt codeBlock = new BlockStmt();

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, Collections.emptySet());

		String expectedValue = "{\r\n" + //
				"    // TODO add initalization for class: Adresse\r\n" + //
				"    Adresse adresse = null;\r\n" + //
				"    adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"    adresse.setHausnummer((short) 7);\r\n" + //
				"    adresse.setOrt(\"Nuernberg\");\r\n" + //
				"    adresse.setPlz(90757);\r\n" + //
				"\r\n" + //
				"}";

		Assert.assertEquals(expectedValue, codeBlock.toString(printerConfig));

		String expectedFieldValue = "{\r\n" + //
				"    // TODO add initalization for class: Adresse\r\n" + //
				"    this.adresse = null;\r\n" + //
				"    this.adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"    this.adresse.setHausnummer((short) 7);\r\n" + //
				"    this.adresse.setOrt(\"Nuernberg\");\r\n" + //
				"    this.adresse.setPlz(90757);\r\n" + //
				"\r\n" + //
				"}";

		bluePrint.resetBuildState();

		BlockStmt block = new BlockStmt();

		complexGeneration.createObject(block, bluePrint, true, classData, Collections.emptySet());

		Assert.assertEquals(expectedFieldValue, block.toString(printerConfig));
	}

	@Test
	public void testCreateObjectWithTraceReadFieldAccessFlagActivated() {
		ClassData classData = getClassDataAdresse(false);

		ComplexBluePrint bluePrint = getBluePrintAdresse();

		BlockStmt codeBlock = new BlockStmt();

		Set<FieldData> calledFields = new HashSet<>();
		calledFields.add(strasse);

		setPropertyTraceReadFieldAccess(true);

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, calledFields);

		String expectedValue = "{\r\n" + //
				"    Adresse adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"\r\n" + //
				"}";

		Assert.assertEquals(expectedValue, codeBlock.toString(printerConfig));

		bluePrint.resetBuildState();

		BlockStmt block = new BlockStmt();

		String expectedFieldValue = "{\r\n" + //
				"    this.adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    this.adresse.setStrasse(\"Bahnhofstrasse\");\r\n" + //
				"\r\n" + //
				"}";

		complexGeneration.createObject(block, bluePrint, true, classData, calledFields);

		Assert.assertEquals(expectedFieldValue, block.toString(printerConfig));
	}

	public class InnerClass {
	}

	@Test
	public void testCreateObjectInnerClass() {
		ComplexBluePrint bluePrint = new ComplexBluePrint("value", new InnerClass());
		bluePrint.addBluePrint(new ComplexBluePrint("outerClass", new JavaParserComplexObjectGenerationTest()));

		ClassData classData = new ClassData(
				"org.testgen.runtime.generation.javaparser.impl.JavaParserComplexObjectGenerationTest.InnerClass", null,
				() -> getTestgenerator$$ClassData(), new ConstructorData(true));

		BlockStmt codeBlock = new BlockStmt();

		String expectedValue = "{\r\n"
				+ "    JavaParserComplexObjectGenerationTest outerClass = new JavaParserComplexObjectGenerationTest();\r\n"
				+ "\r\n"//
				+ "    InnerClass value = outerClass.new InnerClass();\r\n" + //
				"\r\n" + //
				"}";

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, Collections.emptySet());

		Assert.assertEquals(expectedValue, codeBlock.toString(printerConfig));

		String expectedFieldValue = "{\r\n" + //
				"    JavaParserComplexObjectGenerationTest outerClass = new JavaParserComplexObjectGenerationTest();\r\n"
				+ "\r\n" + //
				"    this.value = outerClass.new InnerClass();\r\n" + //
				"\r\n" + //
				"}";

		bluePrint.resetBuildState();

		BlockStmt block = new BlockStmt();

		complexGeneration.createObject(block, bluePrint, true, classData, Collections.emptySet());

		Assert.assertEquals(expectedFieldValue, block.toString(printerConfig));
	}

	@Test
	public void testCreateObjectWithMissingSetter() {
		ConstructorData constructor = new ConstructorData(false);
		constructor.addElement(0, ort);
		constructor.addElement(1, plz);

		ClassData classData = new ClassData("org.testgen.runtime.generation.testclasses.Adresse", constructor);
		classData.addField(ort);
		classData.addField(plz);
		classData.addField(strasse);
		classData.addField(hausnummer);

		classData.addFieldSetterPair(hausnummer,
				new SetterMethodData("setHausnummer", "(S)V", false, SetterType.VALUE_SETTER));
		classData.addFieldSetterPair(ort,
				new SetterMethodData("setOrt", "(Ljava/lang/String;)V", false, SetterType.VALUE_SETTER));
		classData.addFieldSetterPair(plz, new SetterMethodData("setPlz", "(I)V", false, SetterType.VALUE_SETTER));

		ComplexBluePrint bluePrint = getBluePrintAdresse();

		BlockStmt codeBlock = new BlockStmt();

		complexGeneration.createObject(codeBlock, bluePrint, false, classData, Collections.emptySet());

		String expectedValue = "{\r\n" + //
				"    Adresse adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    // TODO no setter found for Field: strasse Value: \"Bahnhofstrasse\"\r\n" + //
				"    adresse.setHausnummer((short) 7);\r\n" + //
				"\r\n" + //
				"}";

		Assert.assertEquals(expectedValue, codeBlock.toString(printerConfig));

		bluePrint.resetBuildState();

		BlockStmt block = new BlockStmt();

		String expectedFieldValue = "{\r\n" + //
				"    this.adresse = new Adresse(\"Nuernberg\", 90757);\r\n" + //
				"    // TODO no setter found for Field: strasse Value: \"Bahnhofstrasse\"\r\n" + //
				"    this.adresse.setHausnummer((short) 7);\r\n" + //
				"\r\n" + //
				"}";

		complexGeneration.createObject(block, bluePrint, true, classData, Collections.emptySet());

		Assert.assertEquals(expectedFieldValue, block.toString(printerConfig));
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

	private ComplexBluePrint getBluePrintAdresse() {
		ComplexBluePrint bluePrint = new ComplexBluePrint("adresse", new Adresse("there", 5));

		StringBluePrintFactory strFactory = new StringBluePrintFactory();
		NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

		bluePrint.addBluePrint(strFactory.createBluePrint("strasse", "Bahnhofstrasse"));
		bluePrint.addBluePrint(numFactory.createBluePrint("hausnummer", (short) 7));
		bluePrint.addBluePrint(strFactory.createBluePrint("ort", "Nuernberg"));
		bluePrint.addBluePrint(numFactory.createBluePrint("plz", 90757));

		return bluePrint;
	}

	public static ClassData getTestgenerator$$ClassData() {
		return new ClassData("org.testgen.runtime.generation.javaparser.impl.JavaParserComplexObjectGenerationTest",
				new ConstructorData(true));
	}

	private static void setPropertyTraceReadFieldAccess(boolean value) {
		System.setProperty(TestgeneratorConfig.PARAM_TRACE_READ_FIELD_ACCESS, Boolean.toString(value));
	}

}
