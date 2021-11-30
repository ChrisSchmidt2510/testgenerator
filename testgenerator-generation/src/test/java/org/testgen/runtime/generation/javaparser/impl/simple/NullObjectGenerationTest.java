package org.testgen.runtime.generation.javaparser.impl.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testgen.runtime.generation.api.naming.NamingServiceProvider;
import org.testgen.runtime.generation.api.simple.SimpleObjectGeneration;
import org.testgen.runtime.valuetracker.blueprint.SimpleBluePrint;
import org.testgen.runtime.valuetracker.blueprint.simpletypes.NullBluePrint.NullBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class NullObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new NullObjectGeneration();

	private NullBluePrintFactory factory = new NullBluePrintFactory();

	@BeforeEach
	public void init() {
		simpleObjectGeneration.setImportCallBackHandler(imports::add);
	}

	@AfterEach
	public void cleanUp() {
		imports.clear();

		NamingServiceProvider.getNamingService().clearFields();
	}

	@Test
	public void testCreateField() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", null);

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		String expectedValueWithInit = "/**\r\n" + //
				" * TODO set correct Type for value\r\n" + //
				" */\r\n" + //
				"private Object value = null;";

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals(expectedValueWithInit, cu.getFields().get(0).toString());

		String expectedValueWithoutInit = "/**\r\n" + //
				" * TODO set correct Type for value\r\n" + //
				" */\r\n" + //
				"private Object value;";

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals(expectedValueWithoutInit, cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", null);

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);

		String expectedField = "// TODO set correct Type for value\r\n" //
				+ "this.value = null;";

		assertEquals(expectedField, block.getStatement(0).toString());

		String expectedLocal = "// TODO set correct Type for value\r\n" //
				+ "Object value = null;";

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals(expectedLocal, block.getStatement(2).toString());
	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", null);

		assertEquals("null", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}

}
