package org.testgen.runtime.generation.javaparser.impl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.classdata.model.descriptor.SignatureType;
import org.testgen.runtime.generation.api.ArrayGeneration;
import org.testgen.runtime.generation.api.GenerationFactory;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.javaparser.impl.collection.JavaParserCollectionGenerationFactory;
import org.testgen.runtime.generation.javaparser.impl.simple.JavaParserSimpleObjectGenerationFactory;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint;
import org.testgen.runtime.valuetracker.blueprint.ArrayBluePrint.ArrayBluePrintFactory;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NumberBluePrint.NumberBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class JavaParserArrayGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();
	private ArrayGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> arrayGeneration;

	private ArrayBluePrintFactory arrayFactory = new ArrayBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setSimpleObjectGenerationFactory(new JavaParserSimpleObjectGenerationFactory());

		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setCollectionGenerationFactory(new JavaParserCollectionGenerationFactory());

		arrayGeneration = new JavaParserArrayGeneration();
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
				+ "}";
		Assert.assertEquals(expected, fieldWithSignature.toString());

		bluePrint.resetBuildState();

		BlockStmt fieldWithoutSignature = new BlockStmt();

		arrayGeneration.createArray(fieldWithoutSignature, bluePrint, null, true);
		Assert.assertEquals(expected, fieldWithoutSignature.toString());

		bluePrint.resetBuildState();

		BlockStmt localWithSignature = new BlockStmt();

		arrayGeneration.createArray(localWithSignature, bluePrint, signature, false);

		String expectedLocal = "{\r\n"//
				+ "    int[] value = new int[4];\r\n"//
				+ "    value[0] = 1;\r\n"//
				+ "    value[1] = 2;\r\n"//
				+ "    value[2] = 4;\r\n"//
				+ "    value[3] = 8;\r\n"//
				+ "}";
		Assert.assertEquals(expectedLocal, localWithSignature.toString());

		bluePrint.resetBuildState();

		BlockStmt localWithoutSignature = new BlockStmt();

		arrayGeneration.createArray(localWithoutSignature, bluePrint, null, false);
		Assert.assertEquals(expectedLocal, localWithoutSignature.toString());

	}
}
