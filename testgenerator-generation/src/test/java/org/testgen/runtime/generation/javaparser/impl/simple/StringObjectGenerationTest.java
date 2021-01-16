package org.testgen.runtime.generation.javaparser.impl.simple;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testgen.runtime.generation.api.GenerationFactory;
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

	private SimpleObjectGeneration<ClassOrInterfaceDeclaration, BlockStmt, Expression> simpleObjectGeneration;

	private StringBluePrintFactory factory = new StringBluePrintFactory();

	@Before
	public void init() {
		GenerationFactory.<ClassOrInterfaceDeclaration, BlockStmt, Expression>getInstance()
				.setImportCallBackHandler(imports::add);

		simpleObjectGeneration = new StringObjectGeneration();
	}

	@After
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
		Assert.assertEquals("private String value = \"Word\";", cu.getFields().get(0).toString());

		simpleObjectGeneration.createField(cu, bluePrint, false);
		Assert.assertEquals("private String value;", cu.getFields().get(1).toString());
	}

	@Test
	public void testCreateObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", "foo");

		BlockStmt block = new BlockStmt();

		simpleObjectGeneration.createObject(block, bluePrint, true);
		Assert.assertEquals("this.value = \"foo\";", block.getStatement(0).toString());

		simpleObjectGeneration.createObject(block, bluePrint, false);
		Assert.assertEquals("String value = \"foo\";", block.getStatement(1).toString());
	}

	@Test
	public void testCreateInlineObject() {
		SimpleBluePrint<?> bluePrint = factory.createBluePrint("value", "This is a test");

		Assert.assertEquals("\"This is a test\"", simpleObjectGeneration.createInlineExpression(bluePrint).toString());
	}
}
