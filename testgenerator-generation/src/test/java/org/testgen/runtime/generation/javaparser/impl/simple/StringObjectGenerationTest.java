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
import org.testgen.runtime.valuetracker.blueprint.simpletypes.StringBluePrint.StringBluePrintFactory;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class StringObjectGenerationTest {

	private Set<Class<?>> imports = new HashSet<>();

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration = new StringObjectGeneration();

	private StringBluePrintFactory factory = new StringBluePrintFactory();

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
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", "Word");

		ClassOrInterfaceDeclaration cu = new ClassOrInterfaceDeclaration(Modifier.createModifierList(Keyword.PUBLIC),
				false, "Test");

		simpleObjectGeneration.createField(cu, bluePrint, true);
		assertEquals("private String value = \"Word\";", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		assertEquals("private String value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", "foo");

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		assertEquals("this.value = \"foo\";", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		assertEquals("String value = \"foo\";", block.getStatement(2).toString());
	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", "This is a test");

		assertEquals("\"This is a test\"", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}
}
