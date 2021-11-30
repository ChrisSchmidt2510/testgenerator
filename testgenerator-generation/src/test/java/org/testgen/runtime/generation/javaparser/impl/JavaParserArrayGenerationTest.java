package org.testgen.runtime.generation.javaparser.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.classdata.model.SetterMethodData;
import org.testgen.runtime.classdata.model.SetterType;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.collection.JavaParserCollectionGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.CurrentlyBuildedBluePrints;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.complextypes.ArrayBluePrint.ArrayBluePrintFactory;
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
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;

public class JavaParserArrayGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration = new JavaParserArrayGeneration();

	private ArrayBluePrintFactory arrayFactory = new ArrayBluePrintFactory();

	private CurrentlyBuildedBluePrints currentlyBuildedBluePrints = new CurrentlyBuildedBluePrints();
	
	private DefaultPrettyPrinter printer= new DefaultPrettyPrinter(
			(config) -> new TestgeneratorPrettyPrinter(config), new DefaultPrinterConfiguration());

	@BeforeEach
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

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		ArrayBluePrint bluePrint = arrayFactory
				.createBluePrint("value", new List[10][], currentlyBuildedBluePrints, null).castToArrayBluePrint();

		SignatureType nestedType = new SignatureType(LocalDate.class);
		SignatureType signature = new SignatureType(List[][].class);
		signature.addSubType(nestedType);

		arrayGeneration.createField(cu, bluePrint, signature);
		assertEquals("private List<LocalDate>[][] value = new List[10][];", cu.getFields().get(0).toString());
		assertTrue(imports.contains(List.class) && imports.contains(LocalDate.class));

		imports.clear();

		arrayGeneration.createField(cu, bluePrint, null);
		assertEquals("private List[][] value = new List[10][];", cu.getFields().get(1).toString());
		assertTrue(imports.contains(List.class));

		NumberBluePrintFactory factory = new NumberBluePrintFactory();

		ArrayBluePrint primitiveBluePrint = arrayFactory.createBluePrint("array", new int[5],
				currentlyBuildedBluePrints, (name, value) -> factory.createBluePrint(name, (Number) value))
				.castToArrayBluePrint();

		SignatureType primitiveSignature = new SignatureType(int[].class);

		arrayGeneration.createField(cu, primitiveBluePrint, primitiveSignature);
		assertEquals("private int[] array = new int[5];", cu.getFields().get(2).toString());

		arrayGeneration.createField(cu, primitiveBluePrint, null);
		assertEquals("private int[] array = new int[5];", cu.getFields().get(3).toString());
	}

	@Test
	public void testCreateArray() {
		int[] array = new int[] { 1, 2, 4, 8 };

		NumberBluePrintFactory numFactory = new NumberBluePrintFactory();

		ArrayBluePrint bluePrint = arrayFactory.createBluePrint("value", array, currentlyBuildedBluePrints,
				(name, value) -> numFactory.createBluePrint(name, (Number) value)).castToArrayBluePrint();

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



		assertEquals(expected, printer.print(fieldWithSignature));

		bluePrint.resetBuildState();

		BlockStmt fieldWithoutSignature = new BlockStmt();

		arrayGeneration.createArray(fieldWithoutSignature, bluePrint, null, true);
		assertEquals(expected, printer.print(fieldWithoutSignature));

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
		assertEquals(expectedLocal, printer.print(localWithSignature));

		bluePrint.resetBuildState();

		BlockStmt localWithoutSignature = new BlockStmt();

		arrayGeneration.createArray(localWithoutSignature, bluePrint, null, false);
		assertEquals(expectedLocal, printer.print(localWithoutSignature));
	}

	@Test
	public void testAddArrayToObject() {
		LocalTime[] array = new LocalTime[3];
		array[0] = LocalTime.of(0, 1);
		array[2] = LocalTime.of(23, 57);

		LocalTimeBluePrintFactory localTimeFactory = new LocalTimeBluePrintFactory();

		ArrayBluePrint bluePrint = arrayFactory
				.createBluePrint("value", array, currentlyBuildedBluePrints,
						(name, value) -> localTimeFactory.createBluePrint(name, (LocalTime) value))
				.castToArrayBluePrint();

		NameExpr accessExpr = new NameExpr("object");

		SetterMethodData setter = new SetterMethodData("setArray", "([Ljava/time/LocalTime;)V", false,
				SetterType.VALUE_SETTER);

		BlockStmt block = new BlockStmt();

		arrayGeneration.addArrayToObject(block, bluePrint, setter, true, accessExpr);
		assertEquals("object.setArray(this.value);", block.getStatement(0).toString());

		arrayGeneration.addArrayToObject(block, bluePrint, setter, false, accessExpr);
		assertEquals("object.setArray(value);", block.getStatement(1).toString());

		ArrayBluePrint arrayBluePrint = arrayFactory
				.createBluePrint("array", array, currentlyBuildedBluePrints,
						(name, value) -> localTimeFactory.createBluePrint(name, (LocalTime) value))
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

		assertEquals(expected, getterBlock.toString());

		getterBlock.setStatements(new NodeList<>());

		arrayGeneration.addArrayToObject(getterBlock, arrayBluePrint, getter, false, accessExpr);

		String expectedAlternativeGetterField = "{\r\n"//
				+ "    object.getArray()[0] = array[0];\r\n"//
				+ "    object.getArray()[2] = array[2];\r\n"//
				+ "}";

		assertEquals(expectedAlternativeGetterField, getterBlock.toString());
	}

	@Test
	public void testAddArrayToField() {
		ArrayBluePrint bluePrint = arrayFactory
				.createBluePrint("value", new String[10], currentlyBuildedBluePrints, null).castToArrayBluePrint();

		BlockStmt codeBlock = new BlockStmt();

		FieldAccessExpr accessExpr = new FieldAccessExpr(new ThisExpr(), "object");

		arrayGeneration.addArrayToField(codeBlock, bluePrint, true, accessExpr);
		assertEquals("this.object = this.value;", codeBlock.getStatement(0).toString());

		arrayGeneration.addArrayToField(codeBlock, bluePrint, false, accessExpr);
		assertEquals("this.object = value;", codeBlock.getStatement(1).toString());
	}

}
