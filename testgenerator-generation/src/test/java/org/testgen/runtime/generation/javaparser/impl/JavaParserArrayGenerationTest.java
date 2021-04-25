package org.testgen.runtime.generation.javaparser.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.collection.JavaParserCollectionGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint.ArrayBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.LocalTimeBluePrint.LocalTimeBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

public class JavaParserArrayGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = new JavaParserArrayGeneration();

	private ArrayBluePrintFactory arrayFactory = new ArrayBluePrintFactory();

	@Before
	public void init() {
		JavaParserSimpleObjectGenerationFactory simpleObjectGenerationFactory = new JavaParserSimpleObjectGenerationFactory();
		JavaParserCollectionGenerationFactory collectionGenerationFactory = new JavaParserCollectionGenerationFactory();

		Consumer<Class<?>> importCallBackHandler = imports::add;

		arrayGeneration.setImportCallBackHandler(importCallBackHandler);
		simpleObjectGenerationFactory.setImportCallBackHandler(importCallBackHandler);
		collectionGenerationFactory.setImportCallBackHandler(importCallBackHandler);

		collectionGenerationFactory.setSimpleObjectGenerationFactory(simpleObjectGenerationFactory);

		arrayGeneration.setSimpleObjectGenerationFactory(simpleObjectGenerationFactory);

		arrayGeneration.setCollectionGenerationFactory(collectionGenerationFactory);
	}

	@After
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		ArrayBluePrint bluePrint = arrayFactory.createBluePrint("value", new List[10][], null).castToArrayBluePrint();

		SignatureType nestedType = new SignatureType(LocalDate.class);
		SignatureType signature = new SignatureType(List[][].class);
		signature.addSubType(nestedType);

		arrayGeneration.createField(cu, bluePrint, signature);
		Assert.assertEquals("private List<LocalDate>[][] value = new List[10][];", cu.getFields().get(0).toString());
		Assert.assertTrue(imports.contains(List.class) && imports.contains(LocalDate.class));

		imports.clear();

		arrayGeneration.createField(cu, bluePrint, null);
		Assert.assertEquals("private List[][] value = new List[10][];", cu.getFields().get(1).toString());
		Assert.assertTrue(imports.contains(List.class));

		NumberBluePrintFactory factory = new NumberBluePrintFactory();

		ArrayBluePrint primitiveBluePrint = arrayFactory
				.createBluePrint("array", new int[5], (name, value) -> factory.createBluePrint(name, value))
				.castToArrayBluePrint();

		SignatureType primitiveSignature = new SignatureType(int[].class);

		arrayGeneration.createField(cu, primitiveBluePrint, primitiveSignature);
		Assert.assertEquals("private int[] array = new int[5];", cu.getFields().get(2).toString());

		arrayGeneration.createField(cu, primitiveBluePrint, null);
		Assert.assertEquals("private int[] array = new int[5];", cu.getFields().get(3).toString());
	}

	@Test
	public void testCreateArray() {
		int[] array = new int[] { 1, 2, 4, 8 };

		NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

		ArrayBluePrint bluePrint = arrayFactory
				.createBluePrint("value", array, (name, value) -> numFactory.createBluePrint(name, value))
				.castToArrayBluePrint();

		BlockStmt fieldWithSignature = new BlockStmt();

		SignatureType signature = new SignatureType(int[].class);

		arrayGeneration.createArray(fieldWithSignature, bluePrint, signature, true);

		String expected = "{\r\n"//
				+ "    this.value[0] = 1;\r\n"//
				+ "    this.value[1] = 2;\r\n"//
				+ "    this.value[2] = 4;\r\n"//
				+ "    this.value[3] = 8;\r\n"//
				+ "\r\n"//
				+ "}";

		PrettyPrinterConfiguration printerConfig = new PrettyPrinterConfiguration()
				.setVisitorFactory(TestgeneratorPrettyPrinter::new);

		Assert.assertEquals(expected, fieldWithSignature.toString(printerConfig));

		bluePrint.resetBuildState();

		BlockStmt fieldWithoutSignature = new BlockStmt();

		arrayGeneration.createArray(fieldWithoutSignature, bluePrint, null, true);
		Assert.assertEquals(expected, fieldWithoutSignature.toString(printerConfig));

		bluePrint.resetBuildState();

		BlockStmt localWithSignature = new BlockStmt();

		arrayGeneration.createArray(localWithSignature, bluePrint, signature, false);

		String expectedLocal = "{\r\n"//
				+ "    int[] value = new int[4];\r\n"//
				+ "    value[0] = 1;\r\n"//
				+ "    value[1] = 2;\r\n"//
				+ "    value[2] = 4;\r\n"//
				+ "    value[3] = 8;\r\n"//
				+ "\r\n"//
				+ "}";
		Assert.assertEquals(expectedLocal, localWithSignature.toString(printerConfig));

		bluePrint.resetBuildState();

		BlockStmt localWithoutSignature = new BlockStmt();

		arrayGeneration.createArray(localWithoutSignature, bluePrint, null, false);
		Assert.assertEquals(expectedLocal, localWithoutSignature.toString(printerConfig));
	}

	@Test
	public void testAddArrayToObject() {
		LocalTime[] array = new LocalTime[3];
		array[0] = LocalTime.of(0, 1);
		array[2] = LocalTime.of(23, 57);

		LocalTimeBluePrintFactory localTimeFactory = new LocalTimeBluePrintFactory();

		ArrayBluePrint bluePrint = arrayFactory
				.createBluePrint("value", array, (name, value) -> localTimeFactory.createBluePrint(name, value))
				.castToArrayBluePrint();

		NameExpr accessExpr = new NameExpr("object");

		SetterMethodData setter = new SetterMethodData("setArray", "([Ljava/time/LocalTime;)V", false,
				SetterType.VALUE_SETTER);

		BlockStmt block = new BlockStmt();

		arrayGeneration.addArrayToObject(block, bluePrint, setter, true, accessExpr);
		Assert.assertEquals("object.setArray(this.value);", block.getStatement(0).toString());

		arrayGeneration.addArrayToObject(block, bluePrint, setter, false, accessExpr);
		Assert.assertEquals("object.setArray(value);", block.getStatement(1).toString());

		ArrayBluePrint arrayBluePrint = arrayFactory
				.createBluePrint("array", array, (name, value) -> localTimeFactory.createBluePrint(name, value))
				.castToArrayBluePrint();

		BlockStmt getterBlock = new BlockStmt();

		SetterMethodData getter = new SetterMethodData("getArray", "()[Ljava/time/LocalTime;", false,
				SetterType.VALUE_GETTER);

		arrayGeneration.addArrayToObject(getterBlock, arrayBluePrint, getter, true, accessExpr);

		String expected = "{\r\n"//
				+ "    LocalTime[] array = object.getArray();\r\n"//
				+ "    array[0] = LocalTime.of(0, 1);\r\n"//
				+ "    array[2] = LocalTime.of(23, 57);\r\n"//
				+ "}";

		Assert.assertEquals(expected, getterBlock.toString());

		getterBlock.setStatements(new NodeList<>());

		arrayGeneration.addArrayToObject(getterBlock, arrayBluePrint, getter, false, accessExpr);

		String expectedAlternativeGetterField = "{\r\n"//
				+ "    object.getArray()[0] = array[0];\r\n"//
				+ "    object.getArray()[2] = array[2];\r\n"//
				+ "}";

		Assert.assertEquals(expectedAlternativeGetterField, getterBlock.toString());
	}

	@Test
	public void testAddArrayToField() {
		ArrayBluePrint bluePrint = arrayFactory.createBluePrint("value", new String[10], null).castToArrayBluePrint();

		BlockStmt codeBlock = new BlockStmt();

		FieldAccessExpr accessExpr = new FieldAccessExpr(new ThisExpr(), "object");

		arrayGeneration.addArrayToField(codeBlock, bluePrint, true, accessExpr);
		Assert.assertEquals("this.object = this.value;", codeBlock.getStatement(0).toString());

		arrayGeneration.addArrayToField(codeBlock, bluePrint, false, accessExpr);
		Assert.assertEquals("this.object = value;", codeBlock.getStatement(1).toString());
	}

}
